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

public class GuidedBFSonDAGBottomTopWorkStealing<V> extends DynamicBFSonDAGBottomTopWorkStealing<V> {

	private final int minChunkSize;
	private static int currentChunkSize = 0;

	public GuidedBFSonDAGBottomTopWorkStealing(GraphAdapterInterface graph,
			Collection startNodes, int numOfThreads, int minChunkSize) {
		super(graph, startNodes, numOfThreads, minChunkSize * 2);
		if(currentChunkSize == 0){
		currentChunkSize = minChunkSize * 2;
		}
		this.minChunkSize = minChunkSize;
	}

	public boolean hasNext(){
		if(breakAll.get() == false){
			int id = threadID.get();


			if(localChunkStack.get(id).size() == 0){
				permissionTable[id] = true;
			}else{
				permissionTable[id] = false;
			}

			if(permissionTable[id]){ // Get free nodes.
				System.out.println("setting the localchunkstack....");
				for(int i = 0; i < currentChunkSize; i++){
					// Prevent retrieval of free nodes if chunk size quota has been filled.
					if(localChunkStack.get(id).size() < currentChunkSize){ 
						lock.lock();
						V node = freeNodeStack.poll();
						lock.unlock();

						if(node != null){
							if(!processedNodes.contains(node)){
								localChunkStack.get(id).push(node);
							}
						}else{ // Steal work (nodes) .
							V stolenNode = null;
							for (int j = 0; j < numOfThreads; j++) {
								if(localChunkStack.get(id).size() < currentChunkSize){
									stolenNode = stealNode(j);
									if (stolenNode != null){
										System.out.println("Node stolen!");
										
										if(!processedNodes.contains(stolenNode)){
											stolenNodeStack.get(id).push(stolenNode);
											localChunkStack.get(id).push(stolenNode);
										}
									}else{
										break;
									}
								}else{
									break;
								}
								
								stealingThreads.decrementAndGet();
							}
						}
					}
				}
				lock.lock();
				if(currentChunkSize > minChunkSize){
					currentChunkSize--;
				}
				lock.unlock();
				System.out.println("Thread"+ id + "localchunkstack size: " + localChunkStack.get(id).size());
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
			

}


