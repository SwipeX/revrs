package pw.tdekk.deob.cfg;

import java.util.*;

/**
 * @author Tim Dekker
 * Adapted from code by: Tyler Sedlar
 */
public class DirectedGraph<V, E> implements Iterable<V> {

    private final Map<V, Set<E>> graph = new HashMap<>();

    @SuppressWarnings("unchecked")
    public Set<E> getEdgeAt(int index) {
        return (Set<E>) graph.values().toArray()[index];
    }

    public int getSize() {
        return graph.size();
    }

    public boolean containsVertex(V vertex) {
        return graph.containsKey(vertex);
    }

    public boolean containsEdge(V vertex, E edge) {
        return graph.containsKey(vertex) && graph.get(vertex).contains(edge);
    }

    public void removeVertex(V vertex) {
        graph.remove(vertex);
    }

    public boolean addVertex(V vertex) {
        if (graph.containsKey(vertex)) {
            return false;
        }
        graph.put(vertex, new HashSet<>());
        return true;
    }

    public void addEdge(V start, E dest) {
        if (!graph.containsKey(start)) {
            return;
        }
        graph.get(start).add(dest);
    }

    public void removeEdge(V start, E dest) {
        if (!graph.containsKey(start)) {
            return;
        }
        graph.get(start).remove(dest);
    }

    public Set<E> edgesFrom(V node) {
        return graph.get(node);
    }

    public Set<V> edgesTo(E edge) {
        final Set set = new HashSet();
        graph.entrySet().forEach(entry -> {
            if (entry.getValue().stream().filter(val -> val.equals(edge)).toArray().length > 0)
                set.add(entry.getKey());
        });
        return set;
    }

    @Override
    public final Iterator<V> iterator() {
        return graph.keySet().iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (V v : graph.keySet()) {
            builder.append("\n    ").append(v).append(" -> ").append(graph.get(v));
        }
        return builder.toString();
    }

    public void flush() {
        graph.clear();
    }
}