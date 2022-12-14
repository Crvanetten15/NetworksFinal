
/*
 * Description given:
 * 
 */
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.*;
public class ParReceiver extends TransportLayer{
    public static final int RECEIVER_PORT = 9888;
    public static final int SENDER_PORT = 9887;

    public ParReceiver(LossyChannel lc) {
	super(lc);
    }

    public void run() {
		byte nextPacketExpected = 0;
		Packet packetReceived = new Packet();
		Packet packetToSend = new Packet();

		System.out.println("Ready to receive: ");

// ---------------------------------------------Task 2------------------------------------------------------------
		while(true) {
			// Waiting until event is ready 
			int event = 1;
			while(0 != event) {
				event = this.waitForEvent();
			}
			
			// Retrieving packet from lossy channel 
			packetReceived = this.receiveFromLossyChannel();

			// If packet is enter and start retrieving message
			if (packetReceived.isValid()) {
				// If packer is next enter and return the message is delivered, then increment
		    	if (packetReceived.seq == nextPacketExpected) {
					this.deliverMessage(packetReceived);
					nextPacketExpected = this.increment(nextPacketExpected);
			   	} 
				// If it reaches here we have not entered new packet potential loss
			   	else {
				  System.out.println("\nALERT: duplicate packet recieved\n");
			   	}
			}
			
			// setting the value of ack and length
			packetToSend.ack = (byte)(1 - nextPacketExpected);
			packetToSend.length = 0;

			// set up return packet 
			this.sendToLossyChannel(packetToSend);
		 }
// ---------------------------------------------Task 2------------------------------------------------------------
    }
    
    // ---------------------------------------------Task 5------------------------------------------------------------
    //
    // We simply extract the payload and display it as a string in stdout
    void deliverMessage(Packet packet) {
		byte[] payload = new byte[packet.length];
		for(int i=0; i<payload.length; i++){
			payload[i] = packet.payload[i];
		}
		String recvd = new String(payload);
		System.out.println("Received "+packet.length+" bytes: \n"
				+new String(payload)+ "\n");
		
		// Write the output to the file given
		WriteToFile(recvd);
    }
	// ---------------------------------------------Task 5------------------------------------------------------------

	// ---------------------------------------------Added Functions for File------------------------------------------------------------
	// Creation of a Write to File function
	public void WriteToFile(String arg){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("Output.txt", true));
			out.write(arg + "\n");
			out.close();
		}
		catch (IOException e) {
			System.out.println("exception occurred" + e);
		}
	}

	// Creation of the output file :: Also handles the removal and recreation of the file. 
	public void CreateFile(){
		try {
			File myObj = new File("Output.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} 
			else {
				System.out.println("File already exists. Remaking File");
				myObj.delete();
				CreateFile();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	// ---------------------------------------------Added Functions for File------------------------------------------------------------

	/*
	 * If experiencing an address already held type 'fg' then hit "ctrl+c", and repeat until 'fg: no current job is returned' 
	 */
    public static void main(String args[]) throws Exception { 
		LossyChannel lc = new LossyChannel(RECEIVER_PORT, SENDER_PORT);

		// Setting the desired loss with my made function in LossyChannel/setLoss from CLI (1 == 10%)
		if (args.length == 1){
			lc.setLoss(Integer.parseInt(args[0]));
			System.out.println("" + (Integer.parseInt(args[0])*10)+"% packet loss has been chosen");
		}
		else {
			System.out.println("30% packet loss is set as default");
		}

		ParReceiver receiver = new ParReceiver(lc);
		receiver.CreateFile();
		lc.setTransportLayer(receiver);
		receiver.run();
    } 
}  
