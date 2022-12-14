# Networks Final Project 

## Task list: 

### 1 - 
```(DONE)```
Thoroughly read and understand the code provided (This task does not apply if you choose to implement this project in a different language) 

### 2 - 
```(DONE)```
Complete the main protocol loop in the run() method in RarReceiver and ParSender classes.

### 3 - 
```(DONE)```
Modify the send() method defined in the LossyChannel class so that you can control the loss rate dynamically. 
The reference implementation has a hard coded loss rate of 30%, i.e., 3 packets will be lost for every 10 packets sent. 
There are a number of ways you can use to convey the loss rate information dynamically (i.e., at runtime) to the program, e.g., 
    through command line, environment variables and Java Properties. 
    Please consult with the Java Tutorial for detail: http://java.sun.com/docs/books/tutorial/essential/environment/config.html

### 4 - 
```(DONE)```
Modify the getMessageToSend() method in ParSender class so that it can fetch packets to send from a file (ASCII file would be fine).

### 5 - 
```(DONE)```
Modify the deliverPacket() method in ParReceiver class so that it extracts the packet from  the frame received and write the payload into a file.

### 6 - 
```(DONE)```
The above two tasks enable the measurement of the performance of the PAR protocol. You need to instrument the ParSender class to measure 
the total time required to transmit a big file. If you choose to use an ASCII file, it should include at least 1,000 lines. You need to perform 
the latency measurement under the following configurations: 0 loss, 10% loss, 20%, and 30%. You can take the measurement on the same machine, or 
on two computers. The measurement result can be reported in a table or in a figure.
