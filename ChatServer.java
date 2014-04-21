import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChatServer extends Thread {
	
	private Socket socket;
	private boolean debugOn;
	private CopyOnWriteArrayList<Socket> clientList;
	
	public ChatServer(Socket socket, boolean debugOn, CopyOnWriteArrayList<Socket> clientList){
		this.socket = socket;
		this.debugOn = debugOn;
		this.clientList = clientList;
	}
	
	@Override
	public void run(){
		
		try{
			
			// input stream of the Client
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	       
	        String inputData;
			
	        // reads the sent messages from the Client until the stream ends because of "quit" typing in
	        while((inputData = in.readLine()) != null){	        		        	
	        	
	        	// prints the typed-in message on the server screen when "-debug" is set
	        	if(debugOn == true){
	        		System.out.println(inputData); 
	        	}
	        	
	        	// sends the message from one Client to all other Clients
	        	sendToAll(inputData);
	        		        	
	        }
	        
	        // according to inserting "quit" the Client is removed from the list of connected Clients
	        clientList.remove(socket);
	       	        
	    }
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public synchronized void sendToAll(String inputData) throws IOException{
			
			// sends the inserted message from one Client to all other connected Clients
			for(int i = 0; i < clientList.size(); i++){
				if(clientList.get(i) != socket){
					PrintWriter output = new PrintWriter(clientList.get(i).getOutputStream(), true);
					output.println(inputData);
				}
			}
	}
	
	
	public static void main(String[] args){
		
		// standard values
		boolean debugOn = false;
		int port = 5678;
		CopyOnWriteArrayList<Socket> clientList = new CopyOnWriteArrayList<Socket>();
		 
		try{
			
		 if(args.length == 1){
			 if(args[0].equals("-debug")){
				 debugOn = true;
			 }
			 else{
				 port = Integer.parseInt(args[0]);
			 }
		 }
		 
		 if(args.length == 2){
			 if(args[0].equals("-debug")){
				 debugOn = true;
				 port = Integer.parseInt(args[1]);
			 }
			 else{
				 port = Integer.parseInt(args[1]);
			 }
		 }
		 
		 	 // starts new Server
			 ServerSocket server = new ServerSocket(port);
			 
			 // waiting continuously for new connections from the Client, starting new thread for each Client and
			 // add the Client to the list of connected Clients
			 while(true){
				 Socket client = server.accept();
				 (new ChatServer(client, debugOn, clientList)).start();
				 clientList.add(client);
			 }
		 }
		catch(NumberFormatException e){
			System.err.println("Fehlerhafte Eingabe! Port muss eine ganze Zahl darstellen!\nStarten des Servers mit java ChatServer [-debug] [<port>]");
		}
		 catch(IOException e){
			 e.printStackTrace();
		 }


		
	}
}
	
	
	
	
	

