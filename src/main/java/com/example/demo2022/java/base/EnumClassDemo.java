package com.example.demo2022.java.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumClassDemo {
    public static void main(String[] args) {
       Stream.of(Counting.values()).forEach(System.out::println );
    }
}

final class Counting {
    public static final Counting ONE = new Counting(1);
    public static final Counting TWO = new Counting(2);
    public static final Counting THREE = new Counting(3);
    public static final Counting FOUR = new Counting(4);
    public static final Counting FIVE = new Counting(5);

    public int getValue() {
        return value;
    }

    private Counting(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Counting:" + value;
    }

    private int value;

    public static Counting[] values() {
        Field[] fields = Counting.class.getDeclaredFields();

        return Stream.of(fields).filter(field -> {
            int modifiers = field.getModifiers();
            return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
        }).map(field -> {
            try {
                return field.get("null");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()).toArray(new Counting[0]);
    }

}