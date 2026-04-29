package com.project.downloadmanager.util.iterator;


public interface Aggregate<T> {
    public void add(T t);
    public void remove(T t);
    public Iterator<T> createIterator();
}
