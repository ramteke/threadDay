
##Use case: 

User queries for data periodically and processes it. In a sequential operation, due to load of the data, the periodicity of poll might get affected if the thread get stuck in process operations. Ex. if you have to poll every 1 min and the thread is stuck in processing for more then 1 min, then next poll will miss data as exact 1 min polling boundry is now delayed. 

Solution: Create a asyn thread and give it a queue. As you poll, add your polled data to this queue and let a async. thread work on it in background.


##Implementation
Simple Async thread which performs operations on the blocking queue. 

The thread is aborted using atomic boolean


