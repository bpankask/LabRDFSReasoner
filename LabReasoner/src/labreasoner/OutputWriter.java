package labreasoner;

import java.nio.file.Paths;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
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
	public void writeToJSON(JsonFileContent jsonFC) {
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

			// convert book object to JSON file
			mapper.writeValue(Paths.get("outputInJSON.json").toFile(), jsonFC);
			
			
		} catch (Exception ex) {
		    System.out.println("...Error in Method writeToJSON in OutputWriter class...");
		}
	}
}
