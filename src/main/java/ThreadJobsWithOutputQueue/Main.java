package ThreadJobsWithOutputQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by skynet on 13/04/17.
 */

/*
 Create threads that can take jobs from common queue...process it and print it to output queue as they are getting done with processing
 What kind of options we have to implement a queue.
 A good explanation is present here: http://tutorials.jenkov.com/java-util-concurrent/index.html

 1. ArrayBlockingQueue, size bounded queue. No need to use synchronize block while put/take operations
        BlockingQueue queue = new ArrayBlockingQueue(1024);
        queue.put("1");
        Object object = queue.take();

 2. LinkedBlockingQueue, content stored as link list. Can be bounded and UnBounded
        BlockingQueue<String> unbounded = new LinkedBlockingQueue<String>();
        bounded.put("Value");
        String value = bounded.take();

3. PriorityBlockingQueue, unbounded concurrent queue. Have to use Comparable implementation to check priorities
        BlockingQueue queue   = new PriorityBlockingQueue();
        queue.put("Value");
        String value = queue.take();

4. SynchronousQueue, one element queue. Thread inserting/adding gets blocked on full/empty states
        Good For Demo. But Lets leave it here

5. BlockingDeque ("Double Ended Queue"), blocks thread to insert/remove elements.

        BlockingDeque<String> deque = new LinkedBlockingDeque<String>();

        deque.addFirst("1");
        deque.addLast("2");

        String two = deque.takeLast();
        String one = deque.takeFirst();


        LinkedBlockingDeque FITS the Bill for my problem here.

        queue.take() blocks you on empty data. so make sure to check for size before doing take() else u cannot abort out of printing thread.

LOGIC:
     1. Create a JOB Processing thread with fixed batch size as input.
     2. Start instances of this JOB Processing thread
     3. Also create a printing thread. It runs as long as it is aborted using a AtomicBoolean

     4. use future.get() to wait for all the executors to complete. Each get() now becomes a blocking call and now we go sequential at each iteration
     5. Once all future's are done, set AtomicBoolean and force abort the printing thread.

*/


class JobProcessor implements Callable<String> {
    final int id;
    final BlockingDeque<String> outputQueue;
    final String pattern;
    final List<String> inputDataSet;

    public JobProcessor(int id, BlockingDeque<String> queue, List<String> inputDataSet, String pattern) {
        this.id = id;
        this.outputQueue = queue;
        this.pattern = pattern;
        this.inputDataSet = inputDataSet;
        System.out.println(this.id + "] Starting JOB with Load Size: " + inputDataSet.size());
    }

    public String call() throws java.lang.InterruptedException {
        try {
            for (String str : inputDataSet) {
                if (str.contains(pattern)) {
                    outputQueue.put(str);
                }
            }
        } catch (java.lang.InterruptedException e) {
            return "ABORTED";
        }
        System.out.println(this.id + "] Finished JOB");
        return "DONE";
    }
}

class PrinterProcess implements Runnable {
    final BlockingDeque<String> outputQueue;
    AtomicBoolean abort;

    public PrinterProcess(BlockingDeque<String> queue, AtomicBoolean atomicBoolean) {
        this.outputQueue = queue;
        this.abort = atomicBoolean;
    }

    public void run() {
        while (true) {
            try {
                if ( abort.get() ) {
                    System.out.println("------------------------------------------------------------------------");
                    break;
                }

                if (outputQueue.size() > 0) {           //Blocking Safety.
                    String outStr = outputQueue.take();
                    System.out.println(">> " + outStr);
                }

            } catch (java.lang.InterruptedException e) {
                System.out.println("Printer Process interrupted..");
                break;
            }
        }
    }


}

public class Main {
    final static int limit = 100;

    private static List<String> createDataSet() {


        List<String> arrayList = new ArrayList<>();
        String runningString = "";
        for (int  i = 0; i < limit; i++) {
            runningString = runningString + i;
            arrayList.add(runningString);
        }
        return arrayList;
    }

    public static void main(String args[]) throws Exception {
        List<String> inputData = createDataSet();

        //Lets divide load among some 5 threads and wait for them to compute the result..We are searching for some pattern 1213
        String patten = "1213";

        int batchSize = 20;
        int threadSize = limit / batchSize;
        System.out.println(" Will be creating " + threadSize + " parallel jobs");

        ExecutorService service = Executors.newCachedThreadPool();
        BlockingDeque<String> outputQueue = new LinkedBlockingDeque<String>();
        List<Future> allFutures = new ArrayList<Future>();

        //Start printing process
        AtomicBoolean abortFlag = new AtomicBoolean(false);
        PrinterProcess printerProcess = new PrinterProcess(outputQueue, abortFlag);
        service.execute(printerProcess);


        int incr = 0;
        for (int i = 0; i < threadSize; i++) {
            List<String> batchJob = inputData.subList(incr, incr + batchSize);
            incr = incr + batchSize;
            Future<String> future = service.submit(new JobProcessor(i,outputQueue, batchJob, patten));
            allFutures.add(future);
        }

        Thread.sleep(1000);  //Wait for 1 sec.

        for ( Future future : allFutures) {
            String status = (String) future.get();
            if ( status == null || !status.contains("DONE") ) {
                System.out.println("Thread Failure to completed. Aborted in middle");
            }
        }
        System.out.println("All Futures Completed. We will try to stop printing thread.");

        abortFlag.set(true);

        service.shutdown();

    }



}
