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
import java.util.concurrent.locks.ReentrantLock;

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
public class DynamicBFSonDAGBottomTopWorkStealing<V> extends ParIteratorAbstract<V> {
	// Stores the object to be retrieved when calling the next method.
	protected Object[][] buffer;

	// Maps each thread id to its local stack which holds the max amount
	// specified by
	// the Chunk size.
	protected ConcurrentHashMap<Integer, LinkedBlockingDeque<V>> localChunkStack;

	// Stores a boolean value for each thread to indicate whether
	// the thread should be assigned with work or not
	protected volatile boolean[] permissionTable;

	protected final int chunkSize;

	protected CountDownLatch latch;

	protected int processedNodesNum = 0;

	protected int numTreeNodes = 0;

	protected AtomicBoolean breakAll = new AtomicBoolean(false);

	protected GraphAdapterInterface graph;

	protected LinkedBlockingDeque<V> freeNodeStack;

	protected ConcurrentLinkedQueue<V> processedNodes;

	protected ConcurrentLinkedQueue<V> waitingList;
	
	protected AtomicInteger stealingThreads = new AtomicInteger(0);

	protected final ReentrantLock lock = new ReentrantLock();
	
	// Keep track of nodes stolen. Can be used to prevent stolen nodes from taken back by original owner thread.
	protected LinkedBlockingDeque<V> stolenNodeStack;

	/**
	 * 
	 * @param graph - DAG graph that is being iterated over
	 * @param root - root of the DAG tree
	 * @param freeNodeList - Starting nodes that are essentially the initial freeNodes.
	 * @param numOfThreads - number of threads running
	 * @param chunkSize - max number of nodes assigned to a thread at a time.
	 */
	public DynamicBFSonDAGBottomTopWorkStealing(GraphAdapterInterface graph, Collection<V> startNodes, int numOfThreads, int chunkSize) {
		super(numOfThreads, false);
		this.chunkSize = chunkSize;
		this.graph = graph;
		this.freeNodeStack = new LinkedBlockingDeque<V>();
		numTreeNodes = graph.verticesSet().size();
		
		System.out.println("Total Nodes: " + numTreeNodes);
		
		buffer = new Object[numOfThreads][1];
		permissionTable = new boolean[numOfThreads];
		permissionTable = initializePermissionTable(permissionTable);
		processedNodes = new ConcurrentLinkedQueue<V>();
		waitingList = new ConcurrentLinkedQueue<V>();
		localChunkStack = new ConcurrentHashMap<Integer, LinkedBlockingDeque<V>>();
		
		stolenNodeStack = new LinkedBlockingDeque<V>();
		
		// Initialise freeNodeStack with the nodes from the startNodeList
		for(V n : startNodes){
			freeNodeStack.add(n);
		}
		
		System.out.println(freeNodeStack.size());
		
		for (int i = 0; i < numOfThreads; i++) {
			localChunkStack.put(i, new LinkedBlockingDeque<V>(chunkSize));
			//stolenNodeStack.put(new LinkedBlockingDeque<V>());
		}
		
		latch = new CountDownLatch(numOfThreads);
	}
	
	// Give all threads permission at the start.
	protected boolean[] initializePermissionTable(boolean[] permissionTable) {
		for (int i = 0; i < numOfThreads; i++) {
			permissionTable[i] = true;
		}
		return permissionTable;
	}
	
	@Override
	public boolean hasNext() {
		if(breakAll.get() == false){
			int id = threadID.get();
			
			if(localChunkStack.get(id).size() == 0){
				permissionTable[id] = true;
			}else{
				permissionTable[id] = false;
			}
			
			// Retrieve free nodes to fill up chunk size quota.
			if(permissionTable[id]){ // Get free nodes.
				for(int i = 0; i < chunkSize; i++){
					// Prevent retrieval of free nodes if chunk size quota has been filled.
					if(localChunkStack.get(id).size() < chunkSize){ 
						lock.lock();
						V node = freeNodeStack.poll();
						lock.unlock();
						
						if(node != null){
							if(!processedNodes.contains(node)){
								localChunkStack.get(id).push(node);
							}
						}else{ // Attempt to steal work.
							stealingThreads.incrementAndGet();
							if(stealingThreads.get() == numOfThreads){
								stealingThreads.decrementAndGet();
								continue;
							}else{ // Steal work (nodes) .
								V stolenNode = null;
								for (int j = 0; j < numOfThreads; j++) {
									if(localChunkStack.get(id).size() < chunkSize){
										stolenNode = stealNode(j);
										if (stolenNode != null){
											System.out.println("Thread: "+id+" stole the node "+((INode)stolenNode).getName()+" from Thread "+j);
											
											if(!processedNodes.contains(stolenNode)){
												stolenNodeStack.push(stolenNode);
												localChunkStack.get(id).push(stolenNode);
												
												break;
											}
										}else{
											continue;
										}
									}
									
								}
								stealingThreads.decrementAndGet();

							}
						}
					}
				}
			}
			
			V nextNode = getLocalNode();
			if(nextNode != null){
				buffer[id][0] = nextNode;
				processedNodes.add(nextNode);
				checkFreeNodes(nextNode);
				return true;
			}
			
			if(processedNodes.size() == numTreeNodes){
				exit(latch);
				return false;
			}
			
		}
		exit(latch);
		return false;
	}
	
	/**
	 * Returns a stolen node from the stack of the target
	 * @param target
	 * @return
	 */
	protected V stealNode(int target) {
		int id = threadID.get();	
		
		if(target != id){
			//V currentStackNode =  localChunkStack.get(target).peek();
			
			Iterator it = localChunkStack.get(target).iterator();
			
			while(it.hasNext()){
				V currentStackNode = (V) it.next();
				// checks that all the children of the node are processed, if not
				// add it to waiting list and call method again of the target
				if(processedNodes.containsAll(graph.getChildrenList(currentStackNode)) && !processedNodes.contains(currentStackNode)){
					if(!stolenNodeStack.contains(currentStackNode)){
						localChunkStack.get(target).remove(currentStackNode);
						return currentStackNode;
					}
				}
				waitingList.add(currentStackNode);
			}
		}
		return null;
	}
	
	
	/**
	 * Threads call this method to exit.
	 * @param latch
	 */
	protected void exit(CountDownLatch latch) {
		latch.countDown(); // Sign off thread.
		try {
			latch.await(); // Wait for other threads to sign off.
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception");
		}
	}
	
	/**
	 * @return node from the local stack of the thread.
	 */
	private synchronized V getLocalNode() {
		int id = threadID.get();	

		V localNode = localChunkStack.get(id).poll();
		
		if(localNode != null){
			if(processedNodes.containsAll(graph.getChildrenList(localNode)) && !processedNodes.contains(localNode)){


				
				return localNode;
			}else{
				waitingList.add(localNode);
				return getLocalNode();
			}
		}else{
			return null;
		}
	}

	
	/**
	 * Check if any of the parents (nodes to be processed next) have become free nodes.
	 * @param node
	 */
	protected void checkFreeNodes(V node){
		int id = threadID.get();

		@SuppressWarnings("unchecked")
		Iterator<V> it = graph.getParentsList(node).iterator();
		
		V parent;
		while(it.hasNext()){	
			parent = it.next();
			if(processedNodes.containsAll(graph.getChildrenList(parent)) && !processedNodes.contains(parent)){
				// Parent has become a free node.
				freeNodeStack.offerLast(parent);
				
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V next() {
		int id = threadID.get();
		V nextNode = (V) buffer[id][0];		
		return nextNode;
	}

	@Override
	public boolean localBreak() {
		return false;
	}

}
