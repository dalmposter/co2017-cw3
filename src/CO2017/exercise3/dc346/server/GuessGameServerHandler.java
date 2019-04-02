package CO2017.exercise3.dc346.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class GuessGameServerHandler implements Runnable
{
	private GameState game;
	//The unique upper case character identifying this handler
	private char id;
	//Max value to be guessed
	private int mv;
	//The time limit for games with this handler
	private long timeLimit;
	//Connection to client
	private Socket client;
	
	//Streams to client
	private PrintWriter out;
	private BufferedReader in;
	//Static counter to decide ascii code of next id character
	static int nextChar = 65;
	
	boolean serviceover;
	
	/**
	 * Print a message to the server console (prefixed with this handlers id)
	 */
	private void log(String msg)
	{
		System.out.println(id + " " + msg);
	}
	
	/**
	 * Send a line to the client
	 */
	private void send(String msg) throws IOException
	{
		out.printf("%s%n", msg);
	}

	/**
	 * Constructor
	 * @param mv The maximum number to be guessed
	 * @param tl The time limit for games with this handler
	 * @param c1 The client for this handler
	 */
	public GuessGameServerHandler(int mv, long tl, Socket c1)
	{
		this.mv = mv;
		timeLimit = tl * 1000;
		game = new GameState(mv, timeLimit, this);
		client = c1;
		
		//Get the next character whos ascii code is between 65 and 90 (the upper case letters)
		if(nextChar > 90) nextChar = 65;
		id = (char) nextChar;
		nextChar++;
	}
	
	/**
	 * Start this handlers game with the client
	 */
	@Override
	public void run()
	{
		try
		{
			//Create streams to client
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			
			//Send the start message to client and start the GameState thread (for the timer)
			send(String.format("START:%s:%s", mv, timeLimit));
			Thread t = new Thread(game);
			t.start();
			
			//Log some info to the server console
			log("connection : " + client.getInetAddress());
			log("start watching");
			log("target is " + game.getTarget());
			
			serviceover = false;
			int guess = 0;
			while(!serviceover)
			{
				boolean valid = false;
				boolean isInt = false;
				String guessStr = "";
				guess = 0;
				
				//Try to get the guess from the client. An exception here means they entered a non-integer message
				try
				{
					guessStr = in.readLine();
					guess = Integer.parseInt(guessStr);
					
					//If we didn't cause an exception the client entered an integer
					isInt = true;
					//If we reach this if statement and pass it, the guess is within the allowed range
					if(guess <= mv && guess >= GameState.MINVAL) valid = true;
				}
				//Causing this exception means the client entered a non-integer guess
				//Set this flag to send 'ERR non-integer' instead of 'ERR out of range'
				catch(NumberFormatException ex) { isInt = false; }
				
				if(!valid)
				{
					//Send error message to client
					send(String.format("ERR:%s:%s", game.getTimeRemaining(), game.getGuesses()));
					if(isInt)
					{
						//The client sent an integer out of range. Log this event to the server console
						log(String.format("%s (ERR out of range)%s/%s", guess, game.getRemainingSeconds(), game.getGuesses()));
					}
					else
					{
						//The client didn't send an integer. Log this event to the server console
						log(String.format("%s (ERR non-integer)%s/%s", guessStr, game.getRemainingSeconds(), game.getGuesses()));
					}
				}
				else
				{
					//Send the guess to the GameState
					game.guess(guess);
					if(game.finished())
					{
						//If the game is finished, break the loop to send end of game message and terminate service
						serviceover = true;
					}
					else
					{
						//The game continues, send them the result of their guess from the GameState
						send(String.format("%s:%s:%s", game.toString(), game.getTimeRemaining(), game.getGuesses()));
					}
					//Log whatever happened
					log(String.format("%s (%s)-%ss/%s", guess, game.toString(), game.getRemainingSeconds(), game.getGuesses()));
				}
			}
			
			//If we left the loop the game is over so send a WIN/LOSE message to the client and log to console.
			send(String.format("%s:%s:%s", game.toString(), game.getGuesses(), game.getTarget()));
			log(String.format("%s (%s)-%ss/%s", guess, game.toString(), game.getRemainingSeconds(), game.getGuesses()));
		}
		catch (IOException e)
		{
			log("Error in Handler.run(): " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	public void shutdownInput() throws IOException
	{
		serviceover = true;
		client.shutdownInput();
	}
}
