package com.cf.Utils;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {

	public static final Integer M = Integer.MAX_VALUE;

	public static int[] dijkstra_alg(int[][] matrix, int orig, int des) {
		int N = matrix.length;
		int[][] matrix_copy = Utils.copyArray(matrix);
		int[] shortest = new int[N]; 
		String[] path = new String[N];
		for (int i = 0; i < N; i++) {
			path[i] = new String(orig + "--->" + i);
		}
		boolean[] visited = new boolean[N]; 
		shortest[orig] = 0;
		visited[orig] = true;
		
		for (int count = 1; count <= N - 1; count++) {
			
			int k = -1;
			int min = M;
			for (int i = 0; i < N; i++)
			{
				if (!visited[i] && matrix_copy[orig][i] != M) 
				{
					if (min == -1 || min > matrix_copy[orig][i]) 
					{
						min = matrix_copy[orig][i];
						k = i;
					}
				}
			}
		
			if (k == M) {
				System.out.println("the input map matrix is wrong!");
				return null;
			}
			shortest[k] = min;
			visited[k] = true;
//		
			for (int i = 0; i < N; i++) {
				if (!visited[i] && matrix_copy[k][i] != M) {
					int callen = min + matrix_copy[k][i];
					if (matrix_copy[orig][i] == M || matrix_copy[orig][i] > callen) {
						matrix_copy[orig][i] = callen;
						path[i] = path[k] + "--->" + i;
					}
				}
			}
		}
//		System.out.println("起始点" + orig + " 目的点" + des + "路径 " + path[des]);
		String[] despath = path[des].split("--->");
		int[] tmp = new int[despath.length];
		int length = 0;
		for (int i = 0; i < despath.length; i++) {
			tmp[i] = Integer.parseInt(despath[i]);
		}
		for(int i=0;i<tmp.length-1;i++){
			length+= matrix[tmp[i]] [tmp[i+1]];
		}
		if(length>=10000){
//			System.out.println(length);
			return null;
		}else{
			
			return tmp;
		}
	}
	
	public static List<Integer> dijkstra(int[][] matrix, int orig, int des) {
		int[] sp = dijkstra_alg(matrix, orig, des);
		if(sp==null) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<sp.length;i++) {
			list.add(sp[i]);
		}
		return list;
	}

	public static List<int[]> KSP(int[][] matrix, int orig, int des, int k) {
		
		List<int[]> pathList = new ArrayList<>();
		int j = 0;
		do {
			int[] tmp = dijkstra_alg(matrix, orig, des);
			pathList.add(tmp);
			for (int i = 0; i < tmp.length - 1; i++) {
				matrix[tmp[i]][tmp[i + 1]] = (Integer) 10000;
				matrix[tmp[i + 1]][tmp[i]] = (Integer) 10000;
			}

			j++;
		} while (j < k);
		return pathList;
	}

}
