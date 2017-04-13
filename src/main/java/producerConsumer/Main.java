package producerConsumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by skynet on 13/04/17.
 */

class Producer extends Thread {
    List<String> array = new ArrayList<String>();

    int MAX_SIZE = 10;

    public Producer(List<String> array) {
        this.array = array;
    }

    public void run() {
        while ( true ) {
            synchronized (array) {
                if ( array.size() == MAX_SIZE) {
                    try {
                        array.wait();    // Bag is full..lets wait for some space to clean up
                    } catch (java.lang.InterruptedException e) {
                        System.out.println("Producer got interrupted while waiting !!!. " + e);
                        break;
                    }
                } else {
                    String data = new Date().toString();
                    System.out.println("Added :  " + data);
                    array.add(data);
                    array.notifyAll();   //Allow consumer to consume this new item
                    try {
                        Thread.sleep(100); //Lets wait here and allow consumer to take items
                    } catch (java.lang.InterruptedException e) {
                        System.out.println("Producer got interrupted while sleeping !!!.  " + e);
                        break;
                    }
                }
            }
        }
    }
}


class Consumer extends Thread {
    List<String> array = new ArrayList<String>();

    public Consumer(List<String> array) {
        this.array = array;
    }

    public void run() {
        while ( true ) {
            synchronized (array) {
                if ( array.size() == 0) {
                    try {
                        array.wait(); //Nothing to consume..lets wait here for new items to get in
                    } catch (java.lang.InterruptedException e) {
                        System.out.println("Consumer got interrupted while waiting. " + e);
                        break;
                    }
                } else {
                    System.out.println("Consuming: " + array.get(0));
                    array.remove(0);
                    array.notifyAll(); //Allow producer to add more
                }
            }
        }
    }
}

public class Main {

    public static void main(String args [] ) {
        List<String> array = new ArrayList<String>();
        Producer producer = new Producer(array);
        Consumer consumer = new Consumer(array);
        producer.start();
        consumer.start();
    }

}


/*
-----------------------   Sample Output ----------------------------

Added :  Thu Apr 13 19:07:21 IST 2017
Added :  Thu Apr 13 19:07:21 IST 2017
Consuming: Thu Apr 13 19:07:21 IST 2017
Consuming: Thu Apr 13 19:07:21 IST 2017
Added :  Thu Apr 13 19:07:21 IST 2017
Added :  Thu Apr 13 19:07:21 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:21 IST 2017
Consuming: Thu Apr 13 19:07:21 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Consuming: Thu Apr 13 19:07:22 IST 2017
Added :  Thu Apr 13 19:07:23 IST 2017
Added :  Thu Apr 13 19:07:24 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:23 IST 2017
Consuming: Thu Apr 13 19:07:24 IST 2017
Added :  Thu Apr 13 19:07:24 IST 2017
Added :  Thu Apr 13 19:07:24 IST 2017
Added :  Thu Apr 13 19:07:24 IST 2017
Added :  Thu Apr 13 19:07:24 IST 2017

Process finished with exit code 130 (interrupted by signal 2: SIGINT)
 */
