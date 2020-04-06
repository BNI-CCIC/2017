package com.cf.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.junit.Test;

import com.cf.Utils.Dijkstra;
import com.cf.Utils.Utils;

public class KShortestPath {
	/**
	 * function YenKSP(Graph, source, sink, K): // Determine the shortest path from
	 * the source to the sink. A[0] = Dijkstra(Graph, source, sink); // Initialize
	 * the set to store the potential kth shortest path. B = [];
	 * 
	 * for k from 1 to K: // The spur node ranges from the first node to the next to
	 * last node in the previous k-shortest path. for i from 0 to size(A[k − 1]) −
	 * 2:
	 * 
	 * // Spur node is retrieved from the previous k-shortest path, k − 1. spurNode
	 * = A[k-1].node(i); // The sequence of nodes from the source to the spur node
	 * of the previous k-shortest path. rootPath = A[k-1].nodes(0, i);
	 * 
	 * for each path p in A: if rootPath == p.nodes(0, i): // Remove the links that
	 * are part of the previous shortest paths which share the same root path.
	 * remove p.edge(i,i + 1) from Graph;
	 * 
	 * for each node rootPathNode in rootPath except spurNode: remove rootPathNode
	 * from Graph;
	 * 
	 * // Calculate the spur path from the spur node to the sink. spurPath =
	 * Dijkstra(Graph, spurNode, sink);
	 * 
	 * // Entire path is made up of the root path and spur path. totalPath =
	 * rootPath + spurPath; // Add the potential k-shortest path to the heap.
	 * B.append(totalPath);
	 * 
	 * // Add back the edges and nodes that were removed from the graph. restore
	 * edges to Graph; restore nodes in rootPath to Graph;
	 * 
	 * if B is empty: // This handles the case of there being no spur paths, or no
	 * spur paths left. // This could happen if the spur paths have already been
	 * exhausted (added to A), // or there are no spur paths at all - such as when
	 * both the source and sink vertices // lie along a "dead end". break; // Sort
	 * the potential k-shortest paths by cost. B.sort(); // Add the lowest cost path
	 * becomes the k-shortest path. A[k] = B[0]; B.pop();
	 * 
	 * return A;
	 */
	List<List<Integer>> KSP(int[][] matrix, int orig, int des, int K) {
		int[][] sp_matrix = Utils.copyArray(matrix);
		List<List<Integer>> KSP = new ArrayList<List<Integer>>();
		Stack<Integer> stack = new Stack<>();
		List<Integer> sp1 = Dijkstra.dijkstra(sp_matrix, orig, des);
		KSP.add(sp1);
		List<List<Integer>> EX_KSP = new ArrayList<>();
		for (int k = 0; k < K - 1; k++) {
			int len = KSP.get(k).size();
			List<Integer> base = KSP.get(KSP.size() - 1);
			for (int i = 0; i < len - 1; i++) {
				int[][] cp_matrix = Utils.copyArray(matrix);

				Set<Integer> set = findRepetition(base, i, KSP);
				cp_matrix = removeLink(cp_matrix, base, i, set);
				List<Integer> tmp = Dijkstra.dijkstra(cp_matrix, base.get(i), des);

				if (tmp == null) {
					continue;
				} else {
					List<Integer> res = concat(base, i, tmp);

					boolean isadd = isAddEX_KSP(res, EX_KSP);
					if (isadd) {
						EX_KSP.add(res);
					}
				}

			}
			// find the shortest path from EX_KSP
			List<Integer> obj = findShostest(matrix, EX_KSP);
			KSP.add(obj);
			// remove obj
			Iterator<List<Integer>> it = EX_KSP.iterator();
			while (it.hasNext()) {
				if (isequalsLink(obj, it.next())) {
					it.remove();
					break;
				}
			}
		}
		return KSP;
	}

	public List<Integer> concat(List<Integer> base, int index, List<Integer> tmp) {
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < index; i++) {
			res.add(base.get(i));
		}
		for (int i = 0; i < tmp.size(); i++) {
			res.add(tmp.get(i));
		}
		return res;
	}

	public boolean isAddEX_KSP(List<Integer> obj, List<List<Integer>> EX_KSP) {

		for (int k = 0; k < EX_KSP.size(); k++) {
			List<Integer> tmp = EX_KSP.get(k);
			boolean flag = isequalsLink(tmp, obj);
			if (flag) {
				return false;
			}
		}
		return true;
	}

	public boolean isequalsLink(List<Integer> tmp, List<Integer> obj) {
		if (tmp.size() != obj.size()) {
			return false;
		}
		for (int i = 0; i < obj.size(); i++) {
			if (tmp.get(i) != obj.get(i)) {
				return false;
			}
		}
		return true;
	}

	public Set<Integer> findRepetition(List<Integer> tmp, int index, List<List<Integer>> KSP) {
		Set<Integer> set = new HashSet<Integer>();
		for (int k = 0; k < KSP.size(); k++) {
			for (int i = 0; i <= index; i++) {
				if (KSP.get(k).get(i) != tmp.get(i)) {
					break;
				}
				if (i == index) {
					set.add(KSP.get(k).get(index + 1));
				}
			}
		}
		return set;
	}

	public int[][] removeLink(int[][] matrix, List<Integer> tmp, int index, Set<Integer> set) {
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			int cur = it.next();
			matrix[tmp.get(index)][cur] = 10000;
			matrix[cur][tmp.get(index)] = 10000;
		}
		return matrix;
	}

	public List<Integer> findShostest(int[][] matrix, List<List<Integer>> EX_KSP) {

		List<Integer> result = null;
		;
		int cost = Integer.MAX_VALUE;
		for (int i = 0; i < EX_KSP.size(); i++) {
			int tmp_cost = 0;
			for (int j = 0; j < EX_KSP.get(i).size() - 1; j++) {
				tmp_cost += matrix[EX_KSP.get(i).get(j)][EX_KSP.get(i).get(j + 1)];
				if (tmp_cost < cost) {
					result = EX_KSP.get(i);
					cost = tmp_cost;
				}
			}
		}
		return result;
	}

	@Test
	public void testKSP() {
		int[][] matrix = new int[][] { { 10000, 3, 2, 10000, 10000, 10000 }, { 10000, 10000, 10000, 4, 10000, 10000 },
				{ 10000, 1, 10000, 2, 3, 10000 }, { 10000, 10000, 10000, 10000, 2, 1 },
				{ 10000, 10000, 10000, 10000, 10000, 2 }, { 10000, 10000, 10000, 10000, 10000, 10000 } };
//		Utils.printMatrix(matrix);
//		int[] sp = Dijkstra.dijkstra_alg(matrix, 0, 5);
//		Utils.printMatrix(sp);
		List<List<Integer>> KSP = KSP(matrix, 0, 5, 3);
		for (int k = 0; k < KSP.size(); k++) {
			for (int i = 0; i < KSP.get(k).size(); i++) {
				System.out.print(KSP.get(k).get(i) + " ");
			}
			System.out.println();
		}
	}

}
