package com.cf.Utils;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class Utils {

	public static int[][] copyArray(int[][] array) {

		int N = array.length;
		int[][] new_array = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				new_array[i][j] = array[i][j];
			}
		}
		return new_array;
	}

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static void printMatrix(int[] path) {
		for (int i = 0; i < path.length; i++) {
			System.out.print(path[i] + " ");
		}
		System.out.println();
	}

	public static Boolean equalmatrix(Short[] object, Short[] existed) {
		if (object.length == existed.length) {
			for (int i = 0; i < object.length; i++) {
				if (object[i] != existed[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static short value2key(Map<Short, Short> map, Short value) {
		Short key = null;
		for (Entry<Short, Short> iter : map.entrySet()) {
			if (iter.getValue() == value) {
				key = iter.getKey();
				break;
			}
		}
		return key;

	}

	public static void printMatrix(int[][] matrix) {
		// TODO Auto-generated method stub
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * 找到一个连续的子数组进行频隙分配
	 * @param Array
	 * @return
	 */
	public int[] MaxContinuousArray(int[] Array) {
		return null;
	}
	
}
