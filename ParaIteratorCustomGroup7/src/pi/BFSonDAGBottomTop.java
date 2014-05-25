package pi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * This class represents a Parallel Breath First Search (BFS) Iterator which
 * works on Directed Acyclic Graphs (DAGs). It returns nodes mainly in BFS order
 * from bottom to top of the DAG (i.e. Leaf nodes are returned first before the
 * Root).
 * 
 * It supports work-stealing when threads get idle.
 * 
 * @author SE750 - 2014 - Group 7 - Amruth Akoju, Mark Tooley, Kyle Jung Based
 *         of DFS iterators created by Lama Akeila.
 */
public class BFSonDAGBottomTop<V> extends ParIteratorAbstract<V> {
	// Stores the object to be retrieved when calling the next method.
	private Object[][] buffer;

	// Maps each thread id to its local stack which holds the max amount
	// specified by
	// the Chunk size.
	private ConcurrentHashMap<Integer, LinkedBlockingDeque<V>> localChunkStack;

	// Stores a boolean value for each thread to indicate whether
	// the thread should be assigned with work or not
	private volatile boolean[][] permissionTable;

	private final int chunkSize;

	private CountDownLatch latch;

	private int processedNodesNum = 0;

	private int numTreeNodes = 0;

	private AtomicBoolean breakAll = new AtomicBoolean(false);

	private GraphAdapterInterface graph;

	private V root;

	private LinkedBlockingDeque<V> stack;

	private ConcurrentLinkedQueue<V> processedNodes;

	private ConcurrentLinkedQueue<V> waitingList;
	
	private AtomicInteger stealingThreads = new AtomicInteger(0);

	public BFSonDAGBottomTop(GraphAdapterInterface graph, V root,
			int numOfThreads, int chunkSize) {
		super(numOfThreads, false);
		this.chunkSize = chunkSize;
		this.graph = graph;
		this.root = root;
		stack = new LinkedBlockingDeque<V>();
		stack.push(root);
		numTreeNodes = graph.verticesSet().size();
		
		System.out.println("Total Nodes: " + numTreeNodes);
		buffer = new Object[numOfThreads][1];
		permissionTable = new boolean[numOfThreads][1];
		permissionTable = initializePermissionTable(permissionTable);
		processedNodes = new ConcurrentLinkedQueue<V>();
		waitingList = new ConcurrentLinkedQueue<V>();
		localChunkStack = new ConcurrentHashMap<Integer, LinkedBlockingDeque<V>>();
		
		for (int i = 0; i < numOfThreads; i++) {
			localChunkStack.put(i, new LinkedBlockingDeque<V>(chunkSize));
			if (i == 0)
				localChunkStack.get(0).push(root);
		}
		
		latch = new CountDownLatch(numOfThreads);
	}
	
	// Gives permission to the first thread
	private boolean[][] initializePermissionTable(boolean[][] permissionTable) {

		for (int i = 0; i < numOfThreads; i++) {
			if (i == 0) {
				permissionTable[0][0] = true;
			} else {
				permissionTable[i][0] = false;
			}
		}
		return permissionTable;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public V next() {
		return null;
	}

	@Override
	public boolean localBreak() {
		return false;
	}

}
