package com.example.demo2022.java.collection.algorithm;

public interface Sort<T extends Comparable<T>> {

    void sort(T[] values);


    static <T> T[] of(T... values) {
        return values;
    }
}