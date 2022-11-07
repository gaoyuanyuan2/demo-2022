package com.example.demo2022.java.base;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class GenericParameterTypeDemo {
    public static void main(String[] args) {
        //编译时:为什么不报错?
        // StringBuffer 是CharSequence 子类
        // String (参数) 也是CharSequence 子类
        // 运行时都是：Object
        Container<StringBuffer> c = new Container("Hello,World");
        //通过构造器传递参数是String 类型，运行时都是Object， 没有问题
        System.out.println(c.getElement()); // Hello , World
        //不过当C对象申明的类型为Container<StringBuffer>,
        // E 类型为 StringBuffer, 因此 set(E) , E必须是StringBuffer
        c.set(new StringBuffer("2019"));
        System.out.println(c.getElement()); //2019
        // Java 泛型对象操作时，看申明对象泛型参数类型
        forEach(Arrays.asList(1, 2, 3, 4, 5), System.out::println);
    }

    //把一个类型的元素，添加到集合中
    public static <C extends Collection<E>, E extends Serializable> void add(C target, E element) {
        target.add(element);
    }

    public static <C extends Iterable<E>, E extends Serializable> void forEach(C source, Consumer<E> consumer) {
        for (E e : source) {
            consumer.accept(e);
        }
    }

    public static class Container<E extends CharSequence> {
        private E element;

        public E getElement() {
            return element;
        }

        public void set(E element) {
            this.element = element;
        }

        public Container(E element) {
            this.element = element;
        }
    }
}
