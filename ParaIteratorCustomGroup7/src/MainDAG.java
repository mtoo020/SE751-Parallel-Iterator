import java.awt.Color;
import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

import pi.GraphAdapterInterface;
import pi.INode;
import pi.ParIterator;
import pi.ParIterator.Schedule;
import pi.ParIteratorFactory;

public class MainDAG extends JFrame {
	
	static JLayeredPane d = new JLayeredPane();
	static int rect1Count = 0;
	static final int offset1 = 150;
	static int rect2Count = 0;
	static final int offset2 = 180;
	static int rect3Count = 0;
	static final int offset3 = 250;

	
	public static void main(String[] args) throws Exception {
		int threadCount = 4;
		int chunkSize = 1;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame a = new JFrame("Overlapping images");
				d.setVisible(true);

//				JLabel label = new JLabel();
//				label.setBackground(Color.red);
//				label.setBounds(0, 0, 140, 140);
//				label.setForeground(Color.black);
//				label.setOpaque(true);
//				d.add(label,1);

				d.setPreferredSize(new Dimension(700, 500));
				d.setBorder(BorderFactory.createTitledBorder(
						"Move the Mouse to Move Duke"));
				a.setSize(700, 700);


				JComponent newContentPane = new JPanel();
				newContentPane.add(d);
				newContentPane.setVisible(true);
				newContentPane.setOpaque(true); //content panes must be opaque
				a.setContentPane(newContentPane);
				a.setVisible(true);
			}});

		GraphAdapterInterface<INode, String> dag = new XLSImageParser("test4.xls").parse();

		long start = System.currentTimeMillis();

		@SuppressWarnings("unchecked")
		ParIterator<INode> pi = ParIteratorFactory
		.getTreeParIteratorBFSonDAGBottomTop(dag, dag.getStartNodes(), threadCount, chunkSize, Schedule.DYNAMIC, false);

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
