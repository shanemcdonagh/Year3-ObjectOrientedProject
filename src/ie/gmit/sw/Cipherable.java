package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Map;

/** 
 * This interface is used to ensure that the capability of reading in a cyphertext and
 * generating the necessary lists/maps is provided.
 * 
 * @author G00371430
 * @version 1.0
 * @since 17.0.1
 * @see Cipher
 */
public interface Cipherable
{		
	/**
	 * <p>Used to generate a <b>String</b> based on a specified text file</p>
	 * 
	 * @param file - The name of the text file in which the user entered
	 * @return A String value based off the contents of the file
	 */
	public String readInFile(String file);
	
	/**
	 * <p>Generates a <b>Map</b> based on each distinct character within the 
	 * text and their frequency</p>
	 * 
	 * @param cypher - The cipher-text entered
	 * @return A list containing all the characters within the text
	 */
	public Map<Character, Integer> cypherList(String cypher);
	
	/**
	 * <p>Returns a list of all characters from the text</p>
	 * 
	 * 
	 * @return An instance of ArrayList based on all the characters 
	 * within the text.
	 */
	public ArrayList<Character> returnCypherContent();
}
