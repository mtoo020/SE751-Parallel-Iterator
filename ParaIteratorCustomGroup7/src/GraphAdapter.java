

import java.util.ArrayList;
import java.util.Collection;

import pi.GraphAdapterInterface;
import pi.INode;

public class GraphAdapter implements GraphAdapterInterface<INode, String> {

	private ArrayList<INode> nodes;
	
	public GraphAdapter(ArrayList<INode> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<INode> getChildrenList(Object v) {
		return ((INode) v).getChildren();
	}

	public ArrayList<INode> getParentsList(Object v) {
		return ((INode) v).getParents();
	}

	public Collection<INode> verticesSet() {
		return nodes;
	}

	public Collection<String> edgesSet() {
		return null;
	}
	
	public INode getRoot() {
		// TODO Need to refactor this so the root is only calculated once somewhere.
		// We do not want to loop everytime we want the root.
		for(INode n : nodes){				
			if(n.getParents().size() == 0){
				return n;
			}
		}
		return null;		
	}
}
