import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import pi.INode;

public class Node implements INode {

	private ArrayList<INode> children;
	private ArrayList<INode> parents;
	private String name; 
	private Object data;

	public Node(String name, Object data) {
		this.name = name;
		this.data = data;
		children = new ArrayList<INode>();
		parents = new ArrayList<INode>();
	}

	public String getName() {
		return name;
	}

	public Object getData() {
		return data;
	}

	public void addChild(INode child) {
		children.add(child);
	}

	public void addParent(INode parent) {
		parents.add(parent);
	}

	public ArrayList<INode> getChildren() {
		return children;
	}

	public ArrayList<INode> getParents() {
		return parents;
	}
}
