package se751;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pi.GraphAdapterInterface;
import pi.INode;

public class GraphAdapter implements GraphAdapterInterface<INode, String> {

	private Collection<INode> nodes;
	private Collection<INode> startNodes;

	public GraphAdapter(Collection<INode> nodes, Collection<INode> startNodes) {
		this.nodes = nodes;
		this.startNodes = startNodes;
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

	public Collection<INode> getStartNodes() {
		return startNodes;
	}

	public boolean hasCycles() {
		HashMap<INode,Boolean> marks = new HashMap<INode,Boolean>();

		for (INode node : startNodes) {
			if (visit(node,marks)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean visit(INode node,HashMap<INode,Boolean> marks) {
		Boolean mark = marks.get(node);
		//not marked
		if (mark == null) {
			marks.put(node, false);
			for (INode parent : node.getParents()) {
				visit(parent,marks);
			}
			marks.put(node, true);
		}
		//temporarily marked
		else if (!mark) {
			return true;
		}
		
		return false;
	}
}
