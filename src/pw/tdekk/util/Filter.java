package pw.tdekk.util;

public interface Filter<E> {

    public boolean accept(E e);
}