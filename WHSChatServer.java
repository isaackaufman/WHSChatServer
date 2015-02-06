/**
 *
 * @author Isaac Kaufman
 * @version 0.9.5
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
    private static HashSet<ObjectOutputStream> writers = new HashSet<ObjectOutputStream>();
	
	
    public static void main (String[] args) throws IOException
    {
    	// start server on port given by first argument
        if (args.length == 1)
		{
            PORT = Integer.parseInt(args[0]);
		}
		else
		{
            System.out.println("Usage: java WHSChatServer <PORT>");
            System.exit(1);
		}

		System.out.println("The server has been started on port " + PORT);

		// create ServerSocket for clients to connect to
		ServerSocket listener = new ServerSocket(PORT);
		try
		{
            while (true)
            {
            	// create a clientHandler object for each accepted connection from clients and start the thread
				Socket clientSocket = listener.accept();

				System.out.println("Incoming connection from " + clientSocket.getInetAddress().getHostAddress() + " accepted.");

				new clientHandler(clientSocket).start();
            }
		}
		finally
		{
			// close socket
            listener.close();
		}
    }
	
    private static class clientHandler extends Thread
    {
		private String name;
		private Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
	
		// construct a thread to talk across a socket to the client
		public clientHandler (Socket socket)
		{
			this.socket = socket;	
		}
	
		public void run ()
		{
            try
            {

            	// *** SET UP CLIENT ***
				// assign in and out streams for the socket connection
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
		
				// get username from client until username is unique
				while (true)
				{
                    out.writeObject(new Message(Message.Type.SUBMITNAME));
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
                out.writeObject(Type.NAMEACCEPTED);
                System.out.println(this.socket.getInetAddress() + " authenticated with username " + name);
				out.writeObject(new Message(Message.Type.SYS, "Welcome to the Chat Room " + name + ", there are " + writers.size() + " other clients connected."));
				for (ObjectOutputStream writer : writers)
                {
                    writer.writeObject(new Message(Message.Type.SYS, name + " has joined the chat room!"));
				}
                writers.add(out);

				
				// *** NORMAL COMMUNICATION HANDLING ***
				String message;
				while (true)
				{
                    if ((message = in.readLine()) != null)
                    {
                        if (!message.startsWith("!"))
                        {
                            for (ObjectOutputStream writer : writers)
                            {
                                writer.writeObject(new Message(name, message, Message.Type.USER));
                            }
                        }
						else
						{
                            if (message.startsWith("!showclients"))
                            {
								String list = "The clients currently connected are:";
								synchronized (names)
								{
									for (String name : names)
									{
										list = list + " " + name;
									}
									out.writeObject(new Message(Message.Type.SYS, list));
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

				try
				{
					for (ObjectOutputStream writer : writers)
					{   
            			writer.writeObject(new Message(Message.Type.SYS, name + "has disconnected."));
					}
					out.flush();
                	out.close();
                	in.close();
                	socket.close();
                }
                catch (IOException e)
                {
                	// stuff
                }
            }
        }
    }
}
