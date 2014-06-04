package se751;



import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import pi.INode;
import pi.ParIterator;
import pi.reductions.Reducible;

public class WorkerThread extends Thread {

	private ParIterator<INode> pi;
	private int id = -1;
	private AtomicInteger atomicInt;
	private JLayeredPane layeredPane;
	private int offset1 = 150;
	private int offset2 = 180;
	private int offset3 = 250;

	
	public WorkerThread(int id, ParIterator<INode> pi, AtomicInteger atomicInt, JLayeredPane layeredPane) {
		this.id = id;
		this.pi = pi;
		this.atomicInt = atomicInt;
		this.layeredPane = layeredPane;
	}

	public void run() {
		while (pi.hasNext()) {
			INode element = pi.next();
			String imageLink = ((ImageObject) element.getData()).getImageLink();

			System.out.println(atomicInt.getAndIncrement() + "| Thread " + id
					+ " is given Element " + element.getName()
					+ " which has the data: " + imageLink);

			try {
				Random rand = new Random();
				int randomNum = rand.nextInt((1500 - 1000) + 1) + 1000;
				Thread.sleep(randomNum);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ImageIcon rect = createImageIcon(imageLink + ".png");
			JLabel label = new JLabel(rect);
			label.setOpaque(true);
			
			synchronized(layeredPane){
				switch (imageLink.charAt(imageLink.length() - 1)) {
				case '0':
					label.setBounds(0, 0, rect.getIconWidth(), rect.getIconHeight());
					layeredPane.add(label, 0, 0);
					break;
				case '1':
					label.setBounds(15 + offset1 * MainDAG.rect1Count, 15,
							rect.getIconWidth(), rect.getIconHeight());
					MainDAG.rect1Count++;
					layeredPane.add(label, 1, 0);
					break;
				case '2':
					label.setBounds(30 + offset2 * MainDAG.rect2Count, 50,
							rect.getIconWidth(), rect.getIconHeight());
					MainDAG.rect2Count++;
					layeredPane.add(label, 2, 0);
					break;
				case '3':
					label.setBounds(60 + offset3 * MainDAG.rect3Count, 80,
							rect.getIconWidth(), rect.getIconHeight());
					MainDAG.rect3Count++;
					layeredPane.add(label, 3, 0);
					break;
				case '4':
					label.setBounds(230, 120, rect.getIconWidth(),
							rect.getIconHeight());
					layeredPane.add(label, 4, 0);
					break;
				default:
					System.out.println("shouldn't be popping up");
					break;
				}
			}
		}
		System.out.println("    Thread " + id + " has finished.");
	}

	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = WorkerThread.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
