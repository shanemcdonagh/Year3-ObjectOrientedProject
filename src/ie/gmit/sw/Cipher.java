package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Instance of this class is used to create maps/lists based on
 *    ciphertext entered by a user</p>
 *    
 * <p> Implements methods declared in the interface, <b>Cipherable</b>
 *    
 * @author G00371430
 * @version 1.0
 * @since 17.0.1
 * @see Cipherable
 */
public class Cipher implements Cipherable{

	// Instance variables
	private ArrayList<Character> cypherContent = new ArrayList<Character>();
	private Map<Character, Integer> cypherFrequency = new HashMap<>();
		
	@Override
	public String readInFile(String file)
	{
		String cyphertext = ""; // Used to contain the contents of the text file
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)))))
		{
			// Variables
			String line = null; // Used to read in line from text file
			char letter;
			
			while((line = br.readLine())!=null) // while: line isn't empty
			{
				// Loop over the length of the line
				for(int i=0; i < line.length(); i++)
				{
					// Read in the i-th character in the line
					letter = line.charAt(i);
					
					// Add letter to the content list
					cyphertext+=letter;		
				}
			}		
			// Confirm successful entry
			System.out.println("Successfully read in cipher text file...\n");
			
		}
		catch(Exception e) 
		{	
			// Display appropriate error
			System.out.println("Encountered error reading in file " + e);
		}
		
		// Return cyphertext
		return cyphertext;
		
	}
	
	// https://stackoverflow.com/a/1521935
	// https://www.geeksforgeeks.org/print-characters-frequencies-order-occurrence/
	@Override
	public Map<Character, Integer> cypherList(String cypher)
	{
			// Variables
			char letter;
			
			// Loop over the length of the line
			for(int i=0; i < cypher.length(); i++)
			{
				// Read in the i-th character in the line
				letter = cypher.charAt(i);
				
				// Add letter to the content list
				cypherContent.add(letter);
					
				// If: The letter is already in the frequency list...
				if(cypherFrequency.containsKey(letter))
				{
					// Increase the frequency of the letter
					cypherFrequency.replace(letter, cypherFrequency.get(letter) + 1);
				}
				else
				{
					// Enter into the frequency list
					cypherFrequency.put(letter, 1);
				}
			}
	
		// Notify of successful addition
		System.out.println("Successfully created list of cyphertext characters\n");
	
		// Return the list of characters from the cyphertext
		return new HashMap<Character, Integer>(cypherFrequency);
	}
	
	@Override
	// Returns a copy of the cyphertext broken into individual characters
	public ArrayList<Character> returnCypherContent()
	{
		return new ArrayList<Character>(cypherContent);
	}
}
