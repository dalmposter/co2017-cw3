package CO2017.exercise3.dc346.server;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class GameState implements Runnable
{
	//Min number
	public static int MINVAL = 1;
	//Random generator
	static Random RANDGEN = new Random();
	
	//Max number
	int mv;
	//The number to be guessed
	int target;
	//Number of guesses so far
	int guesses;
	
	private boolean finished;
	private String gameState;
	private long tl;
	private long gameEnd;
	private GuessGameServerHandler ggsh;

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
		gameEnd = new Date().getTime() + tl;
		
		while(!finished)
		{
			//Has the game ended?
			if(gameEnd < new Date().getTime())
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
		if(gameEnd - new Date().getTime() > 0) return gameEnd - new Date().getTime();
		return 0;
	}
	
	public long getRemainingSeconds()
	{
		return Long.valueOf(Math.round((gameEnd - new Date().getTime()) / 100)) / 10;
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
