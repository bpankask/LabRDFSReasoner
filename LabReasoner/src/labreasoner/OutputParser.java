/**
 * Class to take output from the reasoner trace and format it for an LSTM in Tensorflow.
 */
package labreasoner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;




public class OutputParser {
	
	
	/**
	 * Reads the trace of the inferences
	 * If we add rules that use more than two facts to draw an inference we will need to change this method to accommodate that
	 * @param fileName
	 * @return
	 */
	public List<StringBuilder> readTrace(String traceFile) {
		
		//holds every line in trace file
		List<String> sl = new ArrayList<String>();
		
		try {
		File file = new File(traceFile);
		Scanner scan = new Scanner(file);
		
		//puts every line from the file to the list of strings
		while(scan.hasNext()) {
			String line = scan.nextLine().strip();
			sl.add(line);
		}
		
		//list to store individual steps that were meshed together in previouse list
		List<StringBuilder> sbl = new ArrayList<StringBuilder>();
		
		//string builder to store the conclusion and the facts it used to find it or a "step"
		StringBuilder sb = new StringBuilder();
		
		//loops through list looking for a line that starts with "Fact (" which indicates the end of a step
		int i = 0;
		do {
			String l = sl.get(i);
			sb.append(l + "\n");
			
			//checks to see if the line starts with Fact (
			if(l.substring(0, 6).contentEquals("Fact (")) {
				//some Fact ( lines will have a second fact line
				if(i+1 < sl.size()) {
					String l2 = sl.get(i+1);
					if(l2.substring(0, 6).contentEquals("Fact (")) {
						sb.append(l2 + "\n");
						i++;
					}
				}
				//adds sb to sbl when a step is found indicated by the fact line
				sbl.add(sb);
				//then clears string builder
				sb = new StringBuilder();
			}
			i++;
		}while(i < sl.size());
		
		scan.close();
		
		//returns list of string builders, each one being an entailment and the facts it used to find it
		return sbl;
		
		}
		catch(Exception e) {
			System.out.println("...Error in Method readTrace in OutputParser class...");
			return null;
		}
	}
	
	/**
	 * Strips the input of any line starting with Fact ( because that knowledge will already be in the KB when passed to the LSTM
	 * @param input
	 * @return newArray
	 */
	public String[] factStripper(String[] input) {
		
		//length for new array
		int len = input.length;
		
		//loops through to remove any line that starts with "Fact ("
		for(int i=0; i<input.length; i++) {
			if(input[i].substring(0, 6).contentEquals("Fact (")) {
				input[i] = "";	
				len--;
			}		
		}
		
		//creates and fill newArray with only the values that are not "Fact (" lines
		String[] newArray = new String[len];
		for(int j=0; j<newArray.length; j++) {
			newArray[j] = input[j];
		}
		
		return newArray;
	}//end method
	
	
	/**
	 * 
	 * @param input
	 * @return numOfLists
	 */
	public int getNumOfLists(List<String[]> input) {
	
		//variable used to calculate the number of lists to add 
		int numOfLists = 0;
		
		//outer loop for input 
		for(int i=0; i<input.size(); i++) {
			
			//updates the numOfStacks variable to see if you need to add any more
			String[] arrayFromInput = input.get(i);
			if(arrayFromInput.length >= numOfLists) {
				int numOfListsToAdd = arrayFromInput.length - numOfLists;
				numOfLists += numOfListsToAdd; 
			}
			
			Stack stack = new Stack();
			for(int j=0; j<arrayFromInput.length; j++) {
				stack.push(arrayFromInput[j]);
			}
			for(int j=0; j<arrayFromInput.length; j++) {
				arrayFromInput[j] = (String) stack.pop();
			}
		}
		return numOfLists;
	}//end method
	
	
	public void alotArraysToLists(int numLists, List<String[]> input) {
		
		//make a stack with correct number of lists 
		List<List> list = new ArrayList<List>();
		for(int i=0; i<numLists; i++) {
			List l = new ArrayList();
			list.add(l);
		}
		
		for(int j=0; j<input.size(); j++) {
			String[] inputArray = input.get(j);
			for(int k=inputArray.length-1; k>=0; k--) {	
				list.get(k).add(inputArray[k]);
			}//end inner for
		}//end outer for
		
		
		for(int i=0; i<numLists; i++) {
			List l = list.get(i);
			for(int j=0; j<l.size(); j++) {
				System.out.println(l.get(j));
			}
			System.out.println("\n");
		}
		
		
	}//end method
	
}
	