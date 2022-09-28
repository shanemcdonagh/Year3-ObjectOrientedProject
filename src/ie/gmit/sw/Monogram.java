package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Instance of this class is used to created a Map based on a monogram file.
 * 
 * @author G00371430
 * @version 1.0
 * @since 17.0.1
 */
public class Monogram
{
	// Instance Variable
	private Map<Character, Double> expected;
	
	/** 
	 * <p>Initializes instance of a HashMap and invokes the method to populate it</p>
	 */
	Monogram()
	{
		expected = new HashMap<>();
		mapExpectedFrequencies();
	}
	
	/**
	 * <p>Populates instance of Map<Character,Double> based on the monograms file entered.</p>
	 * 
	 * <p>This map is later used to calculate the chi-score of an entered cyphertext.</p>
	 */
	private void mapExpectedFrequencies()
	{
		// Instance of ArrayList
		ArrayList<String> list = new ArrayList<>();
		
		// Try-Catch: Read in monograms file
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./monograms-ASCII-32-127.txt")))))
		{
			// Variables
			String line = null; // Used to read in line from text file
			String values[] = null;
			double probability = 0;
			
			// While: line isn't empty
			while((line = br.readLine())!=null) 
			{
				values = line.split(","); // Splits line into separate values
				
				// For: Each word in the words[] array
				for(String value: values)
				{
					list.add(value); // Add to the ArrayList
				}
			}
			
			//For: Loops based on the amount of characters in a line
			for(int i = 0; i <list.size(); i+=2) 
			{	
				// Reads the first character of index 
				char c = list.get(i).charAt(0);
		
				// Parses double value from the numerical values within the list
				probability = Double.parseDouble(list.get(i+1));
				
				// Once it comes across the same letter again, it replaces it with ','
				if(expected.containsKey(c))
				{	
					// Places them within a map
					expected.put(',',(probability / 100d));
				}
				else
				{
					// Places them within a map
					expected.put(c,(probability / 100d));
				}		
			}		
			// Notify of successful creation of instance Map
			System.out.println("***Successfully created map of monograms***\n");
		}
		catch(Exception e) 
		{	
			// Notify of file-handling error
			System.out.println("Encountered error reading in file " + e);
		}
	}
	
	/**
	 * <p>Returns a new instance of Map based on the currently populated map</p>
	 * 
	 * @return Map containing each common English character and their frequency
	 * @see mapExpectedFrequencies()
	 */
	public Map<Character, Double> expectedProbability()
	{
		return new HashMap<Character, Double>(expected);
	}

}
