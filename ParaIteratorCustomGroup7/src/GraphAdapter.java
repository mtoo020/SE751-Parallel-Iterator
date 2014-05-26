
import java.util.ArrayList;
import java.util.Collection;

import pi.GraphAdapterInterface;
import pi.INode;

public class GraphAdapter implements GraphAdapterInterface<INode, String> {

	private ArrayList<INode> nodes;
	private ArrayList<INode> startNodes;

	public GraphAdapter(ArrayList<INode> nodes, ArrayList<INode> startNodes) {
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

	public ArrayList<INode> getStartNodes() {
		return startNodes;
	}
}
