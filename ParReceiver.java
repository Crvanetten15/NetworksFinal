
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

	private String message;

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
			// Abitrary number to guarentee enterance into the while loop
			int event = 1;
			while(0 != event) {
				event = this.waitForEvent();
			}
   
			packetReceived = this.receiveFromLossyChannel();
			if (packetReceived.isValid()) {
			   if (packetReceived.seq == nextPacketExpected) {
				  this.deliverMessage(packetReceived);
				  nextPacketExpected = this.increment(nextPacketExpected);
			   } else {
				  System.out.println("\nalert: duplicate packet...\n");
			   }
			}
   
			packetToSend.ack = (byte)(1 - nextPacketExpected);
			packetToSend.length = 0;
			this.sendToLossyChannel(packetToSend);
		 }
// ---------------------------------------------Task 2------------------------------------------------------------
    }
    
    // TODO task#5
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
		WriteToFile(recvd);
    }
	
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
			} else {
			  System.out.println("File already exists. Remaking File");
			  myObj.delete();
			  CreateFile();
			}
		  } catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		  }
	}
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
