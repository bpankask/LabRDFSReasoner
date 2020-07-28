# RDFS Semantic Reasoner

### Contributers:
Brayden Pankaskie

### Purpose:
This Java RDFS reasoner was designed to be flexible, custamizable, scalable, and simple to understand.  It was built to accept a wide variaty of linked data and be able to present this data in a human readable form.

### Functions and Features:
* Accept and parse linked data of diverse rdf syntax and ontologies, storing then as a querable graph.
* Convert any linked data graph to desired syntax.  (Turtle, N-Triples, NQuads, TriG, JSON-LD, RDF/XML, RDF/JSON, TriX, RDF Binary)
* Reason over linked data using comletely customized rule-set in forward, backward, or hibrid chaining.
* Create an accurate inference graph which is a full RDFS entailment of the original graph based on the RDFS entailment lema from the W3C website.

## Class Flow
![](images/ReasonerGitHub.png)

## User Manual Summery
### Reading different files
To read a linked data file or change the file being read, enter the Main.java class and observe the string variable named ontologyFile at the top of the main method.  Next place the path of the file which is to be read and run the program.
### Altering rules
To alter the rules or rule-set the reasoner uses to infer new information first go to the Rules.txt file.  All the rules and axioms which must be present for full RDFS reasoning are currently listed in this file but may be commented out using either two backslashed "//" or with a pound sign "#".  Existing rules may be changed or new rules added by following the Apache Jena Documentation for [general purpose rule engines](https://jena.apache.org/documentation/inference/index.html#rules).
### Changing output syntax
```java
ontModel.write(originalOut, "TURTLE");
```

#### Other Helpful Documenation
* [Apache Jena Documentation](https://jena.apache.org/documentation/)
provides direction on how to use Jena, an API which contains many semantic tools and methods.
* [W3C Documentation](https://www.w3.org/TR/rdf-mt/#rules)
provides all rules, entailment lemas, and standards which must be adhered to.

