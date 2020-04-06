package com.cf.Utils;

import java.util.Collections;

import com.cf.BIZ.JobScheduler;
import com.cf.BIZ.Request;
import com.cf.DAO.GraphDao;
import com.cf.domain.Job;

public class Simulation {

	


	public static void main(String[] args) {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		DB.jobList = request.generateJob(10); // 生成请求
		JobScheduler scheduler = new JobScheduler();
		for (Job j : DB.jobList) {
			scheduler.competeJobPreTime(j);
		}
		Collections.sort(DB.jobList); //升序排列
		for(Job j:DB.jobList){

		}
	}
	
	
	/**
	 * cpu 0:1000 1: 700 2: 500 3: 200
	 * 
	 * @param datasize
	 * @param cpu
	 * @return
	 */
	public double processtime(int datasize, int cpu) {
		double[][] matrix = new double[][] { { 8.9385, 11.607, 17.334, 44.781 }, { 18.362, 23.376, 32.451, 91.992 },
				{ 27.814, 35.298, 47.446, 139.351 }, { 37.336, 47.159, 63858, 187.055 },
				{ 46.684, 59.027, 78.001, 233.884 }, { 56.031, 71.124, 92.297, 280.712 },
				{ 65.725, 83.472, 106.701, 329.283 }, { 75.449, 95.713, 122.5334, 378.001 },
				{ 85.253, 106.981, 137.923, 427.12 }, { 95.104, 114.576, 154.3574, 476.471 },

		};
		return matrix[datasize - 1][cpu];
	}

	/**
	 * 0:800: 1:400: 2:200 3:100:
	 * 
	 * @param datasize
	 * @param BW
	 * @return
	 */
	public double transporttime(int datasize, int BW) {
		double[][] matrix = new double[][] { { 11.2, 22.9, 43, 87.128 }, { 23.72, 43.321, 88.203, 170.525 },
				{ 34.83, 67.567, 129.395, 258.485 }, { 45.777, 89.921, 171.382, 341.163 },
				{ 57.187, 119.942, 215.371, 441.043 }, { 68.125, 138.912, 258.264, 549.951 },
				{ 80.246, 166.589, 312.722, 614.099 }, { 94.104, 182.122, 352.021, 712.941 }, 
				{ 106.821, 204.458, 392.039,802.928},{119.038,231.841,452.804,890.252} };
		return matrix[datasize - 1][BW];
	}
	public int assignBWLevel(int BW) {
		if (BW > 800) {
			return 0;
		} else if (BW <= 800 && BW > 400) {
			return 1;
		} else if (BW > 200 && BW <= 400) {
			return 2;
		} else if (BW <= 200 && BW > 100) {
			return 3;
		} else {
			return -1;
		}
	}

	public int levelBW(int i) {
		switch (i) {
		case 0:
			return 800;
		case 1:
			return 400;
		case 2:
			return 200;
		case 3:
			return 100;
		}
		return -1;
	}

	/**
	 * 返回 -1 说明 无法进行部署服务
	 * 
	 * @param DC_residualCPU
	 * @return
	 */
	public int assignDC(int DC_residualCPU) {
		if (DC_residualCPU == 1000) {
			return 0;
		} else if (DC_residualCPU < 1000 && DC_residualCPU >= 700) {
			return 1;
		} else if (DC_residualCPU >= 500 && DC_residualCPU < 700) {
			return 2;
		} else if (DC_residualCPU >= 200 && DC_residualCPU < 500) {
			return 3;
		} else {
			return -1;
		}
	}

	public static int levelDC(int i) {
		switch (i) {
		case 0:
			return 1000;
		case 1:
			return 700;
		case 2:
			return 500;
		case 3:
			return 200;
		}
		return -1;
	}
}
