package labreasoner;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.vocabulary.ReasonerVocabulary;

public class Main {

	public static void main(String[] args) throws Exception{
		
		final long startTime = System.nanoTime();
		
		//Input ontology came from this url http://www.semanticbible.com/ntn/ntn-view.html
		String readOntFileName = "C:\\Users\\Brayden Pankaskie\\Desktop\\BibleNames.rdf";
		String ruleFile = "C:\\Users\\Brayden Pankaskie\\Desktop\\LabRDFSReasoner\\LabReasoner\\PrimaryRules.txt";
		String traceFileName = "C:\\Users\\Brayden Pankaskie\\Desktop\\trace.txt";
		
		//other classes needed
	  	OutputParser op = new OutputParser();
		OutputWriter ow = new OutputWriter();
		
		//create empty ont model
    	OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    	
    	
    	//reads in the ontology at specified location
    	ontModel.read(readOntFileName);
    	
    	//reason over and trace results
    	reasonAndTrace(ontModel,ruleFile,traceFileName);
    	
    	//parse trace file and format triples into newList
    	List<List> newList = parseTraceFile(op, traceFileName);
    	
    	//write original KB and new triples to json
    	writeToJson(ontModel, op, ow, newList);
    		
    	final long duration = System.nanoTime() - startTime;
    	System.out.println(duration + " nano seconds run time.");
		
	}//end main
	
	/**
	 * Method to create custom reasoner reason over ontology and record the trace of every rule producing a new triple
	 * @param ontModel
	 * @param ruleFile
	 * @param traceFileName
	 * @throws FileNotFoundException
	 */
	public static void reasonAndTrace(OntModel ontModel, String ruleFile, String traceFileName) throws FileNotFoundException {
		
		//load rules
    	List<Rule> rules = Rule.rulesFromURL(ruleFile);
    	
    	//creates reasoner with custom rules and enables tracing
    	Reasoner reasoner = new GenericRuleReasoner(rules);
    	reasoner.setDerivationLogging(true);
    	reasoner.setParameter(ReasonerVocabulary.PROPtraceOn, Boolean.TRUE);
    	
    	//creates an inference model using custom reasoner and the read in ontology model
    	//contains the original KG and inferences
    	InfModel inf = ModelFactory.createInfModel(reasoner, ontModel); 	
    	
    	//stuff to write trace to a file
    	PrintWriter out = new PrintWriter(traceFileName);
    	for (StmtIterator i = inf.listStatements(); i.hasNext(); ) {
    	    Statement s = i.nextStatement();
    	    for (Iterator id = inf.getDerivation(s); id.hasNext(); ) {
    	        Derivation deriv = (Derivation) id.next();
    	        //System.out.println(deriv.toString());
    	        deriv.printTrace(out, true);
    	    }
    	} 
    	out.flush();
	}
	
	/**
	 * Method to parse entailment triples from reasoner and get them into correct format
	 * @param op
	 * @param traceFileName
	 * @return
	 */
	public static List<List> parseTraceFile(OutputParser op, String traceFileName) {
		
		//beginning to parse data	
    	//puts raw data into list to be parsed
    	List<StringBuilder> listOfConclutionsAndSteps = op.readTrace(traceFileName);
    	
    	//splits each line of string builder in listOfConclutionsAndSteps into an array of lines
    	List<String[]> listOfArrays = new ArrayList<String[]>();
    	for(int i=0; i<listOfConclutionsAndSteps.size(); i++) {
    		listOfArrays.add(listOfConclutionsAndSteps.get(i).toString().split("\n"));
    	}
    	
    	//gets the list of conclusions and steps without the facts which are found in original KB
    	List<String[]> listOfArraysStripped = new ArrayList<String[]>();
    	for(int j=0; j<listOfArrays.size(); j++) {
    		listOfArraysStripped.add(op.factStripper(listOfArrays.get(j)));
    	}
    	
    	//gets the number of Lists needed and reverses the arrays in listOfArraysStripped
    	int numOfLists = op.getNumOfLists(listOfArraysStripped);
    	
    	//gets a list of lists where the first list are the first steps in 
    	List<List> newList = op.alotArraysToLists(numOfLists, listOfArraysStripped); 
    	
    	op.deleteDupl(newList);
    	
    	op.deleteIllegChar(newList);
    	
    	return newList;
    	
	}
	
	/**
	 * Method to take all the triples from the original KB and entailment triples and write them to json file
	 * @param ontModel
	 * @param op
	 * @param ow
	 * @param newList
	 */
	public static void writeToJson(OntModel ontModel, OutputParser op, OutputWriter ow, List<List> newList) {
		
		List<Triple> tl = new ArrayList<Triple>();
    	
    	//getting base model and writing 
    	Model base = ontModel.getBaseModel();
    	String[] strArr1 = op.getTripleArray(base);
    	Triple t = new Triple(strArr1);
    	tl.add(t);
    	
    	
    	for (int i = 0; i < newList.size(); i++) {
    		List l = newList.get(i);
    		String[] strArr2 = (String[]) l.toArray(new String[l.size()]);
    		t = new Triple(strArr2);
    		tl.add(t);
		}
    	
    	Triple[] tripleArray = tl.toArray(new Triple[tl.size()]);
    	
    	TripleList tripleList = new TripleList(tripleArray);
    	
    	ow.writeToJSON(tripleList);
    	
	}

}//end class
