package com.cf.DAO;

import java.io.BufferedReader;
import java.io.FileInputStream;

import java.io.InputStreamReader;
import java.util.Scanner;

import org.junit.Test;

import com.cf.Utils.DB;
import com.cf.domain.DC;
import com.cf.domain.Link;


public class GraphDao3 {
	
	private static Scanner scanner = new Scanner(System.in);
	
   
	public int[][] generateGraph(){
//		String file = "E:/workspace/me/EmulationCL/src/update.txt";
		String file = "/EmulationCL/src/graph2.txt";
		try {
			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			int n = Integer.valueOf(bufferedReader.readLine());
			String tmp = null;
			int [][] matrix = new int[n][n];
			while((tmp=bufferedReader.readLine()) != null){
				int a = Integer.valueOf(tmp.split(" ")[0]);
				int b = Integer.valueOf(tmp.split(" ")[1]);
				matrix[a-1][b-1] = 20;
				matrix[b-1][a-1] = 20;				
			}
			convertMatrix(matrix);
			initDC(matrix);
			initLink(matrix);
			initLink();
			return matrix;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void initDC(int[][] matrix){
		
		for(int i=0;i<6;i++){
			DC tmpdc = new DC();
			tmpdc.setCPUCapacity(1000);
			tmpdc.setResidualCPU(1000);
//			int nodeId = getphysicsId(i);
//			tmpdc.setDcId(nodeId);
			tmpdc.setMatrixId(i);
			DB.dcList.add(tmpdc);
		}		
	}
	
	public int getphysicsId(int i){
		if(i<=6){
			return i+46; 
		}
		if(i>7 && i<=14){
			return i-7;
		}
		switch(i){
		case 15:return 17;
		case 16:return 18;
		case 17:return 20;
		case 18:return 22;
		case 19:return 23;
		case 20:return 25;
		case 21:return 27;
		case 22:return 19;
		case 23:return 21;
		case 24:return 16;
		case 25:return 24;
		case 26:return 26;		
		}
		return -1;
	}
	@Test
	public void testConvertDC(){
		int[][] matrix = generateGraph();
		initDC(matrix);
	}
	
	private static void convertMatrix(int[][] matrix){
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix.length;j++){
				matrix[i][j]=matrix[i][j]==0?10000:matrix[i][j];
			}
		}
	}
	private void initLink(int[][] matrix){
		for(int i =0;i<matrix.length;i++){
			for(int j =0;j<matrix.length;j++){
				if(matrix[i][j]==20){
					Link l1 =new Link(i,j,1000,1000,20);
					DB.linkList.add(l1);
				}
			}
		}
	}
	
	public void initLink(){
		for(int i=0;i<6;i++){
			Link l1 = DB.getLink(i, i+6);
			l1.setTotalBW(Integer.MAX_VALUE);
			l1.setResidualBW(Integer.MAX_VALUE);
			Link l2 = DB.getLink(i+6, i);
			l2.setTotalBW(Integer.MAX_VALUE);
			l2.setResidualBW(Integer.MAX_VALUE);
			Link l3 = DB.getLink(i+6, i+12);
			l3.setTotalBW(Integer.MAX_VALUE);
			l3.setResidualBW(Integer.MAX_VALUE);
			Link l4 = DB.getLink(i+12, i+6);
			l4.setTotalBW(Integer.MAX_VALUE);
			l4.setResidualBW(Integer.MAX_VALUE);
		}
	}
	
}
