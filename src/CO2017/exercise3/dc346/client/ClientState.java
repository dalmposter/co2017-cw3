package CO2017.exercise3.dc346.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class ClientState implements Runnable
{
	//System.in
	static final BufferedReader _tty = new BufferedReader(new InputStreamReader(System.in));
	
	private volatile boolean _finished;
	private String lastGuess;
	
	//Data from server
	private PrintWriter out;
	
	public ClientState(PrintWriter o)
	{
		_finished = false;
		out = o;
	}
	
	@Override
	public void run()
	{
		//Loop to repeatedly read guesses from the client console and send them to the server
		while(!_finished)
		{
			try
			{
				//Read input and send to server
				lastGuess = readLineTimeout(_tty, 500);
				out.printf("%s%n", lastGuess);
			}
			catch (TimeoutException e) { }
			catch (IOException e) { }
		}
	}
	
	/**
	 * Print a message to the client console.
	 * @param end has the game ended?
	 * @param msg the message to be printed
	 */
	void userPrint(boolean end, String msg)
	{
		//If the user lost, we interrupted input so need a new line
		if(end && !GuessGameClient.won) System.out.println();
		
		System.out.println(msg);
		
		//If the game is not over, the user can guess again. Prompt them
		if(!end) System.out.print("Enter guess: ");
	}
	
	/**
	 * Helper function that allows the interruption of reading from a buffered reader after a given time.
	 * @param reader
	 * @param timeout the length of time to wait for input before timing out (in milliseconds)
	 * @return The next line from the reader
	 * @throws TimeoutException if no input was received in timeout milliseconds
	 * @throws IOException 		if the reader is closed unexpectedly
	 */
	public String readLineTimeout(BufferedReader reader, long timeout)
			throws TimeoutException, IOException
	{
		long start = System.currentTimeMillis();
		
		while(!reader.ready())
		{
			if(System.currentTimeMillis() - start >= timeout) throw new TimeoutException();
			
			//Delay between polling buffer
			try { Thread.sleep(50); }
			catch (Exception e) {	}
		}
		
		//The buffer contains something so we can guarantee no blocking on this read
		return reader.readLine();
	}
	
	//Get the last guess the user made
	String getLastGuess()
	{
		return lastGuess;
	}

	
	//Getter and setter for _finsihed, used in GuessGameClient
	public boolean is_finished()
	{
		return _finished;
	}

	public void set_finished(boolean _finished)
	{
		this._finished = _finished;
	}
	
	
}
