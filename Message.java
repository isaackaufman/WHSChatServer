/*
 * @author Isaac Kaufman
 * @version 0.9
 */

public class Message
{

	// declare instance variables
	
	// username of client who sent the Message object
	String sender;
	// message text
	String message;
	// type of message being sent
	Type type;
	
	// constructor for no arguments
	// TODO - consider removing - should this be illegal?
	public Message ()
	{
		this.sender = null;
		this.message = null;
		this.type = null;
	}
	
	// standard constructor to initiate all instance variables
	public Message (String sender, String message, Type type)
	{
		this.sender = sender;
		this.message = message;
		this.type = type;
	}

	// constructor for Messages from server to establish username or other protocol processes
	public Message (Type type)
	{
		this.sender = null;
		this.message = null;
		this.type = type;
	}

	public Message (Type type, String message)
	{
		this.sender = null;
		this.message = message;
		this.type = type;
	}
	
	// return value of message instance variable - mainly for printing
	public String getMessage()
	{
		return this.message;
	}
	
	// check if message is a valid length
	public boolean isValid()
	{
		return this.message.length() > 0 && this.message.length() < 250;
	}
}
