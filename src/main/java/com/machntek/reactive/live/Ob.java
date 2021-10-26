package com.machntek.reactive.live;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Ob {
    // Iterable <---> Observable (duality) 쌍대성
    // Pull           Push
    public static void main(String[] args) {

        Iterable<Integer> iter = () ->
            new Iterator<>() {
                int i = 0;
                final static int MAX = 10;

                public boolean hasNext() {
                    return i < MAX;
                }

                public Integer next() {
                    return ++i;
                }
            };

//        Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        for (Integer integer : iter) {      // for-each
            System.out.println(integer);
        }
    }
}
