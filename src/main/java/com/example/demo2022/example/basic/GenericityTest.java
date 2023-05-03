package com.example.demo2022.example.basic;

import org.junit.Test;

public class GenericityTest {

    //注意：Number并没有实现Comparable
    private static <T extends Number & Comparable<? super T>> T min(T[] values) {
        if (values == null || values.length == 0) return null;
        T min = values[0];
        for (int i = 1; i < values.length; i++) {
            if (min.compareTo(values[i]) > 0) min = values[i];
        }
        return min;
    }

    @Test
    public void  minTest(){
        int minInteger = min(new Integer[]{1, 2, 3});//result:1
        System.out.println(minInteger);
        double minDouble = min(new Double[]{1.2, 2.2, -1d});//result:-1d
        System.out.println(minDouble);
    }
}
