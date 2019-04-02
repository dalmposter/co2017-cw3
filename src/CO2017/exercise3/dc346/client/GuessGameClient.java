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
		
		//Start a new thread to get client input
		ClientState clientState = new ClientState(out);
		new Thread(clientState).start();
		
		//Get and format the message from the server to be printed to client console
		String[] received = in.readLine().split("\\:");
		clientState.userPrint(false,
				String.format("New guessing game. Range is %s..%s. Time limit is %ss",
						GameState.MINVAL,
						received[1],
						toSeconds(Long.parseLong(received[2]))));
		
		//Loop to repeatedly read server responses
		while(!clientState._finished)
		{
			//Interpret response from server
			received = in.readLine().split("\\:");
				
			//If the guess was valid but incorrect
			if(received[0].equals("HIGH") || received[0].equals("LOW"))
			{
				clientState.userPrint(false,
						String.format("Turn %s: %s was %s, %ss remaining",
								received[2],
								clientState.lastGuess,
								received[0],
								toSeconds(Long.parseLong(received[1]))));
			}
			//If the guess was invalid
			else if(received[0].equals("ERR"))
			{
				clientState.userPrint(false,
						String.format("ERROR: Turn %s: %ss remaining",
								received[2],
								toSeconds(Long.parseLong(received[1]))));
			}
			//If the game is over
			else
			{
				clientState.userPrint(true,
						String.format("Turn %s: target was %s - %s",
								received[1],
								received[2],
								received[0]));
				clientState._finished = true;
			}
		}
		
		server.close();
	}

	/**
	 * Convert milliseconds to seconds with one decimal place
	 * 
	 * @param milliseconds
	 */
	private static double toSeconds(long milliseconds)
	{
		return (Math.floor(milliseconds / 100) / 10);
	}
}
