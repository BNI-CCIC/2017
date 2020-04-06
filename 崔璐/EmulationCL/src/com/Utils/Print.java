package com.cf.Utils;

import java.util.List;

public class Print<T> {
	
	public  void  print(List<T> list){
		for(T t: list){
			System.out.println(t);
		}
	}
	
	public void print(T[][] matrix){
		for(int i=0;i<matrix.length-1;i++){
			for(int j=0;j<matrix.length-1;j++){
				System.out.println(matrix[i][j]+" ");
			}
			System.out.println();
		}
	}


}
