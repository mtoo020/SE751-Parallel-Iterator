package se751;



import java.util.ArrayList;
import java.util.Collection;

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
}
