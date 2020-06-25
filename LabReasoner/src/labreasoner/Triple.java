package labreasoner;

/**
 * Class which holds an array of triples in String form 
 * @author Brayden Pankaskie
 *
 */
public class Triple {
	
	public String[] triples;

	public Triple () {
		
	}
	
	public Triple(String[] triples) {
		super();
		this.triples = triples;
	}

	public String[] getTriples() {
		return triples;
	}

	public void setTriples(String[] triples) {
		this.triples = triples;
	}
	
}
