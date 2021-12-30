package me.htrewrite.exeterimports.mcapi.manager;

public interface Manager<T> {
    void register(T object);

    void unregister(T object);

    int size();
}