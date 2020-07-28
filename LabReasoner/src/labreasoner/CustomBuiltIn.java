package labreasoner;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.reasoner.rulesys.Builtin;
import org.apache.jena.reasoner.rulesys.RuleContext;

public class CustomBuiltIn implements Builtin {

	
	@Override
	public boolean bodyCall(Node[] arg0, int arg1, RuleContext arg2) {
		// TODO Auto-generated method stud
		
		if(arg0[0].getLiteralIsXML()) {
			return true;
		}
		else
			return false;
	}

	@Override
	public int getArgLength() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "isWellFormedXML";
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void headAction(Node[] arg0, int arg1, RuleContext arg2) {		
	}

	@Override
	public boolean isMonotonic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSafe() {
		// TODO Auto-generated method stub
		return false;
	}

}
