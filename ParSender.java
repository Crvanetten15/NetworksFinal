/*
 * Description given:
 * 
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner; 
  
public class ParSender extends TransportLayer{ 
    public static final int RECEIVER_PORT = 9888;
    public static final int SENDER_PORT = 9887;
	// adding variables needed to make this work for a file 
	public static File text_message = null;
	public static ArrayList<String> file_list = new ArrayList<String>();
	public static int count = 0; 
	public static long StartTime; 
	public static long EndTime; 


    public ParSender(LossyChannel lc) {
		super(lc);
    }

    public void run() {
		byte nextPacketToSend = 0;
		Packet packet = new Packet();
		byte[] msgToSend = getMessageToSend();

		if(null == msgToSend)
			return;
// ---------------------------------------------Task 2------------------------------------------------------------	
		while(true) {
			
			// Set the event to 1 to ensure enterance
			int event = 1;
            while(0 != event){
				// Set payload, length, and SEQ # 
				packet.payload = msgToSend;
				packet.length = msgToSend.length;
				packet.seq = nextPacketToSend;

               	this.sendToLossyChannel(packet);
               	this.m_wakeup = false;
               	// Start time in case of loss
				this.startTimer();
               	
				//Again wait til event returns 0 for a successfule send
				event = this.waitForEvent();
            }

			// Retrieved packet from the lossy channel
            packet = this.receiveFromLossyChannel();
			
			// Checking if it is a working packet
            if (packet.isValid()) {
				// check ack to see if it is the next Packet 
            	if (packet.ack == nextPacketToSend) {
                	//if so stop timer as we dont need to send a dup
					this.stopTimer();

					// retrieve next message to send 
                	msgToSend = this.getMessageToSend();
                	if (null == msgToSend) {return;}

					// Increment to next to send the packet
					nextPacketToSend = this.increment(nextPacketToSend);
               	} 
				else {
					System.out.println("\nALERT: duplicate packet recieved\n");
				}
            }
		}
// ---------------------------------------------Task 2------------------------------------------------------------
    }
    

    // TODO task#4
    //
    // This is the location for getting our message and converting it to a byte array 
    byte[] getMessageToSend() {

		if (text_message != null && count < file_list.size()){
			System.out.println("Sending: "+file_list.get(count)+ " " + count);
			String temp = file_list.get(count);
			count++;
			return temp.getBytes(); 
		} 
		if (count == file_list.size()) {
			EndTime = System.nanoTime();
			count++;

			System.out.printf("\nTime of Program : %f seconds\n\n", ((EndTime-StartTime) * 1e-9));
		}
		
		System.out.println("Please enter a message to send:");

		try {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
			String sentence = inFromUser.readLine(); 
			if(null == sentence) {System.exit(1);}
			System.out.println("Sending: "+sentence);
			
			return sentence.getBytes();         
		} catch(Exception e) {
			System.out.println("IO error: "+e);
			return null;
		}

    }


	/*
	 * If experiencing an address already held type 'fg' then hit "ctrl+c", and repeat until 'fg: no current job is returned' 
	 */
    public static void main(String args[]) throws Exception { 
		LossyChannel lc = new LossyChannel(SENDER_PORT, RECEIVER_PORT);

		// Setting the desired loss with my made function in LossyChannel/setLoss from CLI (1 == 10%)
		if (args.length == 1){
			lc.setLoss(Integer.parseInt(args[0]));
			System.out.println("" + (Integer.parseInt(args[0])*10)+"% packet loss has been chosen");

		}
		else if (args.length == 2){
			lc.setLoss(Integer.parseInt(args[0]));
			System.out.println("" + (Integer.parseInt(args[0])*10)+"% packet loss has been chosen");

			text_message = new File(args[1]);
			System.out.println("A file was entered in CLI");
			Scanner scanner;
			try {
				scanner = new Scanner(text_message);			
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					file_list.add(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("30% packet loss is set as default");
		}
		// System.out.println(file_list.get(1));
		ParSender sender = new ParSender(lc);
		lc.setTransportLayer(sender);
		StartTime = System.nanoTime();
		sender.run();
    } 
} 
