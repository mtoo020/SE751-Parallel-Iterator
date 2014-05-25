package pi;

import java.util.ArrayList;

public interface INode {
	public String getName();
	
	public void addChild(INode child);
	public void addParent(INode parent);
	
	public ArrayList<INode> getChildren();
	public ArrayList<INode> getParents();
	public String getFormula();
}
