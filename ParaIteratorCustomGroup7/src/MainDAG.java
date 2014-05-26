import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIteratorFactory;

public class MainDAG {
	public static void main(String[] args) throws Exception {
		int threadCount = 2;
		int chunkSize = 2;

		GraphAdapterInterface<INode, String> dag = new XLSParser("test.xls").parse();

		//@SuppressWarnings("unchecked")
		//ParIterator<INode> p_old = ParIteratorFactory.getTreeParIteratorDFSonDAGTopBottom(dag, dag.getRoot(), threadCount);
		
		@SuppressWarnings("unchecked")
		ParIterator<INode> pi = ParIteratorFactory.getTreeIteratorBFSonDAGBottomTop(dag, dag.getStartNodes(), threadCount, chunkSize);
		
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("All worker threads have completed.");
	}
}
