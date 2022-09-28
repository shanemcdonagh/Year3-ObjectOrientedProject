package ie.gmit.sw;

/**
 * <p>Instance of this class is used to <b>decrypt</b> and return a <b>message</b></p>
 * 
 * <p>A decrypted message is generated based on a key passed in</p>
 * 
 * @author G00371430
 * @version 1.0
 * @since 17.0.1
 */
public class Message 
{
	// Instance Variables
	private String cypher, message = "";
	private int key;
	
	/**
	 * Initializes the instance of current class Message
	 * 
	 * @param c The list of characters to decipher
	 * @param k The key to decipher with
	 */
	Message(String c, int k)
	{
		this.cypher = c;
		this.key = k;
	}
	
	/**
	 * <p>Displays a <b>decrypted</b> message.</p> 
	 * 
	 * <p>List of characters are shifted by a key associated with
	 * the lowest chi-score</p>
	 */
	public void decrypt()
	{	
		// Variables
		int decryptedLetter;
		char letter;
			
		// Loop over the length of the line
		for(int i=0; i < cypher.length(); i++)
		{	
			// Read in the i-th character in the line
			letter = cypher.charAt(i);
			decryptedLetter = letter - key;
			
			// If: The decrypted letter is less than 32 (lower limit of ASCII characters)
			while(decryptedLetter < 32)
			{
				// Find the difference between the current decimal value of the character and the lower limit...
				int difference = letter - 32;
				
				// Divide the difference from the key and then divide the result from the upper limit of ASCII characters
				decryptedLetter = 126 - (key - difference);
			}
			
			// If: The letter is a full stop...
			if(decryptedLetter == 46)
			{
				message+= (char)decryptedLetter + System.lineSeparator();
			}
			else
			{
				// Add string to message
				message+=(char)decryptedLetter;
			}	
		}
	
		// Display the results
		System.out.println("Result:\n"  + message + "\n");		
	}
}
