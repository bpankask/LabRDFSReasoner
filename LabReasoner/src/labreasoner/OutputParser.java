package labreasoner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;




public class OutputParser {
	
	
	/**
	 * Reads the trace of the inferences
	 * If we add rules that need more than two facts we will need to change this method to accommodate
	 * @param fileName
	 * @return
	 */
	public boolean readTrace(String traceFile, String outPutFile) {
		
		//holds every line in trace file
		List<String> sl = new ArrayList<String>();
		
		try {
		File file = new File(traceFile);
		Scanner scan = new Scanner(file);
		PrintWriter pw = new PrintWriter(outPutFile);
		
		//puts every line from the file to the list of strings
		while(scan.hasNext()) {
			String line = scan.nextLine().strip();
			sl.add(line);
		}
		
		//string builder to store the conclusion and the facts it used to find it or a "step"
		StringBuilder sb = new StringBuilder();
		//loops through list looking for a line that starts with "Fact (" which indicates the end of a step
		//
		for(int i=0; i<sl.size(); i++) {
			String l = sl.get(i);
			sb.append(l + "\n");
			
			//checks to see if the line starts with Fact (
			if(l.substring(0, 6).contentEquals("Fact (")) {
				//some Fact ( lines will have a second fact line
				if(i+1 < sl.size()) {
					String l2 = sl.get(i+1);
					if(l2.substring(0, 5) == "Fact (") {
						sb.append(l2);
					}
				}
				//writes sb to file only when it finds a fact line
				pw.println(sb.toString());
				//then clears string builder
				sb = new StringBuilder();
			}
		}
		//flushes print writer 
		pw.flush();
		
		return true;
		
		}
		catch(Exception e) {
			System.out.println("...Error in Method readTrace in OutputParser class...");
			return false;
		}
	}
	
}
	