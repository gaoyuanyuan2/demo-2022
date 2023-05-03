package com.example.demo2022.example.interview;

import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InterviewTest {

    @Test
    public void testShortSet() {
        Set<Short> s = new HashSet<>();
        //byte short int ->int
        for (short i = 0; i < 100; i++) {
            s.add(i);//0 ... 99
            s.remove(i - 1);//short - integer = integer
        }
        System.out.println("size:" + s.size());//100
    }

    @Test
    public void testAbs() {
        System.out.println(Math.abs(Integer.MIN_VALUE));
    }

    @Test
    public void testExec() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("calc");
        process.exitValue();
    }


    @Test
    public void testExec2() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("cmd /k start http://www.baidu.com");
        process.exitValue();
    }

}
