import java.io.*;
import java.net.*;
import java.util.regex.Pattern;


public class ChatClient extends Thread {
	
	private Socket socket;
	private String nick;
	
	
	ChatClient(Socket socket, String nick){
		this.socket = socket;
		this.nick = nick;
	}
	
	@Override
	public void run(){
		
		try{
			
			// out-/input stream of the Client and the standard system input stream
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			InputStream inStream = socket.getInputStream();
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			String userInput;
			
			// reading the messages committed in command line
			while ((userInput = stdIn.readLine()) != null) {
				
				// free resources after typing in "quit"
			    if(userInput.equals("quit")){
			    	inStream.close();
			    	stdIn.close();
			    	out.close();
			    	socket.close();
			    	break;
			    }
			    
			    // Client sends the input message with the nick in front
			    out.println(nick + ": " + userInput);
			    			    
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) throws IOException{
		
		// standard values
		String nick = "";
		String host = "localhost";
		int port = 5678;
		
		try{
					
			if(args.length == 0){
				System.out.println("Bitte geben Sie einen Nickname an!");
				return;
			}
			else if(args.length == 1){				
				nick = args[0];				
			}	
			else if(args.length == 2){
					nick = args[0];
					
					// second argument committed by the command line is split at the colon sign and saved in a String array
					String[] splitArray = (args[1].split(Pattern.quote(":")));
					
					if(args[1].contains(":")){
						if(splitArray.length == 2){
							if(splitArray[0].isEmpty()){
								port = Integer.parseInt(splitArray[1]);
							}
							else{
								host = splitArray[0];
								port = Integer.parseInt(splitArray[1]);							
							}					
						}
					}
					
			}
			else{
				System.err.println("Fehlerhafte Eingabe. Starten des Clients mit:\njava ChatClient <nick> [<host>]:[<port>]");
				return;
			}
		
		
				// building new connection to committed host and port
				Socket client = new Socket(host, port);
				
				// starting a thread for a new Client
				new ChatClient(client,nick).start();
			
				
				BufferedReader clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
							
				String incoming;
				
				// prints the data which is sent from other Clients on the own screen
				while (!(incoming = clientInput.readLine()).equals("quit")) {
				
					System.out.println(incoming);
			    
				}
				
				// terminates Client if "quit" is typed in
				clientInput.close();
				client.close();
				

		}
		catch(NumberFormatException e){
			System.err.println("Fehlerhafte Eingabe! Port muss eine ganze Zahl darstellen!\nStarten des Clients mit java ChatClient <nick> [<host>]:[<port>]");
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
		
}


	
	
