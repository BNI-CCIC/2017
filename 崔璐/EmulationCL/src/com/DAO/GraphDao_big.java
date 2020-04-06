package com.cf.DAO;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.cf.Utils.Utils;

public class GraphDao_big {

	private static Scanner scanner = new Scanner(System.in);
	
	   
	public int[][] generateGraph(){
//		String file = "E:/workspace/me/EmulationCL/src/graph2.txt";
		String file = "E:/myeclipse/workspace/EmulationCL/src/big_graph.txt";
		try {
			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//			int n = Integer.valueOf(bufferedReader.readLine());
//			String tmp = null;

			int n = Integer.valueOf(bufferedReader.readLine()); // number of nodes
			int m = Integer.valueOf(bufferedReader.readLine()); // number of links
			int [][] matrix = new int[n][n];
			for(int i=0;i<m;i++) {
				int a = Integer.valueOf(bufferedReader.readLine());
				int b = Integer.valueOf(bufferedReader.readLine());
				double c = Double.valueOf(bufferedReader.readLine());
				matrix[a-1][b-1] = 1;
			}
			return matrix;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		GraphDao_big dao = new GraphDao_big();
		int[][] matrix = dao.generateGraph();
		System.out.println(matrix.length);
		Utils.printMatrix(matrix);
	}
}
