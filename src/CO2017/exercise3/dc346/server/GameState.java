package CO2017.exercise3.dc346.server;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class GameState implements Runnable
{
	//Min number
	static int MINVAL = 1;
	//Random generator
	static Random RANDGEN = new Random();
	
	//Max number
	int mv;
	//The number to be guessed
	int target;
	//Number of guesses so far
	int guesses;
	
	//Is the game over?
	private boolean finished;
	//Store the result of the last guess as a string
	private String gameState;
	//Store the time limit (used to initialise gameEnd in run in case there is a delay between instantiating this and starting the thread)
	private long tl;
	//Store the date/time at which the game ends (ms since epoch)
	private long gameEnd;
	private GuessGameServerHandler ggsh;

	/**
	 * Create a new game.
	 * @param mv the max number to guess
	 * @param tl the time limit for this game
	 * @param ggsh the server handler for this game
	 */
	public GameState(int mv, long tl, GuessGameServerHandler ggsh)
	{
		this.tl = tl;
		this.mv = mv;
		this.ggsh = ggsh;
		guesses = 0;
		target = RANDGEN.nextInt(mv + 1 - MINVAL) + MINVAL;
	}
	
	@Override
	public void run()
	{
		//Initialise gameEnd to be now + time limit
		gameEnd = System.currentTimeMillis() + tl;
		
		while(!finished)
		{
			//Has the game ended?
			if(gameEnd < System.currentTimeMillis())
			{
				gameState = "LOSE";
				finished = true;
				try
				{
					ggsh.shutdownInput();
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		
	}

	public int getTarget()
	{
		return target;
	}

	public int getGuesses()
	{
		return guesses;
	}
	
	public boolean finished()
	{
		return finished;
	}
	
	/**
	 * @return Milliseconds until game end
	 */
	public long getTimeRemaining()
	{
		if(gameEnd - System.currentTimeMillis() > 0) return gameEnd - System.currentTimeMillis();
		return 0;
	}
	
	public long getRemainingSeconds()
	{
		return Long.valueOf(Math.round((gameEnd - System.currentTimeMillis()) / 100)) / 10;
	}
	
	/**
	 * Make a guess.
	 */
	public void guess(int guess)
	{
		guesses++;
		
		if(getTimeRemaining() <= 0)
		{
			gameState = "LOSE";
		}
		//User guessed correctly!
		else if(guess == target)
		{
			finished = true;
			gameState = "WIN";
		}
		else if(guess > target)
		{
			gameState = "HIGH";
		}
		else
		{
			gameState = "LOW";
		}
	}
	
	public String toString()
	{
		return gameState;
	}

}
