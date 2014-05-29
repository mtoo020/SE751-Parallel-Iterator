package pi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import pi.util.ThreadID;

/**
 * 
 * This class represents a Parallel Breath First Search (BFS) Iterator which
 * works on Directed Acyclic Graphs (DAGs). It returns nodes mainly in BFS order
 * from bottom to top of the DAG (i.e. Leaf nodes are returned first before the
 * Root).
 * 
 * It supports work-stealing when threads get idle.
 * 
 * @author SE750 - 2014 - Group 7 - Amruth Akoju, Mark Tooley, Kyle Jung based
 *         on DFS iterators created by Lama Akeila.
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
	private volatile boolean[] permissionTable;

	private final int chunkSize;

	private CountDownLatch latch;

	private int processedNodesNum = 0;

	private int numTreeNodes = 0;

	private AtomicBoolean breakAll = new AtomicBoolean(false);

	private GraphAdapterInterface graph;

//	private V root;

	private LinkedBlockingDeque<V> freeNodeStack;

	private ConcurrentLinkedQueue<V> processedNodes;

	private ConcurrentLinkedQueue<V> waitingList;
	
	private AtomicInteger stealingThreads = new AtomicInteger(0);

	/**
	 * 
	 * @param graph - DAG graph that is being iterated over
	 * @param root - root of the DAG tree
	 * @param freeNodeList - Starting nodes that are essentially the initial freeNodes.
	 * @param numOfThreads - number of threads running
	 * @param chunkSize - max number of nodes assigned to a thread at a time.
	 */
	public BFSonDAGBottomTop(GraphAdapterInterface graph, Collection<V> startNodes, int numOfThreads, int chunkSize) {
		super(numOfThreads, false);
		this.chunkSize = chunkSize;
		this.graph = graph;
//		this.root = root;
		this.freeNodeStack = new LinkedBlockingDeque<V>();
		numTreeNodes = graph.verticesSet().size();
		
		System.out.println("Total Nodes: " + numTreeNodes);
		
		buffer = new Object[numOfThreads][1];
		permissionTable = new boolean[numOfThreads];
		permissionTable = initializePermissionTable(permissionTable);
		processedNodes = new ConcurrentLinkedQueue<V>();
		waitingList = new ConcurrentLinkedQueue<V>();
		localChunkStack = new ConcurrentHashMap<Integer, LinkedBlockingDeque<V>>();
		
		// Initialise freeNodeStack with the nodes from the startNodeList
		for(V n : startNodes){
			freeNodeStack.add(n);
		}
			
		for (int i = 0; i < numOfThreads; i++) {
			localChunkStack.put(i, new LinkedBlockingDeque<V>(chunkSize));
		}
		
		latch = new CountDownLatch(numOfThreads);
	}
	
	// Give all threads permission at the start.
	private boolean[] initializePermissionTable(boolean[] permissionTable) {
		for (int i = 0; i < numOfThreads; i++) {
			permissionTable[i] = true;
		}
		return permissionTable;
	}

	@Override
	public boolean hasNext() {
		int id = threadID.get();
		
		if(permissionTable[id]){
			//System.out.println("Thread "+id+" wants nodes to process.");
			
			// Grab one free node if possible then attempt to grab parent nodes of the first node.
			if(freeNodeStack.size() != 0){
				V node = null;
				
				// Store first available free node in thread's local stack.
				node = freeNodeStack.pollFirst();
				localChunkStack.get(id).offerFirst(node);
				
				while(localChunkStack.get(id).size() < chunkSize){
					@SuppressWarnings("unchecked")
					Iterator<V> it = graph.getParentsList(node).iterator();
					
					// Push the parent nodes into the local stack if there is space.
					while(it.hasNext()){
						V nextNode = it.next();
						if(!localChunkStack.get(id).contains(nextNode)){
							localChunkStack.get(id).offerFirst(nextNode);
						}
					}
				}

				permissionTable[id] = false;
			}else{
				System.out.println("Thread "+id+" has no free nodes to process.");
			}
		}		
		
		//System.out.println("Thread: "+id+" LocalChunkStack size: "+localChunkStack.get(id).size());
		
		if(breakAll.get() ==  false){			
			// Retrieve node from local stack and store it in buffer
			V node = getLocalNode();
			if(node != null){
				buffer[id][0] = node;
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @return node from the local stack of the thread.
	 */
	private V getLocalNode() {
		int id = threadID.get();
		V currentStackNode = localChunkStack.get(id).pollLast();
		
		if(currentStackNode != null){
			if(processedNodes.containsAll(graph.getChildrenList(currentStackNode)) && !processedNodes.contains(currentStackNode)){
				return currentStackNode;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V next() {
		int id = threadID.get();
		
		V nextNode = (V) buffer[id][0];
		synchronized(processedNodes){
			if(!processedNodes.contains(nextNode)){
				processedNodes.add(nextNode);
			}
		}
		
		return nextNode;
	}

	@Override
	public boolean localBreak() {
		return false;
	}

}
