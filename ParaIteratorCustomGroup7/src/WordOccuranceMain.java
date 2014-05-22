import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pi.ParIterator;
import pi.ParIteratorFactory;
import pi.reductions.Reducible;
import pi.reductions.Reduction;


public class WordOccuranceMain {

    public static Collection<String> getElements() {
		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("two");
		list.add("three");
		list.add("three");
		list.add("three");
		list.add("four");
		list.add("four");
		list.add("four");
		list.add("four");
		return list;
    }
    
    public static void main(String[] args) {
		
		int threadCount = 2;
			
		Collection<String> elements = getElements();
		
		// Static with Block scheduling.
		ParIterator<String> pi = ParIteratorFactory.createParIterator(elements, 
				threadCount, ParIterator.Schedule.STATIC);		
		
		// The Reducible object acts like a thread-local, except thread-local values are 
		// reduced at the end.

		Reducible<HashMap<String,Integer>> localMap = new Reducible<HashMap<String,Integer>>(); 
		
		
		// Create and start a pool of worker threads
		Thread[] threadPool = new WorkerThread2[threadCount];
		for (int i = 0; i < threadCount; i++) {
		    threadPool[i] = new WorkerThread2(i, pi, localMap);
		    threadPool[i].start();
		}
			
		// ... Main thread may compute other (independant) tasks
	
		// Main thread waits for worker threads to complete
		for (int i = 0; i < threadCount; i++) {
		    try {
		    	threadPool[i].join();
		    } catch(InterruptedException e) {
		    	e.printStackTrace();
		    }
		}
		
		System.out.println("All worker threads have completed.");
		
		// define a custom reduction in the form of 2 elements into 1.
		Reduction<HashMap<String,Integer>> mapReduction = new Reduction<HashMap<String,Integer>>() {

			@Override
			public HashMap<String, Integer> reduce(
					HashMap<String, Integer> first,
					HashMap<String, Integer> second) {
				for (String word : first.keySet()) {
					if (second.containsKey(word))
						first.put(word, first.get(word)+second.get(word));
				}
				for (String word : second.keySet()) {
					if (!first.containsKey(word))
						first.put(word, second.get(word));
				}
				return first;
			}
		};
		
		HashMap<String,Integer> finalMap = localMap.reduce(mapReduction);
		for (String word : finalMap.keySet()) {
			System.out.println(word+" occured "+finalMap.get(word)+" times.");
		}
		
    }
	
}
