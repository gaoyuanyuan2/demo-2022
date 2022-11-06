package com.example.demo2022.java.base;

import java.lang.reflect.Field;

public class StringDemo {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        String str= "123";
        char[] chars = "456".toCharArray();
        Field valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(str,chars);
        System.out.println(str);
    }
}
