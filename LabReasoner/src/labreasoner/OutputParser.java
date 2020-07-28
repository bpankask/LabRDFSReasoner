/**
 * Class to take output from the reasoner trace and format it for an LSTM in Tensorflow.
 */
package labreasoner;

import java.io.File;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class OutputParser {
	
	/**
	 * Takes a Jena model passed from the main class and extracts all the triples from it.
	 * @param model
	 * @return strArr
	 */
	public List getTripleArray(Model model) {
		
		List<String> list = new ArrayList<String>();
				
		StmtIterator iter = model.listStatements();

		while(iter.hasNext()) {
			
			StringBuilder sb = new StringBuilder();
			
			Statement stmt      = iter.nextStatement();// get next statement
			Resource  subject   = stmt.getSubject();   // get the subject
			Property  predicate = stmt.getPredicate(); // get the predicate
			RDFNode   object    = stmt.getObject();    // get the object

			sb.append(subject + " ");
			sb.append(predicate + " ");
			sb.append(object);
			
			list.add(sb.toString());
			
			//in case you need to distinguish resource from literal
//			if (object instanceof Resource) {
//				
//			} else {
//				// object is a literal
//			}
		}//end of while loop
	
		return list;
	}
	
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
		
		//list to store individual steps that were meshed together in previous list
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
	 * This method first gets the number of lists that will be needed to store each stage or step in the reasoning process
	 * It then reverses the order of the array "input" so that the first entailment that was reasoned from the original KB is at position zero in each array instead of the last entailment
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
	
	/**
	 * This method loops through a List of String arrays and adds them to the corresponding position (array[0] -> list.get(0).add(array[0]) for the purpose of grouping 
	 * entailments from the same step together.
	 * @param numLists
	 * @param input
	 * @return
	 */
	public List<List> alotArraysToLists(int numLists, List<String[]> input) {
		
		//make a list with correct number of lists inside list 
		List<List> list = new ArrayList<List>();
		for(int i=0; i<numLists; i++) {
			List l = new ArrayList();
			list.add(l);
		}
		
		//loops through input list and grabs out each String array
		for(int j=0; j<input.size(); j++) {
			String[] inputArray = input.get(j);
			//loops through the array from input list starting from the last spot in the array and adds the contents
			//to the corresponding list created in this method
			for(int k=inputArray.length-1; k>=0; k--) {	
				list.get(k).add(inputArray[k]);
			}//end inner for
		}//end outer for
		
		return list;
	}//end method
	
	/**
	 * Method to remove all duplicates within a list
	 * Note it will not remove duclicates if they are in sepereate lists because this means the reasoner came to this conclusion using different information
	 * @param inputList
	 */
	public void deleteDupl(List<List> inputList) {
		
		for (int i = 0; i < inputList.size(); i++) {
    		List l = inputList.get(i);
    		//loops through l
			for (int j = 0; j < l.size()-1; j++) {
				for (int j2 = j+1; j2 < l.size(); j2++) {
					if(((String) l.get(j)).contentEquals((String)l.get(j2)))
						l.remove(j2);
				}//end inner for
			}//end middle for
		}//end outer for
	}//end method	
	
	/**
	 * First deletes all duplicates within each list in listList but will not delete duplicates between one list and another list, only duplicates within each list. It will also delete all characters
	 * around the triples.  Note the actually triples of two statements could be the same but if they were derived by different rules they will be considered different and not removed.
	 * @param listList
	 */
	public void delUnwanted(List<List> listList) {

		for (int i = 0; i < listList.size(); i++) {

			List l = listList.get(i);

			for (int j = 0; j < l.size(); j++) {

				//loop to check for any duplicates in list l
				for (int j2 = j+1; j2 < l.size(); j2++) {
					if(((String) l.get(j)).contentEquals((String)l.get(j2)))
						l.set(j2, "remove");

				}//end j2 loop
				//----

				//deletes all characters not in the triple that were put there in the trace from Jena
				String s = ((String) l.get(j));

				if(s != "remove") {
					char[] charArr = s.toCharArray();

					int beginChar = 0;
					int endChar = 0;

					//finds the beginning of triple
					for (int k = 0; k < charArr.length; k++) {
						if(charArr[k] == '(') {
							beginChar = k+1;
							k = charArr.length;
						}
					}

					//finds the end of triple
					for (int k = charArr.length-1; k >= 0; k--) {
						if(charArr[k] == ')') {
							endChar = k;
							k = -1;
						}
					}

					//string containing triple and a couple of characters to be removed
					String str = s.substring(beginChar, endChar);

					//split triple into subject predicate and object
					String[] subPredObj = str.split(" ");
					String finalStr = "";

					//loop through the subject predicate and object
					for (int k = 0; k < subPredObj.length; k++) {
						//turn them to character arrays
						char[] spoArr = subPredObj[k].toCharArray();
						String spoStr = "";
						if(spoArr[0] == '<') {
							spoStr = subPredObj[k].substring(1, subPredObj[k].length()-1);
						}
						else {
							spoStr = subPredObj[k];
						}
						finalStr += spoStr + " ";
					}

					l.set(j, finalStr);
					//----
				}
			}
		}//end outer for loop
		
		for (List list : listList) {
			while(list.remove("remove") == true) {
			}
		}
		
	}//end method
}
	