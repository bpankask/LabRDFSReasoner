package labreasoner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;



public class OutputParser {
	
	public boolean readTrace(String fileName) {
		try {
		File file = new File(fileName);
		Scanner scan = new Scanner(file);
	
		while(scan.hasNext()) {
			System.out.println(scan.nextLine());
		}
		return true;
		}
		catch(Exception e) {
			System.out.println("Error");
			return false;
		}
	}
	
}
	