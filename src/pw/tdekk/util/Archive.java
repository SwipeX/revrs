package pw.tdekk.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by TimD on 6/21/2016.
 */
public class Archive {
    private final static ConcurrentHashMap<String, byte[]> resources = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ClassNode> build(JarFile file) {
        ConcurrentHashMap<String, ClassNode> classes = new ConcurrentHashMap<>();
        try {
            ArrayList<JarEntry> entries = Collections.list(file.entries());
            ConcurrentHashMap<String, InputStream> entryStreams = new ConcurrentHashMap<>(entries.size());
            for (JarEntry entry : entries) {
                entryStreams.put(entry.getName(), file.getInputStream(entry));
            }
            entryStreams.forEach(4, (name, input) -> {
                try {
                    if (name.endsWith(".class")) {
                        ClassNode cn = new ClassNode();
                        byte[] bytes = readInputStream(input);
                        ClassReader reader = new ClassReader(bytes);
                        reader.accept(cn, ClassReader.SKIP_FRAMES);
                        classes.put(name.replace(".class", ""), cn);
                    } else {
                        resources.put(name, readInputStream(input));
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    static byte[] readInputStream(InputStream in) throws IOException {
        try (ReadableByteChannel inChannel = Channels.newChannel(in)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (WritableByteChannel outChannel = Channels.newChannel(baos)) {
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while (inChannel.read(buffer) != -1) {
                    buffer.flip();
                    outChannel.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    outChannel.write(buffer);
                }
                return baos.toByteArray();
            }
        }
    }

    public static void write(File target, ConcurrentHashMap<String, ClassNode> classes) {
        try (JarOutputStream output = new JarOutputStream(new FileOutputStream(target))) {
            for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                entry.getValue().accept(writer);
                output.write(writer.toByteArray());
                output.closeEntry();
            }
//            for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
//                output.putNextEntry(new JarEntry(entry.getKey()));
//                output.write(entry.getValue());
//                output.closeEntry();
//            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
