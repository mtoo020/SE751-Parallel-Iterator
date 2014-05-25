import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIteratorFactory;

import java.util.*;

public class MainDAG {
	public static void main(String[] args){
		int threadCount = 2;
		int chunkSize = 2;
		
		GraphAdapterInterface<INode, String> dag = new GraphAdapter(createNodes());
		
		@SuppressWarnings("unchecked")
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
		
		INode a1 = new Node("A1","B1+C1");
		
		INode b1 = new Node("B1","B2+B3");
		INode b2 = new Node("B2","5");
		INode b3 = new Node("B3","7");
		
		INode c1 = new Node("C1","C2+C3");	
		INode c2 = new Node("C2","8");
		INode c3 = new Node("C3","2");
		
		a1.addChild(b1); //this could call b1.setParent(a1) and c1.setParent(a1)
		a1.addChild(c1);
		
		b1.addParent(a1);
		c1.addParent(a1);
		
		b1.addChild(b2);
		b1.addChild(b3);
		b2.addParent(b1);
		b3.addParent(b1);
		
		c1.addChild(c2);
		c1.addChild(c3);
		
		c2.addParent(c1);
		c3.addParent(c1);

		ArrayList<INode> list = new ArrayList<INode>();
		list.add(a1);
		
		list.add(b1);
		list.add(b2);
		list.add(b3);

		list.add(c1);
		list.add(c2);
		list.add(c3);

		return list;
	}
}
