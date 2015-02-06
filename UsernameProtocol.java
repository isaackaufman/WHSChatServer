/**
* @author Isaac Kaufman
* @version 1.0
*/

import java.io.Serializable;

public class UsernameProtocol implements Serializable
{

	// min and max length of username
	private int minChars;
	private int maxChars;

	// is null username allowed
	private boolean allowedNull;

	public UsernameProtocol(int min, int max, boolean nullBool)
	{
		minChars = min;
		maxChars = max;
		allowedNull = nullBool;
	}

	public int getMinChars()
	{
		return this.minChars;
	}

	public int getMaxChars()
	{
		return this.maxChars;
	}

	// check if username is valid according to instance variables
	public boolean isValid(String username)
	{
		// simple logic - TODO clean up if possible
		if (username == null)
		{
				return (username.length() >= this.minChars && username.length() <= this.maxChars) && allowedNull;
		}
		else
		{
			return username.length() >= this.minChars && username.length() <= this.maxChars;
		}
	}
}