Create threads that can take jobs from common queue...process it and print it to output queue as they are getting done with processing

 What kind of options we have to implement a queue ??

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

        ArrayBlockingQueue vs LinkedBlockingDeque
        :: Source:https://stackoverflow.com/questions/18375334/what-is-the-difference-between-arrayblockingqueue-and-linkedblockingqueue

            ArrayBlockingQueue is backed by an array that size will never change after creation.
               Setting the capacity to Integer.MAX_VALUE would create a big array with high costs in space.
               ArrayBlockingQueue is always bounded.

            LinkedBlockingQueue creates nodes dynamically until the capacity is reached (Integer.MAX_VALUE)
            LinkedBlockingQueue is optionally bounded.


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
