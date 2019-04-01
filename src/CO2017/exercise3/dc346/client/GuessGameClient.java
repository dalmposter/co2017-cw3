package CO2017.exercise3.dc346.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import CO2017.exercise3.dc346.server.GameState;

public class GuessGameClient
{	
	public static void main(String[] args) throws IOException
	{
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		
		BufferedReader in = null;
		PrintWriter out = null;
		Socket server = null;
		
		//Get streams to the server
		server = new Socket(hostname, port);
		in = new BufferedReader(new InputStreamReader(server.getInputStream(), "UTF-8"));
		out = new PrintWriter(server.getOutputStream(), true);
		//Make a reader to get input from the client console
		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
		
		//Get and format the message from the server to be printed to client console
		String[] received = in.readLine().split("\\:");
		System.out.printf("New guessing game. Range is %s..%s. Time limit is %ss%n",
				GameState.MINVAL,
				received[1],
				toSeconds(Long.parseLong(received[2])));
		
		//Flag to decide whether to print 'Enter a guess' at the start of the loop
		//(If readLineTimout didn't time out)
		boolean newInput = true;
		//Loop to repeatedly read and send guesses
		boolean serviceover = false;
		while(!serviceover)
		{
			if(newInput) System.out.print("Enter a guess: ");
			
			//Send users guess to server
			try
			{
				String userEntered = readLineTimeout(consoleInput, 500);
				newInput = true;
				out.printf("%s%n", userEntered);
				
				//Interpret response from server
				received = in.readLine().split("\\:");
				
				//If the guess was valid but incorrect
				if(received[0].equals("HIGH") || received[0].equals("LOW"))
				{
					System.out.printf("Turn %s: %s was %s, %ss remaining%n",
							received[2],
							userEntered,
							received[0],
							toSeconds(Long.parseLong(received[1])));
				}
				//If the guess was invalid
				else if(received[0].equals("ERR"))
				{
					System.out.printf("ERROR: Turn %s: %ss remaining%n",
							received[2],
							toSeconds(Long.parseLong(received[1])));
				}
				//If the game is over
				else
				{
					System.out.printf("Turn %s: target was %s - %s%n",
							received[1],
							received[2],
							received[0]);
					serviceover = true;
				}
			}
			//Reading input from user timed out
			catch (TimeoutException e)
			{
				newInput = false;
				//Check if the game has ended (the server sent us a message when we didn't send it a guess)
				if(in.ready())
				{
					received = in.readLine().split("\\:");
					//Extra new line at the start as if we reached this clause, the game ended while the user was inputting
					System.out.printf("%nTurn %s: target was %s - %s%n",
							received[1],
							received[2],
							received[0]);
					serviceover = true;
				}
			}
		}
		
		server.close();
	}
	
	/**
	 * Convert milliseconds to seconds with one decimal place
	 * @param milliseconds
	 */
	private static double toSeconds(long milliseconds)
	{
		return (Math.floor(milliseconds / 100) / 10);
	}
	
	private static String readLineTimeout(BufferedReader reader, long timeout)
			throws TimeoutException, IOException
	{
		long start = new Date().getTime();
		
		while(!reader.ready())
		{
			if(new Date().getTime() - start >= timeout) throw new TimeoutException();
			
			//Delay between polling buffer
			try { Thread.sleep(50); }
			catch (Exception e) {	}
		}
		
		//The buffer contains something so we can guarantee no blocking on this read
		return reader.readLine();
	}
}
