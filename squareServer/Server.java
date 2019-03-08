import java.net.*;
import java.io.*;

public class Server
{

	public static void main(String[] args) throws IOException
	{
		Server fs = new Server();
		int port = 21921;
		ServerSocket server = new ServerSocket(port);
		while (true)
		{
			System.out.println("Waiting for another client...");
			Socket client = server.accept();

			// get and display client's IP address
			InetAddress clientAddress = client.getInetAddress();
			System.out.printf("Client from %s connected, passing to handler.%n", clientAddress);

			Thread h = new Thread(new Handler(client));
			h.start();
		}
	}
}
