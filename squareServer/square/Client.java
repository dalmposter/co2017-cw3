package square;
import java.net.*;
import java.io.*;

public class Client
{

	public static void main(String[] args) throws IOException
	{
		BufferedReader in = null;
		PrintWriter out = null;
		Socket server = null;

		server = new Socket("localhost", 21921);
		in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		out = new PrintWriter(server.getOutputStream(), true);
		// get and display the server's IP address
		InetAddress serverAddress = server.getInetAddress();
		System.out.println("Connected to " + serverAddress);

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		boolean serviceover = false;
		while (serviceover == false)
		{
			// read in an integer number that is typed in by the client
			// and send the number to the server
			System.out.println(
					"Enter an integer to find it's square, or 999 to quit");
			int number = Integer.parseInt(stdin.readLine());
			out.println(number);

			if (number != 999)
			{
				// get and display the server's message
				String inMessage = in.readLine();
				System.out.println("The server responded with " + inMessage);
			} else
			{
				serviceover = true;
				System.out.println("Terminate session.");
				server.close();
			}

		}
	}
}
