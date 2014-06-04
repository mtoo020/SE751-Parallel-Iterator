package se751;

import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIterator.Schedule;
import pi.ParIteratorFactory;

@SuppressWarnings("serial")
public class MainDAG extends JFrame {

	static int rect1Count = 0;
	static int rect2Count = 0;
	static int rect3Count = 0;
	
	public static void main(String[] args) throws Exception {
		int threadCount = 4;
		int chunkSize = 1;
		
		GUIController guiController = new GUIController();
		SwingUtilities.invokeLater(guiController);
		
		GraphAdapterInterface<INode, String> dag = new XLSImageParser("test4.xls").parse();

		long start = System.currentTimeMillis();

		@SuppressWarnings("unchecked")
		ParIterator<INode> pi = ParIteratorFactory
				.getTreeParIteratorBFSonDAGBottomTop(dag, dag.getStartNodes(),
						threadCount, chunkSize, Schedule.DYNAMIC, false);

		AtomicInteger atomicInt = new AtomicInteger(1);

		// Create and start a pool of worker threads
		Thread[] threadPool = new WorkerThread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			threadPool[i] = new WorkerThread(i, pi, atomicInt, guiController.getLayeredPane());
			threadPool[i].start();
		}

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
		System.out.println("Time taken: " + (end - start) + " miliseconds");
	}

}
