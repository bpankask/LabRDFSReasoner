package labreasoner;

import java.nio.file.Paths;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * This class handles all forms of output such as writing to JSON file, Text file, or console.
 * @author Brayden Pankaskie
 *
 */
public class OutputWriter {
	
	/**
	 * Writes the triples stored in the Triple class to a json file in a long array of triples.
	 * @param t
	 */
	public void writeToJSON(TripleList tl) {
		try {
			
			ObjectMapper mapper = new ObjectMapper();

			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

			// convert book object to JSON file
			writer.writeValue(Paths.get("tripleTest.json").toFile(), tl);
			
			
		} catch (Exception ex) {
		    System.out.println("...Error in Method writeToJSON in OutputWriter class...");
		}
	}
}
