package labreasoner;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that holds array of triple.  Has no other job other than to provide necessary object for Jackson library to convert to JSON in the OutputWriter class.
 * @author Brayden Pankaskie
 *
 */
public class JsonFileContent {
	
	private String OntologyName;
	private Map<String,String> Prefixes;
	private String[] OriginalAxioms;
	
	public JsonFileContent(String ontologyName, Map<String, String> prefixes, String[] OriginalAxioms) {
		super();
		OntologyName = ontologyName;
		Prefixes = prefixes;
		this.OriginalAxioms = OriginalAxioms;
	}
	
	@JsonProperty("OntologyName")
	public String getOntologyName() {
		return OntologyName;
	}
	public void setOntologyName(String ontologyName) {
		OntologyName = ontologyName;
	}
	
	@JsonProperty("Prefixes")
	public Map<String, String> getPrefixes() {
		return Prefixes;
	}
	public void setPrefixes(Map<String, String> prefixes) {
		Prefixes = prefixes;
	}
	
	@JsonProperty("OriginalAxioms")
	public String[] getOriginalAxioms() {
		return OriginalAxioms;
	}
	public void setOriginalAxioms(String[] OriginalAxioms) {
		this.OriginalAxioms = OriginalAxioms;
	}
	
	
	
	
	
	


	
	
}


//Flow of classes
/*						Triple // maybe the base KB
 * 					   / 
 * 			TripleList --  Triple// first step of inferences from reasoner
 * 				|	   \ 
 * 				|	    Triple // second step etc.
 * 				|
 * 			JSON File
 * 					\
 * 					 Numpy array - Tensor
 */
