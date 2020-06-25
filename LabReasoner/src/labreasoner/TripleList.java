package labreasoner;

/**
 * Class that holds array of triple.  Has no other job other than to provide necessary object for Jackson library to convert to JSON in the OutputWriter class.
 * @author Brayden Pankaskie
 *
 */
public class TripleList {
	
	private Triple[] tripleList;

	public TripleList(Triple[] tripleList) {
		super();
		this.tripleList = tripleList;
	}

	public Triple[] getTripleList() {
		return tripleList;
	}

	public void setTripleList(Triple[] tripleList) {
		this.tripleList = tripleList;
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
