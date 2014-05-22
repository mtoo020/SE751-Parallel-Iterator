import pi.ParIterator;
import pi.ParIteratorFactory;
import java.util.*;

public class MainApp {
	
    public static Collection<String> getElements() {
		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");
		list.add("five");
		list.add("six");
		list.add("seven");
		list.add("eight");
		return list;
    }

    public static void main(String[] args) {
		
			int threadCount = 2;
				
			Collection<String> elements = getElements();
			
			// Get a Parallel Iterator for the collection of elements
			ParIterator<String> pi;
			/* Following are ParIterator instantiation with different scheduling policies. */
					
			// Dynamic with chunk size of 1.
			//pi = ParIteratorFactory.createParIterator(elements, threadCount);
			
			// Dynamic with chunk size of 3.
			//pi = ParIteratorFactory.createParIterator(elements, threadCount, ParIterator.Schedule.DYNAMIC, 3); 
			
			// Static scheduling. Static Block scheduling since no chunk size is specified.
			//pi = ParIteratorFactory.createParIterator(elements, threadCount, ParIterator.Schedule.STATIC); 
			
			// Static scheduling with chunk size of 3.
			//pi = ParIteratorFactory.createParIterator(elements, threadCount, ParIterator.Schedule.STATIC, 2); 
			
			// Guided scheduling with chunk size of 1.
			pi = ParIteratorFactory.createParIterator(elements, threadCount, ParIterator.Schedule.GUIDED, 1); 
			
			
			// Create and start a pool of worker threads
			Thread[] threadPool = new WorkerThread[threadCount];
			for (int i = 0; i < threadCount; i++) {
			    threadPool[i] = new WorkerThread(i, pi);
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
	    }
}