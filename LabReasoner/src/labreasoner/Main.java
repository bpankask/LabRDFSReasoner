package labreasoner;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
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
		
		//Input ontology came from this url http://www.semanticbible.com/ntn/ntn-view.html
		String readOntFileName = "C:\\Users\\Brayden Pankaskie\\Desktop\\BibleNames.rdf";
		String ruleFile = "C:\\Users\\Brayden Pankaskie\\Desktop\\PrimaryRules.txt";
		String traceFileName = "C:\\Users\\Brayden Pankaskie\\Desktop\\trace.txt";
		
		/*
		//create empty ont model
    	OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    	
    	//reads in the ontology at specified location
    	ontModel.read(readOntFileName);
    	
    	//load rules
    	List<Rule> rules = Rule.rulesFromURL(ruleFile);
    	
    	//creates reasoner with custom rules and enables tracing
    	Reasoner reasoner = new GenericRuleReasoner(rules);
    	reasoner.setDerivationLogging(true);
    	reasoner.setParameter(ReasonerVocabulary.PROPtraceOn, Boolean.TRUE);
    	
    	//creates an inference model using custom reasoner and the read in ontology model
    	//contains the original KG and inferences
    	InfModel inf = ModelFactory.createInfModel(reasoner, ontModel); 	
    	
    	//write model to screen
    	//inf.getDeductionsModel().write(System.out,"TURTLE");
    	
    
    	
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
    	*/

    	OutputParser op = new OutputParser();
    	
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
    	
    	int numOfLists = op.getNumOfLists(listOfArraysStripped);
    	
    	op.alotArraysToLists(numOfLists, listOfArraysStripped);  	
    	
    	
	}//end main

}//end class
