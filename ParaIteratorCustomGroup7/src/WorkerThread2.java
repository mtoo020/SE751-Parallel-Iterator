 
import java.util.HashMap;

import pi.ParIterator;
import pi.reductions.Reducible;

public class WorkerThread2 extends Thread {
		
	private ParIterator<String> pi = null;
	private int id = -1;
	private Reducible<HashMap<String,Integer>> localMap;
	
	public WorkerThread2(int id, ParIterator<String> pi,  Reducible<HashMap<String,Integer>> localMap) {
		this.id = id;
		this.pi = pi;
		this.localMap = localMap;
	}

	public void run() {
		HashMap<String, Integer> myMap = new HashMap<String, Integer>();
		localMap.set(myMap);
		while (pi.hasNext()) {
			String element = pi.next();
			System.out.println("Thread "+id+" got element: "+element);
			if (myMap.containsKey(element)) {
				int oldCount = myMap.get(element);
				myMap.put(element, oldCount+1);				
			} else {
				myMap.put(element, 1);
			}
		}
		System.out.println("    Thread "+id+" has finished.");
	}
}

