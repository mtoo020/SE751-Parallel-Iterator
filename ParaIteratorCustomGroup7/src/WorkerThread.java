 
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import pi.INode;
import pi.ParIterator;
import pi.reductions.Reducible;

public class WorkerThread extends Thread {
		
	private ParIterator<INode> pi;
	private int id = -1;
	private AtomicInteger atomicInt;
	
	public WorkerThread(int id, ParIterator<INode> pi, AtomicInteger atomicInt) {
		this.id = id;
		this.pi = pi;
		this.atomicInt = atomicInt;
	}

	public void run() {
		while (pi.hasNext()) {
			INode element = pi.next();
			//System.out.println(atomicInt.getAndIncrement()+" Hello from Thread "+id+", who got node: " + element.getName() +" which has the data "+element.getData());
			
			System.out.println(atomicInt.getAndIncrement()+"| Thread "+id+" is given Element "+ element.getName()+" which has the data: "+((ImageObject)element.getData()).getImageLink());
			
			// slow down the threads (to illustrate the scheduling)
			try {
				
				Random rand = new Random();
			    int randomNum = rand.nextInt((100 - 10) + 1) + 10;
				
				Thread.sleep(randomNum);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("    Thread "+id+" has finished.");
	}
}

