package CO2017.exercise3.dc346.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GuessGameServer
{

	public static void main(String[] args) throws IOException
	{
		//Get and use port to create server socket
		int port = Integer.valueOf(args[0]);
		ServerSocket server = new ServerSocket(port);
		
		//Print start-up message
		System.out.printf("Starting GuessGame server (%s, %s) on port %s", args[1], Integer.valueOf(args[2]) * 1000, args[0]);
		
		//Loop to accept connections
		while(true)
		{
			//Pass the connection to a new handler and start it
			Socket client = server.accept();
			Thread h = new Thread(new GuessGameServerHandler(Integer.valueOf(args[1]), Long.valueOf(args[2]), client));
			h.start();
		}
	}
}
