package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p>The class Runner is the <b>main class</b> of the application, 
 * which handles user input.</p>
 * 
 * <p>Instantiates relevant classes needed to perform the <b>decryption</b> and generation of <b>chi-squared
 * statistics</b>, displaying the results of the most likely key. 
 * Contains a nested class for decryption and thread-handling.</p>
 * 
 * @author G00371430
 * @version 1.0
 * @since 17.0.1
 */
public class Runner {

	// Instance Variables - Maps will be used concurrently by each thread
	Map<Character, Double> expected = new ConcurrentSkipListMap<>(); // Map - Stores 95 common English letters and their frequencies
	Map<Character, Integer> cypherFrequency = new ConcurrentSkipListMap<>(); // Map - Contains all unique characters in the ciphertext and their frequency
	ArrayList<Character> cypherContent = new ArrayList<Character>(); // ArrayList - Contains all characters within the ciphertext 
	int mostLikelyKey;
	int threads;
	double lowestChiScore;
	Cipher c;
	Monogram m;
	
	/**
	 * <p>Initializes the instance variables of instance Runner</p>
	 */
	Runner()
	{
		// Initialize likely key and it's chi score
		mostLikelyKey = 0;
		lowestChiScore = 0;
		threads = 5;
		c = new Cipher(); // Initialize instance of Cipher
		m = new Monogram();
		expected = m.expectedProbability(); // Create map based on monogram text file
	}
	
	/**
	 * <p>Provides a menu to the user which allows for a myriad of options,
	 * such as decyphering a text based on the most likely key</p>
	 * 
	 * <p>Handles method calls and their return values, based on the options in
	 * which a user chooses.</p>
	 * 
	 * @throws Exception
	 */
	public void go() throws Exception
	{
		// Variables
		Scanner scan = new Scanner(System.in);
		String userInput;
		int option;
		String cypherText = null, cypherFile = null;
		boolean exit = true;
			
		// Instance of Message
		Message m;
				
		// Header for Program
		System.out.println("***** Cypher Cracker System *****");
		
		// Initial Read 
		System.out.println("1) Enter cyphertext"); 
		System.out.println("2) Specify a text file"); 
		System.out.println("3) Crack ciphertext");
		System.out.println("4) Display text based on lowest chi-score");
		System.out.println("5) Specify number of threads to use");
		System.out.println("6) Quit");
		System.out.println("\nSelect Option [1-6]>");
		
		// Read in value from user
		userInput = scan.next();
		option = Integer.parseInt(userInput);
		
		// While: Continuously prompts user for input until condition is false
		while(exit) 
		{	
			if(option == 1)
			{
				// Read in 
				System.out.println("Please enter the cyphertext without spaces (key used to encrypt can only be from 0-94):");
				cypherText = scan.next();
				
				cypherFrequency = c.cypherList(cypherText);
				cypherContent = c.returnCypherContent();
				mostLikelyKey = 0; // Reset values on new ciphertext entered
				lowestChiScore = 0;	
			}
			else if(option == 2)
			{
				// Read in 
				System.out.println("Please enter the file path of the cyphertext:");
				cypherFile = scan.next();
				cypherText = c.readInFile(cypherFile); // Initialize string based on text-file entered
				cypherFrequency = c.cypherList(cypherText); // Initialize map based on characters and their frequency (unique values)
				cypherContent = c.returnCypherContent(); // Initialize list based on all contents within cypher (used to query the map) 
				mostLikelyKey = 0; // Reset values on new ciphertext entered
				lowestChiScore = 0;
			}
			else if(option == 3)
			{
				if(cypherFrequency.isEmpty() || expected.isEmpty())
				{
					System.out.println("Must enter cyphertext or text file to crack!\n");
				}
				else
				{
					// Invoke method to begin execution of task by the executor service
					begin();
				}
			}
			else if(option == 4)
			{
				if(mostLikelyKey == 0 || lowestChiScore == 0)
				{
					System.out.println("Cypher has not been cracked yet!");
				}
				else
				{
					m = new Message(cypherText, mostLikelyKey);
					m.decrypt();
				}
			}
			else if(option == 5)
			{
				do 
				{
					// Ask for user-specified number
					System.out.println("Please enter number of threads to use (1-10):");
					threads = scan.nextInt();	
				}while(threads < 0 || threads > 10);		
			}
			else if(option == 6)
			{
				exit = false;
			}
						
			// Check if sentinel is still true..
			if(!exit) break;
			
			// Subsequent Read
			System.out.println("1) Enter cyphertext"); 
			System.out.println("2) Specify a text file"); 
			System.out.println("3) Crack ciphertext");
			System.out.println("4) Display text based on lowest chi-score");
			System.out.println("5) Specify number of threads to use");
			System.out.println("6) Quit");
			System.out.println("\nSelect Option [1-6]>");
			
			// Read in value from user
			userInput = scan.next();
			option = Integer.parseInt(userInput);
		}
		
		// Exit message
		System.out.println("Farewell..");
		scan.close();
	}
	
	/**
	 * <p>Passes an ExecutorService, containing a pool of threads, a new instance of
	 * Statistics to execute a set number of times.</p>
	 * 
	 * <p>Each thread of execution, deciphers the entered cypher-text based on a
	 * key, which ranges from 0-95. The result of each thread is then added to
	 * a map to be read to determine the lowest chi-score.</p>
	 * 	  
	 * @throws InterruptedException If a thread is interrupted
	 * @throws ExecutionException
	 */
	private void begin() throws InterruptedException, ExecutionException 
	{
		// Create map to contain chi-squared results and associated decryption key
		ExecutorService pool = Executors.newFixedThreadPool(threads); // Executor Service - Creates pool of threads (5 or user-defined) to run a certain task
		Map<Double, Integer> results = new ConcurrentSkipListMap<Double, Integer>(); 
		List<Double> keyList = Collections.synchronizedList( new ArrayList<Double>()); // https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List)
		
		System.out.println("Determining lowest chi-squared score....");
		
		for(int i=0; i < 95; i++)
		{
			// Submits task to the pool, alongside the ciphertext map
			Future<Double> result = pool.submit(new Statistics(cypherContent, i));
			
		     // Error-handling
			 if (Thread.interrupted()) 
			 {
				 throw new InterruptedException();
			 }
				     		
			results.put(result.get(), i);
			keyList.add(result.get());				
		}
		
		// Set current lowest probability to first value in index
		lowestChiScore = keyList.get(0);
		
		// For: Loop based on the size of the results list
		for(int i=0; i < results.size(); i++)
		{
			// If: The current chi-squared value is lower than the previous lowest...
			if(keyList.get(i) < lowestChiScore)
			{
				// Updated its value
				lowestChiScore = keyList.get(i);
			}
		}
		
		// Retrieve the most likely key....
		mostLikelyKey = results.get(lowestChiScore);
		System.out.printf("Key with the lowest chi-score(%.2f): %d\n\n",lowestChiScore,mostLikelyKey);		
	}

	/**
	 * <p>Instance of this class is used to calculate the chi-score of the
	 * the entered cyphertext</p>
	 * 
	 * <p>This is calculated based on the frequency of each unique character
	 * within the text, against the frequency that is found within the monogram
	 * frequency. Implements method from an interface</p>
	 * 
	 * @author G00371430
	 * @version 1.0
	 * @since 17.0.1
	 * @see Callable
	 */
	public class Statistics implements Callable<Double>
	{
		// Instance variables
		int key;
		ArrayList<Character> cipher;
		ArrayList<Character> decryptKeys = new ArrayList<Character>();
		Map<Character,Integer> guesses = new ConcurrentSkipListMap<>();
		
		/**
		 * <p>Initializes the instance variables of the current class</p>
		 * @param c - A list of all unique characters within the text
		 * @param key - A key in which to decypher the text
		 */
		Statistics(ArrayList<Character> c, int key)
		{
			this.cipher = c;
			this.key = key;
		}
		
		/**
		 * <p>Inherited from the functional interface <b>Callable</b></p>
		 *  
		 * <p>Used to decypher a specified text based on a passed in key and a list of unique
		 * characters from the text.</p>
		 * 
		 * @throws Exception
		 * @return Results from the method used to calculate the chi-score
		 * @see calculateChiSquare()
		 */
		public Double call() throws Exception {
			
			int newValue;
			
			// Reference: https://youtu.be/WNmtw5165QQ (Used to determine how to change a character based on an integer key)
			// For-each: Letter in the ciphertext character list
			for(Character letter: cipher)
			{
				// Initialize new value based on the current letter divided by the current key
				newValue = letter - key;
				
				// If: The decrypted letter is less than 32 (lower limit of ASCII characters)
				while(newValue < 32)
				{
					// Find the difference between the current decimal value of the character and the lower limit...
					int difference = letter - 32;
					
					// Divide the difference from the key and then divide the result from the upper limit of ASCII characters
					newValue = 126 - (key - difference);
				}
					
				// If: The decrypted character doesn't already exist within the list...
				if(!guesses.containsKey((char)newValue))
				{
					// Add to list of characters to be used to search map for frequencies
					decryptKeys.add((char)newValue);
					
					// Populate map based on decryption value and add the frequency of the value before decryption
					guesses.put((char)newValue, cypherFrequency.get(letter));
				}
		
			}
			
			// Calculate the chi-squared value
			double chi = calculateChiSquare();
			
			// Return the value 
			return chi;
		}
		
		// Reference: https://www.statisticshowto.com/probability-and-statistics/chi-square/
		/**
		 *  <p>Used to calculate the chi-score by comparing the expected frequency
		 *  of each unique decrypted character against their actual frequency
		 *  within the text.</p>
		 *  
		 * @return The calculated chi-score
		 */
		public Double calculateChiSquare()
		{
			// Initialize variable to the size of the ciphertext
			int n = guesses.size();
			double expectedFrequency;
			int actualFrequency;
			double residual; // difference between expected and actual frequency
			double chiValue = 0.2f;
				
			// For-each: Character in list of unique characters
			for(int i = 0; i < n; i++)
			{
				expectedFrequency = (expected.get(decryptKeys.get(i))) * cipher.size(); // Initialize expected frequency based on probability within monogram list and multiply by the length of the ciphertext
				actualFrequency = guesses.get(decryptKeys.get(i)); // Initialize actual frequency based on the frequency rate of the character within the ciphertext
				residual =  Math.pow((actualFrequency - expectedFrequency), 2); // Find the difference between and square them
				chiValue+= residual / expectedFrequency; // Divide the residual value by the expected frequency to determine the chiValue
			}
			
			// Return chi-value
			return chiValue;
		}	
	}
	
	// Main method
	public static void main(String[] args) throws Exception 
	{
		// New instance of Runner and invokes 'go'
		new Runner().go();		
	}
}
