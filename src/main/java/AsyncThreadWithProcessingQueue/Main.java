package AsyncThreadWithProcessingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by skynet on 18/04/18.
 */

class AsynProcessor implements Runnable {
    final BlockingQueue<Object> blockingQueue;
    AtomicBoolean abortFlag = null;

    public AsynProcessor(BlockingQueue<Object> objectBlockingQueue, AtomicBoolean atomicBoolean) {
        this.blockingQueue = objectBlockingQueue;
        this.abortFlag = atomicBoolean;
    }

    public void run() {
        while ( true ) {
            try {
                if (abortFlag.get()) {   //User initiated abort...so we will exit this thread
                    break;
                }

                //Process what is present in queue
                if (blockingQueue.size() > 0) {  //if 0 we get blocked.
                    Object obj = blockingQueue.take();     //Remove one element from queue
                    System.out.println("Working on Job: " + ((String)obj));
                    Thread.sleep(10);

                }
            } catch (InterruptedException e) {
                System.out.println("This thread is interrupted...Time to abort");
                break;
            }

        }
        System.out.println("Exiting Async Job Processor");

    }
}

public class Main {
    public static void main(String args[]) throws Exception {
        BlockingQueue<Object> jobQueue = new LinkedBlockingQueue<Object>();  //This queue holds all jobs we need to work on
        AtomicBoolean abortFlag = new AtomicBoolean(false);                  //Flag to kill our asyn. thread
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(10);    //We go with 10 threads for our op
            AsynProcessor asyncProcessor = new AsynProcessor(jobQueue, abortFlag);
            service.execute(asyncProcessor);


            int jobCount = 1;
            while (jobCount < 100) {   //Keep it running as long as system allows you to run...or here we stop at 100 jobs

                //For testing Assuming every interval you have some 10 objects available
                for (int index = 0; index < 10; index++) {
                    jobQueue.add(new String("Job-") + (jobCount++));
                }

            }

        } finally {
            Thread.sleep(10000);
            System.out.println("Initiating abort to our job processor..");
            abortFlag.set(true);
            service.shutdown();
        }



    }

}
