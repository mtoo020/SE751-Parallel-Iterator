import java.util.concurrent.atomic.AtomicInteger;

import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIteratorFactory;

public class MainDAG {
	public static void main(String[] args) throws Exception {
		int threadCount = 4;
		int chunkSize = 32;

		GraphAdapterInterface<INode, String> dag = new XLSParser("test2.xls").parse();
		
		long start = System.currentTimeMillis();
		
		@SuppressWarnings("unchecked")
		ParIterator<INode> pi = ParIteratorFactory.getParIteratorGuidedBFSonDAGBottomTop(dag, dag.getStartNodes(), threadCount, chunkSize);
		
		AtomicInteger atomicInt = new AtomicInteger(1);
		
		// Create and start a pool of worker threads
		Thread[] threadPool = new WorkerThread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			threadPool[i] = new WorkerThread(i, pi, atomicInt);
			threadPool[i].start();
		}

		// ... Main thread may compute other (independent) tasks

		// Main thread waits for worker threads to complete
		for (int i = 0; i < threadCount; i++) {
			try {
				threadPool[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("All worker threads have completed.");
		System.out.println("Time taken: "+(end - start)+" miliseconds");
	}
}
