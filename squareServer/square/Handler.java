package square;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Handler implements Runnable
{
	Socket client;

	public Handler(Socket client)
	{
		this.client = client;
	}

	@Override
	public void run()
	{
		try
		{
			// create a PrintWriter output stream (called out) and a
			// buffered input stream (called in) respectively, which
			// are associated with above created client socket
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

			boolean serviceover = false;
			while (serviceover == false)
			{
				// get and display client's input number
				int num = Integer.parseInt(in.readLine());
				System.out.printf("Client said: %s%n", num);

				if (num != 999)
				{
					System.out.printf("Client requests the square of %d%n", num);

					String result = String.valueOf(num * num);

					out.println(result);
				} else
				{
					serviceover = true;
					System.out.println("Session over.");
					client.close();
				}
			} // end of while
		} catch (Exception e)
		{
			System.out.println("error");
		}
	}

}
