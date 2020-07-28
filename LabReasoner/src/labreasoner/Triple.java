package labreasoner;

/**
 * Class which holds an array of triples in String form 
 * @author Brayden Pankaskie
 *
 */
public class Triple {

	private String[] triples;
	
	
	public Triple(String[] OriginalAxioms) {
		this.triples = OriginalAxioms;
	}
	
	public Triple () {
		
	}

	public String[] getTriples() {
		return triples;
	}

	public void setTriples(String[] OriginalAxioms) {
		this.triples = OriginalAxioms;
	}
	
}
