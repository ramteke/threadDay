package DinningPhilosophers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by skynet on 13/04/17.
 */
class ChopStick {
    final int id;
    ReentrantLock lock = new ReentrantLock();  //This will not block on failure

    ChopStick(int id) {
        this.id = id;
    }

    public boolean pickUp(String ownerName) throws java.lang.InterruptedException {
        if ( lock.tryLock(1, TimeUnit.SECONDS)) { //Lets wait for 1 sec. here
            System.out.println(ownerName + " Pickup " + this.id);
        } else {
            return false;
        }
        return true;
    }

    public void putDown(String ownerName) throws java.lang.InterruptedException {
        System.out.println(ownerName + " kept down " + this.id);
        lock.unlock();
    }
}

class Philosopher extends Thread {
    final ChopStick left;
    final ChopStick right;
    final String name;

    public Philosopher(String name, ChopStick left, ChopStick right) {
        this.left = left;
        this.right = right;
        this.name = name;
    }

    public void eat() throws java.lang.InterruptedException {
        if ( left.pickUp(name)) {
            if ( right.pickUp(name)) {
                System.out.println(name + " got to EAT !!");
                Thread.sleep(200);
                right.putDown(name);
                left.putDown(name);
            }
        }
    }

    public void run() {
        while ( true ) {
            try {
                eat();
                Thread.sleep(1000); //Digest
            } catch (java.lang.InterruptedException e) {
                System.out.println("Philosphoer was Interrupted !!. " + e);
                break;
            }
        }
    }

}

public class Main {

    public static void main(String args[]) throws Exception  {
        //lets create eating items first
        int MAX = 6; //6 will allow 5 to sit in circle

        ChopStick [] chopSticks = new ChopStick [MAX];
        for (int i = 0; i < chopSticks.length; i++) {
            ChopStick chopStick = new ChopStick(i);
            chopSticks[i] = chopStick;
        }


        Philosopher [] philosophers = new Philosopher[MAX - 1];
        for (int i = 0; i < MAX - 1; i++ ) {
            int leftChopstick = i;
            int rightChopstick = (i + 1) % MAX;
            Philosopher philosopher = new Philosopher("Philosopher" + i, chopSticks[leftChopstick], chopSticks[rightChopstick]);
            philosophers[i] = philosopher;
        }

        //Now that all are created..Start the process !!
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < MAX - 1; i++) {
            service.execute(philosophers[i]);
        }

        //Force abort this one u are done analyzing the output

    }
}

/***
 * --- Sample Output ---------
 * Philosopher0 Pickup 0
 Philosopher1 Pickup 1
 Philosopher1 Pickup 2
 Philosopher1 got to EAT !!
 Philosopher3 Pickup 3
 Philosopher3 Pickup 4
 Philosopher3 got to EAT !!
 Philosopher1 kept down 2
 Philosopher3 kept down 4
 Philosopher1 kept down 1
 Philosopher4 Pickup 4
 Philosopher2 Pickup 2
 Philosopher3 kept down 3
 Philosopher4 Pickup 5
 Philosopher4 got to EAT !!
 Philosopher2 Pickup 3
 Philosopher2 got to EAT !!
 Philosopher0 Pickup 1
 Philosopher0 got to EAT !!
 */
