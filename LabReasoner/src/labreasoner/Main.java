/**
 * Creator: Brayden Pankaskie
 * 	 Email: bpankask@ksu.edu
 * 
 * Semantic Reasoner which accepts ontologies in various forms and creates a graph which is a full rdfs entailment of the original.
 * Note that not all classes in this project are used for this purpose and only the main and reasonAndTrace methods have been fully completed.
 */
package labreasoner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.BuiltinRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasoner;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import org.apache.jena.datatypes.xsd.impl.XMLLiteralType;
import org.apache.jena.datatypes.BaseDatatype ;



public class Main {

	public static void main(String[] args) throws Exception{
		
		//performance checking
		final long startTime = System.nanoTime();
		
		//input file to be reasoned on
		String ontologyFile = "";
		//text file containing rules and axioms
		String ruleFile = "Rules.txt";
		//output file used to trace triples that were added during reasoning
		String traceFileName = "trace.txt";
		//output file to print original ontology if desired
		String originalOntologyPrint = "originalOntology.txt"; 
		//output file used to store created graph from reasoning
		String reasonedOntology = "reasonedOntology.txt";
		
		/*instances of classes used for parsing traced information and writing it to json if desired *not completely tested*
	  	OutputParser op = new OutputParser();
		OutputWriter ow = new OutputWriter();
		*/
		
		//create empty ont model
    	OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    	
    	//reads in the ontology at specified location
    	ontModel.read(ontologyFile);
    	
    	//write original ontology to a file converting it to turtle syntax
    	PrintWriter originalOut = new PrintWriter(originalOntologyPrint);    
    	ontModel.write(originalOut, "TURTLE");
    	
    	//writes original ontology to console *only useful for small files*
    	//ontModel.write(System.out,"TURTLE");   
    	
    	//reason over and trace results
    	reasonAndTrace(ontModel,ruleFile,traceFileName, reasonedOntology);
    	
    	//parse trace file and format triples into newList
    	//List<List> newList = parseTraceFile(op, traceFileName);
    	
    	//write original KB and new triples to json
    	//writeToJson(ontModel, op, ow, newList);
    		
    	//performance checking
    	final long duration = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    	System.out.println(duration + " seconds run time.");
		 
		
	}//end main
	 
	/**
	 * Method to create custom reasoner reason over ontology and record the trace of every rule producing a new triple
	 * @param ontModel
	 * @param ruleFile
	 * @param traceFileName
	 * @throws FileNotFoundException
	 */
	public static void reasonAndTrace(OntModel ontModel, String ruleFile, String traceFileName, String reasonedOntology) throws FileNotFoundException {
		
		//registers the class CustomBuiltIn so that it can be used in the rule file for rdfs2
    	BuiltinRegistry.theRegistry.register(new CustomBuiltIn());
		
		//load rules
    	List<Rule> rules = Rule.rulesFromURL(ruleFile);
    	
    	//creates reasoner with custom rules and enables tracing
    	Reasoner reasoner = new GenericRuleReasoner(rules);
    	reasoner.setDerivationLogging(true);
    	reasoner.setParameter(ReasonerVocabulary.PROPtraceOn, Boolean.TRUE);
    	
    	//creates an inference model using custom reasoner and the read in ontology model
    	//contains the original KG and inferences
    	InfModel inf = ModelFactory.createInfModel(reasoner, ontModel); 
    	
    	//print new graph to a file in turtle
    	PrintWriter reasonedOnt = new PrintWriter(reasonedOntology);
    	inf.write(reasonedOnt, "TURTLE");
     
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
	 * @throws FileNotFoundException 
	 */
	public static List<List> parseTraceFile(OutputParser op, String traceFileName) throws FileNotFoundException {
		
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
    	
    	op.delUnwanted(newList);
    	
    	PrintWriter pw = new PrintWriter("trace.txt");
    	for (int j = 0; j < newList.size(); j++) {
    	 	List list2 = newList.get(j);
    		for (int i = 0; i < list2.size(); i++) {
    			pw.write(list2.get(i).toString() + "\n");
    		}
    		pw.write("\n");
    	}
    	pw.flush();
    	return newList;	
	}
	
	/**
	 * Method to take all the triples from the original KB and entailment triples and write them to json file
	 * @param ontModel
	 * @param op
	 * @param ow
	 * @param newList
	 */
	public static void writeToJson(OntModel ontModel, OutputParser op, OutputWriter ow, List<List> newList)  {
    	
    	//getting base model of triples and storing them in the triple class 
    	Model base = ontModel.getBaseModel();
    	
    	//returns list of the base triples
    	List masterList = op.getTripleArray(base);
    	
    	//getting the rest of the inferred triples and storing them in the masterList list with the base triples
    	for (int i = 0; i < newList.size(); i++) {
    		List l = newList.get(i);
    		for (int j = 0; j < l.size(); j++) {
				masterList.add(l.get(j));
			}
		}
    	
    	//instance of Triple class for the JsonFileContent class
    	String[] tripleArray = (String[]) masterList.toArray(new String[masterList.size()]);
    	
    	//Jena method which gets prefixes from ontology
    	Map<String,String> map = ontModel.getNsPrefixMap();
    	
    	//Jena method to get the name of the ontology
    	String ontName = ontModel.getNsPrefixURI("");
    	
    	//creates instance of the class used in writing to Json file
    	JsonFileContent jsonFileContent = new JsonFileContent(ontName,map,tripleArray);
    	
    	ow.writeToJSON(jsonFileContent);
 
/*
    	//stuff for json file more suited for Brayden
    	List<Triple> tl = new ArrayList<Triple>();
    	
    	//getting base model of triples and storing them in the triple class 
    	Model base = ontModel.getBaseModel();
    	String[] strArr1 = op.getTripleArray(base);
    	Triple t = new Triple(strArr1);
    	tl.add(t);
    	
    	//storing the rest of the inferred triples and storing them in the triple class
    	for (int i = 0; i < newList.size(); i++) {
    		List l = newList.get(i);
    		String[] strArr2 = (String[]) l.toArray(new String[l.size()]);
    		t = new Triple(strArr2);
    		tl.add(t);
		}
*/
	}

}//end class
