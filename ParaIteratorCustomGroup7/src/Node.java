import java.util.ArrayList;
import java.util.Arrays;

import pi.INode;


public class Node implements INode {
	
	private ArrayList<INode> children;
	private ArrayList<INode> parents;
	private String formula, name;
	
	public Node(String name, String formula) {
		this.name = name;
		this.formula = formula;
		children = new ArrayList<INode>();
		parents = new ArrayList<INode>();
	}

	public void addChild(INode child){
		children.add(child);
	}
	
	public void addParent(INode parent){
		parents.add(parent);
	}
	
	public ArrayList<INode> getChildren() {
		return children;
	}

	public ArrayList<INode> getParents() {
		return parents;
	}

	public String getData() {
		return formula;
	}

	@Override
	public String getName() {
		return name;
	}	
}
