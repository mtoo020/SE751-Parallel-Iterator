import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIteratorFactory;

import java.util.*;

public class MainDAG {
	public static void main(String[] args){
		int threadCount = 3;
		
		GraphAdapterInterface<INode, String> dag = new GraphAdapter(createNodes());
		
		ParIterator<INode> pi = ParIteratorFactory.getTreeParIteratorDFSonDAGTopBottom(dag, dag.getRoot(), threadCount);
		
		// Create and start a pool of worker threads
		Thread[] threadPool = new WorkerThread[threadCount];
		for (int i = 0; i < threadCount; i++) {
		    threadPool[i] = new WorkerThread(i, pi);
		    threadPool[i].start();
		}
		
		// ... Main thread may compute other (independent) tasks
		
		// Main thread waits for worker threads to complete
		for (int i = 0; i < threadCount; i++) {
		    try {
		    	threadPool[i].join();
		    } catch(InterruptedException e) {
		    	e.printStackTrace();
		    }
		}
		
		System.out.println("All worker threads have completed.");
		
	}
	
	public static ArrayList<INode> createNodes(){
		
		INode a1 = new Node("B1+C1");
		INode b1 = new Node("5");
		INode c1 = new Node("128");	
		a1.addChild(b1); //this could call b1.setParent(a1) and c1.setParent(a1)
		a1.addChild(c1);
		b1.addParent(a1);
		c1.addParent(a1);
		
		ArrayList<INode> list = new ArrayList<INode>();
		list.add(a1);
		list.add(b1);
		list.add(c1);
		return list;
	}
}
