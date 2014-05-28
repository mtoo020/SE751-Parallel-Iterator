import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import pi.INode;

public class Node implements INode {

	private ArrayList<INode> children;
	private ArrayList<INode> parents;
	private String name, formula;
	private AtomicInteger parentsProcessed;
	private AtomicBoolean processed;

	public Node(String name, String formula) {
		this.name = name;
		this.formula = formula;
		children = new ArrayList<INode>();
		parents = new ArrayList<INode>();
		parentsProcessed = new AtomicInteger(0);
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return formula;
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
	
	public boolean isFree() {
		return parents.size() == parentsProcessed.get();
	}

	public boolean getProcessed() {
		return processed.get();
	}

	public void markAsProcessed() {
		processed.set(true);
		for (INode node : children) {
			((Node) node).incrementParentsProcessed();
		}
	}

	private void incrementParentsProcessed() {
		parentsProcessed.incrementAndGet();
	}
}
