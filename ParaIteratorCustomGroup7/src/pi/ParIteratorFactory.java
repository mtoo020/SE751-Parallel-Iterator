/*
 *  Copyright (C) 2009 Nasser Giacaman, Oliver Sinnen, Lama Akeila
 *
 *  This file is part of Parallel Iterator.
 *
 *  Parallel Iterator is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at 
 *  your option) any later version.
 *
 *  Parallel Iterator is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 *  Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along 
 *  with Parallel Iterator. If not, see <http://www.gnu.org/licenses/>.
 */

package pi;

import java.util.*;

import pi.util.DynamicList;

/**
 * This class defines the functionality of a Parallel Iterator factory. The
 * methods return a {@link ParIterator} instance depending on the respective
 * parameters provided.
 *
 * @param <E>
 *            The type of the elements in the collection.
 * 
 * @author Nasser Giacaman
 * @author Oliver Sinnen
 * @author Lama Akeila
 */
public class ParIteratorFactory<E> {

	private ParIteratorFactory() {
	}

	/**
	 * 
	 * Creates a Parallel Iterator for the specified <code>collection</code>,
	 * using the specified schedule policy and chunk size. The number of threads
	 * that will be accessing the Parallel Iterator must also be provided.
	 * 
	 * @param collection
	 *            The collection that contains the elements to be shared amongst
	 *            the threads.
	 * @param threadCount
	 *            The number of threads that will be sharing the Parallel
	 *            Iterator.
	 * @param schedulePol
	 *            The schedule to use to distribute the elements.
	 * @param chunksize
	 *            The chunk size used in the scheduling policy.
	 * @param ignoreBarrier
	 *            If the implicit barrier should be turned off (default
	 *            <code>false</code>.
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <E> ParIterator<E> createParIterator(
			Collection<E> collection, int threadCount,
			ParIterator.Schedule schedulePol, int chunksize,
			boolean ignoreBarrier) {
		return getIterator(collection, schedulePol, chunksize, threadCount,
				ignoreBarrier);
	}

	/**
	 * 
	 * Creates a Parallel Iterator for the specified <code>collection</code>,
	 * using the specified schedule policy and chunk size. The number of threads
	 * that will be accessing the Parallel Iterator must also be provided.
	 * 
	 * @param collection
	 *            The collection that contains the elements to be shared amongst
	 *            the threads.
	 * @param threadCount
	 *            The number of threads that will be sharing the Parallel
	 *            Iterator.
	 * @param schedulePol
	 *            The schedule to use to distribute the elements.
	 * @param chunksize
	 *            The chunk size used in the scheduling policy.
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <E> ParIterator<E> createParIterator(
			Collection<E> collection, int threadCount,
			ParIterator.Schedule schedulePol, int chunksize) {
		return getIterator(collection, schedulePol, chunksize, threadCount,
				false);
	}

	/**
	 * A convenience method for
	 * {@link #createParIterator(Collection, int, ParIterator.Schedule, int)}.
	 * <p>
	 * Returns a Parallel Iterator with a dynamic scheduling policy and chunk
	 * size of 1.
	 * 
	 * @param collection
	 *            The collection that contains the elements to be shared amongst
	 *            the threads.
	 * @param threadCount
	 *            The number of threads that will be sharing the Parallel
	 *            Iterator.
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <E> ParIterator<E> createParIterator(
			Collection<E> collection, int threadCount) {
		return getIterator(collection, ParIterator.Schedule.DYNAMIC,
				ParIterator.DEFAULT_CHUNKSIZE, threadCount, false);
	}

	/**
	 * A convenience method for
	 * {@link #createParIterator(Collection, int, ParIterator.Schedule, int)}.
	 * <p>
	 * Chunk size used:
	 * <ul>
	 * <li>1 if the specified <code>schedule</code> is <code>DYNAMIC</code> or
	 * <code>GUIDED</code>.
	 * <li><i>Block chunk</i> if the specified <code>schedule</code> is
	 * <code>STATIC</code>.
	 * </ul>
	 * 
	 * @param collection
	 *            The collection that contains the elements to be shared amongst
	 *            the threads.
	 * @param threadCount
	 *            The number of threads that will be sharing the Parallel
	 *            Iterator.
	 * @param schedulePol
	 *            The schedule to use to distribute the elements.
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <E> ParIterator<E> createParIterator(
			Collection<E> collection, int threadCount,
			ParIterator.Schedule schedulePol) {
		return getIterator(collection, schedulePol,
				ParIterator.DEFAULT_CHUNKSIZE, threadCount, false);
	}

	/**
	 * Returns a Parallel Iterator, where the collection is specified in the
	 * form of an array.
	 * 
	 * @param <E>
	 *            The type of the elements in the array.
	 * @param collection
	 *            The array containing the elements to be shared amongst the
	 *            threads.
	 * @param threadCount
	 *            The number of threads that will be sharing the Parallel
	 *            Iterator.
	 * @param schedulePol
	 *            The schedule to use to distribute the elements.
	 * @param chunksize
	 *            The chunk size used in the scheduling policy.
	 * @param ignoreBarrier
	 *            If the implicit barrier should be turned off (default
	 *            <code>false</code>.
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <E> ParIterator<E> createParIterator(E[] collection,
			int threadCount, ParIterator.Schedule schedulePol, int chunksize,
			boolean ignoreBarrier) {
		return getIterator(Arrays.asList(collection), schedulePol, chunksize,
				threadCount, ignoreBarrier);
	}

	/**
	 * Returns a Parallel Iterator, where the collection is specified in the
	 * form of a uniform range of integers.
	 * 
	 * @param start
	 *            The first value in the set.
	 * @param size
	 *            The number of elements to create.
	 * @param increment
	 *            The increment amongst the values.
	 * @param threadCount
	 *            The number of threads that will be sharing the Parallel
	 *            Iterator.
	 * @param schedulePol
	 *            The schedule to use to distribute the elements.
	 * @param chunksize
	 *            The chunk size used in the scheduling policy.
	 * @param ignoreBarrier
	 *            If the implicit barrier should be turned off (default
	 *            <code>false</code>.
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static ParIterator<Integer> createParIterator(int start, int size,
			int increment, int threadCount, ParIterator.Schedule schedulePol,
			int chunksize, boolean ignoreBarrier) {
		// TODO instead of creating a buffer, should only be using the integer
		// range..

		DynamicList nums = new DynamicList(start, size, increment);

		// ArrayList<Integer> nums = new ArrayList<Integer>();
		// int currentValue = start;
		// for (int i = 0; i < size; i++) {
		// nums.add(currentValue);
		// currentValue+=increment;
		// }
		return getIterator(nums, schedulePol, chunksize, threadCount,
				ignoreBarrier);
	}

	/**
	 * Returns a DFS Parallel Iterator with Local Stacks and work stealing
	 * mechanism. The Iterator works on Trees.
	 * 
	 * @param tree
	 *            The tree to be traversed in DFS
	 * @param root
	 *            The starting node of the search
	 * @param numOfThreads
	 *            The number of threads that will be sharing the DFS Parallel
	 *            Iterator.
	 * 
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <V> ParIterator getParDFSWorkStealingIterator(
			GraphAdapterInterface tree, V root, int numOfThreads) {
		return getParDFSWorkStealing(tree, root, numOfThreads);

	}

	/**
	 * Returns a DFS Parallel Iterator with Local Stacks and work stealing
	 * mechanism. The Iterator works on Trees and Directed Acyclic Graphs
	 * (DAGs). It returns nodes in DFS order from top to bottom.
	 * 
	 * @param tree
	 *            The tree or DAG to be traversed in DFS
	 * @param root
	 *            The starting node of the search
	 * @param numOfThreads
	 *            The number of threads that will be sharing the DFS Parallel
	 *            Iterator.
	 * 
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <V> ParIterator getTreeParIteratorDFSonDAGTopBottom(
			GraphAdapterInterface tree, V root, int numOfThreads) {
		return getTreeIteratorDFSonDAGs(tree, root, numOfThreads);

	}

	/**
	 * Returns a DFS Parallel Iterator with Local Stacks and work stealing
	 * mechanism. The Iterator works on Trees and Directed Acyclic Graphs
	 * (DAGs). It returns nodes in DFS order from bottom to top.
	 * 
	 * @param graph
	 *            The tree or DAG to be traversed in DFS
	 * @param root
	 *            The starting node of the search
	 * @param numOfThreads
	 *            The number of threads that will be sharing the DFS Parallel
	 *            Iterator.
	 * 
	 * @return An instance of <code>ParIterator</code>.
	 */
	public static <V> ParIterator getTreeParIteratorDFSonDAGBottomTop(
			GraphAdapterInterface graph, V root, int numOfThreads) {
		return getTreeIteratorDFSonDAGBottomTop(graph, root, numOfThreads);

	}

	/**
	 * This class represents a Parallel Breath First Search (BFS) Iterator which
	 * works on Directed Acyclic Graphs (DAGs). It returns nodes mainly in BFS
	 * order from bottom to top of the DAG (i.e. Leaf nodes are returned first
	 * before the Root).
	 * 
	 * @param graph
	 *            The tree or DAG to be traversed in DFS
	 * @param root
	 *            The starting node of the search (Will be replaced by starting
	 *            nodes)
	 * @param numOfThreads
	 *            The number of threads that will be sharing the DFS Parallel
	 *            Iterator.
	 * @param chunkSize
	 *            Max number of nodes a single thread can process at a single
	 *            time.
	 * 
	 * @return An instance of <code>ParIterator</code>.
	 */

	/**
	 * This class represents a Parallel Breath First Search (BFS) Iterator which
	 * works on Directed Acyclic Graphs (DAGs). It returns nodes mainly in BFS
	 * order from bottom to top of the DAG (i.e. Leaf nodes are returned first
	 * before the Root).
	 * 
	 * @param graph
	 *            The tree or DAG to be traversed in DFS
	 * @param root
	 *            The starting node of the search (Will be replaced by starting
	 *            nodes)
	 * @param numOfThreads
	 *            The number of threads that will be sharing the DFS Parallel
	 *            Iterator.
	 * @param chunkSize
	 *            Max number of nodes a single thread can process at a single
	 *            time.
	 * @param schedulePol
	 *            The scheduling policy used by the ParIterator
	 * @param workStealing
	 *            Whether to allow threads stealing work from others when it is
	 *            idle.
	 * @return
	 */
	public static <V> ParIterator getTreeParIteratorBFSonDAGBottomTop(
			GraphAdapterInterface graph, Collection<V> startNodes,
			int numOfThreads, int chunkSize, ParIterator.Schedule schedulePol,
			boolean workStealing, boolean checkForCycles) {

		if (!workStealing) {
			switch (schedulePol) {
			case STATIC:
				System.out
						.println("Opps, static scheduling is not available. Using Dynamic Scheduling.");
				return new DynamicBFSonDAGBottomTop(graph, startNodes,
						numOfThreads, chunkSize, checkForCycles);
			case GUIDED:
				return new GuidedBFSonDAGBottomTop(graph, startNodes,
						numOfThreads, chunkSize, checkForCycles);
			case DYNAMIC:
				return new DynamicBFSonDAGBottomTop(graph, startNodes,
						numOfThreads, chunkSize, checkForCycles);
			case MEMORYAWARE:
				System.out
						.println("Memory Aware is not available. Using Dynamic Scheduling.");
				return new DynamicBFSonDAGBottomTop(graph, startNodes,
						numOfThreads, chunkSize, checkForCycles);
			default:
				throw new RuntimeException("Unknown schedule: " + schedulePol);
			}
		} else {
			switch (schedulePol) {
			case STATIC:
				System.out
						.println("Opps, static scheduling is not available. Using Dynamic Scheduling.");
				return new DynamicBFSonDAGBottomTopWorkStealing(graph,
						startNodes, numOfThreads, chunkSize, checkForCycles);
			case GUIDED:
				return new GuidedBFSonDAGBottomTopWorkStealing(graph,
						startNodes, numOfThreads, chunkSize, checkForCycles);
			case DYNAMIC:
				return new DynamicBFSonDAGBottomTopWorkStealing(graph,
						startNodes, numOfThreads, chunkSize, checkForCycles);
			case MEMORYAWARE:
				System.out
						.println("Memory Aware is not available. Using Dynamic Scheduling.");
				return new DynamicBFSonDAGBottomTopWorkStealing(graph,
						startNodes, numOfThreads, chunkSize, checkForCycles);
			default:
				throw new RuntimeException("Unknown schedule: " + schedulePol);
			}

		}

	}

	public static <V> ParIterator getParIteratorGuidedBFSonDAGBottomTop(
			GraphAdapterInterface graph, List<V> startNodes, int numOfThreads,
			int chunkSize) {
		return getParIteratorGuidedBFSonDAGBottomTop(graph, startNodes,
				numOfThreads, chunkSize);
	}

	/*
	 * This method instantiates the right class based on the parameters passed
	 * by the user
	 * 
	 * @return an instance of the required class that represents the required
	 * schedule scheme provided by the user parameters. The default schedule is
	 * Dynamic
	 */
	private static <E> ParIterator<E> getIterator(Collection<E> collection,
			ParIterator.Schedule schedulePol, int chunksize, int threadCount,
			boolean ignoreBarrier) {
		switch (schedulePol) {
		case STATIC:
			return new StaticParIterator<E>(collection, chunksize, threadCount,
					ignoreBarrier);
		case GUIDED:
			return new GuidedParIterator<E>(collection, chunksize, threadCount,
					ignoreBarrier);
		case DYNAMIC:
			return new DynamicParIterator<E>(collection, chunksize,
					threadCount, ignoreBarrier);
		case MEMORYAWARE:
			return new MemoryAwareParIterator<E>(collection, chunksize,
					threadCount, ignoreBarrier);
		default:
			throw new RuntimeException("Unknown schedule: " + schedulePol);
		}
	}

	private static <V> ParIterator getParDFSWorkStealing(
			GraphAdapterInterface tree, V root, int numOfThreads) {
		return new DFSWorkStealing(tree, root, numOfThreads);
	}

	public static <V> ParIterator getTreeIteratorDFSonDAGBottomTop(
			GraphAdapterInterface tree, V root, int numOfThreads) {
		return new DFSonDAGBottomTop(tree, root, numOfThreads);
	}

	public static <V> ParIterator getTreeIteratorDFSonDAGs(
			GraphAdapterInterface tree, V root, int numOfThreads) {
		return new DFSonDAGs(tree, root, numOfThreads);
	}

	public static ParIterator<INode> getTreeParIteratorDFSonDAGBottomTop(
			GraphAdapterInterface<INode, String> dag, INode root,
			int threadCount, int chunkSize) {
		// TODO Auto-generated method stub
		return null;
	}
}
