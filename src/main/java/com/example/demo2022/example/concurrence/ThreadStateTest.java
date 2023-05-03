package com.example.demo2022.example.concurrence;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ThreadStateTest {

    @Test
    public void test(){
        List<Integer> list = Arrays.asList(1,2,3,4,5);
//
//        //java 9
//        Set<Integer> set = Set.of(1, 2, 3, 4, 5);
//
//        Map<Integer, String> map = Map.of(1, "A");
//
//        //以上实现都是不变对象，不过第一个除外
//
//        list = Collections.synchronizedList(list);
//        set = Collections.synchronizedSet(set);
//        map = Collections.synchronizedMap(map);
//
//        list = new CopyOnWriteArrayList<>(list);
//        set = new CopyOnWriteArraySet<>(set);
//        map = new ConcurrentHashMap<>(map);
    }
}
