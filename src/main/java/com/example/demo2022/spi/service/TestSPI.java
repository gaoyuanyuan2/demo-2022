package com.example.demo2022.spi.service;

import com.example.demo2022.spi.Search;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * https://zhuanlan.zhihu.com/p/28909673
 */
public class TestSPI {
    public static void main(String[] args) {
        ServiceLoader<Search> s = ServiceLoader.load(Search.class);
        Iterator<Search> iterator = s.iterator();
        while (iterator.hasNext()) {
            Search search =  iterator.next();
            search.searchDoc("hello world");
        }
    }
}
