package com.machntek.reactive.live;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {

    // 옵저버 패턴에 없는것
    // 1. Complete????
    // 2. Error???

    // Iterable <---> Observable (duality) 쌍대성
    // Pull           Push

    /**
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

     for (Integer integer : iter) {      // for-each
     System.out.println(integer);
     }
     */

    // 데이터만드는 소스쪽
    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <=10; i++) {
                setChanged();
                notifyObservers(i);     // push
                // int i = it.next();   // pull
            }
        }
    }
    // DATA method(void) <-> void method(DATA)

    public static void main(String[] args) {

        // 데이터 받는쪽
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExecutorService es = Executors.newSingleThreadExecutor();

        es.execute(io);

        System.out.println(Thread.currentThread().getName() + "  EXIT");
        es.shutdown();
    }
}
