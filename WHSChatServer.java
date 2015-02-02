/**
 *
 * @author Isaac Kaufman
 * @version 0.9
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class WHSChatServer {

    // port to listen on
    private static int PORT;
    
    // HashSet of usernames
    private static HashSet<String> names = new HashSet<String>();
    
    // HashSet of PrintWriters for each socket connection
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	
	
    public static void main (String[] args) throws IOException
    {
        if (args.length > 0)
		{
            PORT = Integer.parseInt(args[0]);
		}
		else
		{
            System.out.println("Usage: java server <PORT>");
            System.exit(1);
		}
		System.out.println("The server has been started on port " + PORT);
		ServerSocket listener = new ServerSocket(PORT);
	
		try
		{
            while (true)
            {
				(new clientHandler(listener.accept())).start();
            }
		}
		finally
		{
            listener.close();
		}
    }
	
    private static class clientHandler extends Thread
    {
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
	
		// construct a thread to talk across a given socket to the client
		public clientHandler (Socket socket)
		{
			this.socket = socket;	
		}
	
		public void run ()
		{
            try
            {
				// assign in and out streams for the socket connection
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
		
				// get username from client until username is unique
				while (true)
				{
                    out.println("SUBMITNAME");
                    if ((name = in.readLine()) != null)
                    {
                        synchronized (names)
                        {
                            if (!names.contains(name))
                            {
                                names.add(name);
                                break;
                            }
                        }
                    }
				}
				
				// tell the client the name has been accepted
				// print to the terminal that the client has connected
				// print motd to client
				// broadcast that a new client has connected to the server
				// add the PrintWriter to the HashSet
                out.println("NAMEACCEPTED");
                System.out.println(name + " connected.");
				out.println("SYS" + "Welcome to the Chat Room " + name + ", there are " + writers.size() + " other clients connected.";
				for (PrintWriter writer : writers)
                {
                    writer.println("SYS" + name + " has joined the chat room!");
				}
                writers.add(out);
				
				// broadcast messages
				String input;
				while (true)
				{
                    if ((input = in.readLine()) != null)
                    {
                        if (!input.startsWith("!"))
                        {
                            for (PrintWriter writer : writers)
                            {
                                writer.println("MSG" + name + ": " + input);
                            }
                        }
						else
						{
                            if (input.startsWith("!showclients"))
                            {
								String list = "The clients currently connected are:";
								synchronized (names)
								{
									for (String name : names)
									{
										list = list + " " + name;
									}
									out.println("USER" + list);
									break;
								}
                            }
                        }
                    }
                    else
                    {
						return;
                    }
		}
            }
            catch (IOException e)
            {
		// do something
            }
            finally
            {
		// close connections and clean up
                System.out.println(name + " disconnected.");
		names.remove(name);
		writers.remove(out);
		for (PrintWriter writer : writers)
		{   
                    writer.println("SYS" + name + " has left the chat room.");
		}
		try
		{
                    out.flush();
                    out.close();
                    in.close();
                    socket.close();
		}
		catch (IOException e)
		{
                    // do something
		}
            }
        }
    }
}
