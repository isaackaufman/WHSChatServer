/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whschatserver;


import java.net.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Sweg
 */
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
	// message of the day
	private String motd;
	
	// construct a thread to talk across a given socket to the client
	public clientHandler (Socket socket)
	{
		this.socket = socket;	
	}
	
	public void run ()
	{
            try
            {
		// create in and out streams for the socket connection
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
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
				
		// add the PrintWriter to the HashSet
                out.println("NAMEACCEPTED");
                System.out.println(name + " connected.");
		motd = "Welcome to the Chat Room " + name + ", there are " + writers.size() + " other clients connected.";
		out.println("SYS" + this.motd);
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
			else if (input.startsWith("!"))
                        {
                            if (input.startsWith("!showclients"))
                            {
				String list = "The clients currently connected are:\n";
				for (String name : names)
                                {
                                    System.out.println(list);
                                    list = list + name + "\n";
                                }
                                out.println("USER" + list);
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
