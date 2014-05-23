 
import pi.INode;
import pi.ParIterator;
import pi.reductions.Reducible;

public class WorkerThread extends Thread {
		
	private ParIterator<INode> pi;
	private int id = -1;

	public WorkerThread(int id, ParIterator<INode> pi) {
		this.id = id;
		this.pi = pi;
	}

	public void run() {
		while (pi.hasNext()) {
			INode element = pi.next();
			System.out.println("Hello from Thread "+id+", who got element: "+element.getFormula());
			
			
			// slow down the threads (to illustrate the scheduling)
			try {
				if (id == 0)
					Thread.sleep(50);
				if (id == 1)
					Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("    Thread "+id+" has finished.");
	}
}

