Create threads that can take jobs from common queue...process it and print it to output queue as they are getting done with processing<br><br>

 What kind of options we have to implement a queue ??<br><br>

 A good explanation is present here: http://tutorials.jenkov.com/java-util-concurrent/index.html<br><br>

 <b>1. ArrayBlockingQueue</b>, size bounded queue. No need to use synchronize block while put/take operations<br>
        <pre>
        BlockingQueue queue = new ArrayBlockingQueue(1024);
        queue.put("1");
        Object object = queue.take();<br><
</pre>
 <b>2. LinkedBlockingQueue</b>, content stored as link list. Can be bounded and UnBounded<br>
        <pre>
        BlockingQueue<String> unbounded = new LinkedBlockingQueue<String>();
        bounded.put("Value");
        String value = bounded.take();
</pre>
<b>3. PriorityBlockingQueue</b>, unbounded concurrent queue. <br>Have to use Comparable implementation to check priorities<br>
        <pre>
        BlockingQueue queue   = new PriorityBlockingQueue();
        queue.put("Value");
        String value = queue.take();
</pre>

<b>4. SynchronousQueue</b>, one element queue. Thread inserting/adding gets blocked on full/empty states<br>
        Good For Demo. But Lets leave it here<br><br>

<b>5. BlockingDeque (Double Ended Queue)</b>, blocks thread to insert/remove elements.<br><br>

<pre>
        <b>ArrayBlockingQueue vs LinkedBlockingDeque</b>

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
</pre>

        LinkedBlockingQueque FITS the Bill for my problem here.<br><br>

        queue.take() blocks you on empty data. so make sure to check for size before doing take() else u cannot abort out of printing thread.<br><br>

LOGIC:<br>
     1. Create a JOB Processing thread with fixed batch size as input.<br>
     2. Start instances of this JOB Processing thread<br>
     3. Also create a printing thread. It runs as long as it is aborted using a <b>AtomicBoolean</b>
     <br><br>
     4. use future.get() to wait for all the executors to complete.<br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Each get() now becomes a blocking call and now we go sequential at each iteration
        <br><br>
     5. Once all future's are done, set AtomicBoolean and force abort the printing thread.<br>
