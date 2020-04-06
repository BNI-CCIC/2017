package com.cf.BIZ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import com.cf.DAO.GraphDao;
import com.cf.DAO.GraphDao2;
import com.cf.Utils.CloneUtils;
import com.cf.Utils.DB;
import com.cf.Utils.Dijkstra;
import com.cf.Utils.Pair;
import com.cf.Utils.Utils;
import com.cf.domain.DC;
import com.cf.domain.Job;
import com.cf.domain.Link;
import com.cf.domain.Path;
import com.cf.domain.Task;

public class JobScheduler {
	private static List<Pair<Job, Boolean>> isComplete = new ArrayList<>();
	private static int[][] matrix;
	private static List<Path> paths = new ArrayList<>();
	private static Map<Task, Path> Task_Path = new HashMap<>();
	private static List<Job> noAssignJobs = new ArrayList<>();
	private static List<Job> assignNoReleasJobs = new ArrayList<>();
	private static Job[] Jobarray;
	private static List<Job> jobList = new ArrayList<Job>();
	private static List<DC> dclist = new ArrayList<DC>();
	private static List<Link> linkList = new ArrayList<Link>();
	private static List<Job> job_noscheduler = new ArrayList<Job>();
	private static List<DC> dc_noscheduler = new ArrayList<DC>();
	private static List<Link> link_noscheduler = new ArrayList<Link>();

	private static List<Job> job_onlybw = new ArrayList<Job>();
	private static List<DC> dc_onlybw = new ArrayList<DC>();
	private static List<Link> link_onlybw = new ArrayList<Link>();

	private static List<Job> job_onlycpu = new ArrayList<Job>();
	private static List<DC> dc_onlycpu = new ArrayList<DC>();
	private static List<Link> link_onlycpu = new ArrayList<Link>();

	public void init() {
		GraphDao dao = new GraphDao(); // 生成图
		// GraphDao2 dao = new GraphDao2();
		matrix = dao.generateGraph();
		Request request = new Request();
		DB.jobList = request.generateJob(9); // 生成请求
		JobScheduler scheduler = new JobScheduler();
		for (Job j : DB.jobList) {
			scheduler.competeJobPreTime(j);
		}
	}

	public void init2() {
		GraphDao dao = new GraphDao(); // 生成图
		matrix = dao.generateGraph();
		Request request = new Request();
		Jobarray = request.generateJobArray(10); // 生成请求
		JobScheduler scheduler = new JobScheduler();
		for (Job j : Jobarray) {

			scheduler.competeJobPreTime(j);
		}
	}

	public void sortByCompleteTime(List<Job> jobList) {
		Collections.sort(jobList);
	}

	public List<Link> getLinkList(int[] tmppath, List<Link> linklist) {
		List<Link> links = new ArrayList<Link>();
		for (int i = 0; i < tmppath.length - 1; i++) {
			Link l1 = getLink(tmppath[i], tmppath[i + 1], linklist);
			links.add(l1);
		}
		return links;
	}

	public void updateIscomplete(Job job, List<Pair<Job, Boolean>> iscompleteJob) {
		for (Pair<Job, Boolean> p : iscompleteJob) {
			if (p.getO1().getId() == job.getId()) {
				p.setO2(true);
			}
		}
	}

	/**
	 * String "ProceTime:" "TransTime:"
	 * 
	 * @param preCompleteTime
	 * @param task
	 * @return
	 */
	public Map<String, Integer> downShift(double preCompleteTime, Task task, int cpu_level, int BW_level) {
		double ProceTime = processtime_update(task.getDatasize(), cpu_level);
		double TransTime = transporttime(task.getDatasize(), BW_level);
		int i = cpu_level, j = BW_level;
		if (ProceTime + TransTime < preCompleteTime) {
			while (i < 4 && j < 3) {
				ProceTime = processtime_update(task.getDatasize(), i);
				TransTime = transporttime(task.getDatasize(), j + 1);
				if (ProceTime + TransTime < preCompleteTime) {
					j++;
				} else {
					break;
				}
			}
			while (i < 3 && j < 4) {
				ProceTime = processtime_update(task.getDatasize(), i + 1);
				TransTime = transporttime(task.getDatasize(), j);
				if (ProceTime + TransTime < preCompleteTime) {
					i++;
				} else {
					break;
				}
			}
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("CPU_LEVEL", i);
		map.put("BW_LEVEL", j);
		return map;
	}

	/**
	 * 调整带宽资源
	 * 
	 * @param preCompleteTime
	 * @param task
	 * @param cpu_level
	 * @param BW_level
	 * @return
	 */
	public Map<String, Integer> downShiftBW(double preCompleteTime, Task task, int cpu_level, int BW_level) {
		double ProceTime = 0.0;
		double TransTime = 0.0;
		int i = cpu_level, j = BW_level;
		while (i < 4 && j < 3) {
			ProceTime = processtime_update(task.getDatasize(), i);
			TransTime = transporttime(task.getDatasize(), j + 1);
			if (ProceTime + TransTime < preCompleteTime) {
				j++;
			} else {
				break;
			}
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("CPU_LEVEL", i);
		map.put("BW_LEVEL", j);
		return map;
	}

	/**
	 * 调整计算资源
	 * 
	 * @param preCompleteTime
	 * @param task
	 * @param cpu_level
	 * @param BW_level
	 * @return
	 */
	public Map<String, Integer> downShiftCPU(double preCompleteTime, Task task, int cpu_level, int BW_level) {
		double ProceTime = processtime_update(task.getDatasize(), cpu_level);
		double TransTime = transporttime(task.getDatasize(), BW_level);
		int i = cpu_level, j = BW_level;
		if (ProceTime + TransTime < preCompleteTime) {
			while (i < 3 && j < 4) {
				ProceTime = processtime_update(task.getDatasize(), i + 1);
				TransTime = transporttime(task.getDatasize(), j);
				if (ProceTime + TransTime < preCompleteTime) {
					i++;
				} else {
					break;
				}
			}
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("CPU_LEVEL", i);
		map.put("BW_LEVEL", j);
		return map;
	}

	public int maxBWOfpath(int[] path, List<Link> linkList) {
		int tmpBW = 10000;
		for (int i = 0; i < path.length - 1; i++) {
			Link tmp = getLink(path[i], path[i + 1], linkList);
			tmpBW = tmpBW > tmp.getResidualBW() ? tmp.getResidualBW() : tmpBW;
			// System.out.println("tmpBW:"+ tmpBW);
		}
		return tmpBW;
	}

	/**
	 * 更新网络 BW
	 * 
	 * @param BW
	 * @param path
	 * @param linkList
	 */
	public void assignBW(int BW, int[] path, List<Link> linkList) {
		for (int i = 2; i < path.length - 3; i++) {
			Link tmp = getLink(path[i], path[i + 1], linkList);
			tmp.setResidualBW(tmp.getResidualBW() - BW);
		}
	}
	

	public Link getLink(int start, int dest, List<Link> linkList) {
		for (Link link : linkList) {
			if (link.getStart() == start && link.getDest() == dest) {
				return link;
			}
		}
		return null;
	}

	public int assignBWLevel(int BW) {
		if (BW > 800) {
			return 0;
		} else if (BW <= 800 && BW > 400) {
			return 1;
		} else if (BW <= 400 && BW > 200) {
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
		if (DC_residualCPU > 1000) {
			return 0;
		} else if (DC_residualCPU > 750 && DC_residualCPU <= 1000) {
			return 1;
		} else if (DC_residualCPU > 500 && DC_residualCPU <= 750) {
			return 2;
		} else if (DC_residualCPU > 200 && DC_residualCPU <= 500) {
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
			return 750;
		case 2:
			return 500;
		case 3:
			return 200;
		}
		return -1;
	}

	/**
	 * 查找指定dc
	 * 
	 * @param dcId
	 * @return
	 */
	public static DC selectDC(int dcId, List<DC> dcList) {
		for (DC dc : dcList) {
			if (dc.getMatrixId() == dcId) {
				return dc;
			}
		}
		return null;
	}

	public List<Task> queryTaskofDC(int dcID, List<Task> NoReleaseTask) {
		List<Task> taskList = new ArrayList<>();
		for (Task t : NoReleaseTask) {
			if (t.getDcId() == dcID) {
				taskList.add(t);
			}
		}
		return taskList;
	}

	public double taskwaittime(List<Task> linkList) {
		double waittime = 0.0;
		for (Task t : linkList) {
			if (t.getCompleteTime() > waittime) {
				waittime = t.getCompleteTime();
			}
		}
		return waittime;
	}

	public boolean hasEnoughResources(int[][] matrix, List<DC> dclist, List<Link> linkList, Job job) {
		// 判断节点是否有足够的资源
		for (Task t : job.getTasks()) {
			if (t.getDcId() != job.getDestID()) {
				DC tmpdc = selectDC(t.getDcId(), dclist);
				if (tmpdc.getResidualCPU() <= 200) {
					return false;
				}
				int maxBW = maxBWOfpath(t.getSelectPath(), linkList);
				if (maxBW <= 100) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 调整计算和存储
	 * 
	 * @param job
	 * @param dcList
	 * @param linkList
	 * @param waittime
	 * @param noRealeasetask
	 */
	public void assignJob(Job job, List<DC> dcList, List<Link> linkList, double waittime, List<Task> noRealeasetask) {
		List<Task> tasks = job.getTasks();
		Collections.sort(tasks);
		double finishtime = 0.0;
		for (Task t : tasks) {
			if (t.getDcId() != job.getDestID()) {
				int MaxBW = maxBWOfpath(t.getSelectPath(), linkList);
				int levelBW = assignBWLevel(MaxBW);
				DC tmpdc = selectDC(t.getDcId(), dcList);
				int residualCPU = tmpdc.getResidualCPU();
				int levelcpu = assignDC(residualCPU);
				if (levelcpu == -1) {
					System.out.println(-1);
				}
				double processtime = processtime_update(t.getDatasize(), levelcpu);
				double transtime = transporttime(t.getDatasize(), levelBW);
				if ((processtime + transtime) >= finishtime) {
					int usedCPU = levelDC(levelcpu);
					int usedBW = levelBW(levelBW);
					finishtime = processtime + transtime;
					tmpdc.setResidualCPU(tmpdc.getResidualCPU() - usedCPU);
					assignBW(usedBW, t.getSelectPath(), linkList);
					t.setProcesstime(processtime);
					t.setTransporttime(transtime);
					t.setDequetime(waittime);
					t.setCpuLevel(levelcpu);
					t.setBWLevel(levelBW);
					noRealeasetask.add(t);
				} else {
					Map<String, Integer> map = downShift(finishtime, t, levelcpu, levelBW);
					levelcpu = map.get("CPU_LEVEL");
					levelBW = map.get("BW_LEVEL");
					processtime = processtime_update(t.getDatasize(), levelcpu);
					transtime = transporttime(t.getDatasize(), levelBW);
					int usedCPU = levelDC(levelcpu);
					int usedBW = levelBW(levelBW);
					tmpdc.setResidualCPU(tmpdc.getResidualCPU() - usedCPU);
					assignBW(usedBW, t.getSelectPath(), linkList);
					t.setProcesstime(processtime);
					t.setTransporttime(transtime);
					t.setDequetime(waittime);
					t.setCpuLevel(levelcpu);
					t.setBWLevel(levelBW);
					noRealeasetask.add(t);
				}
			}
		}
	}

	/*
	 * 更新
	 */
	private static int num = 0;

	public void assignJob_update(Job job, List<DC> dcList, List<Link> linkList, double waittime,
			List<Task> noRealeasetask) {
		List<Task> tasks = job.getTasks();
		Collections.sort(tasks);
		aggignPreCompletetime(job, dcList, linkList);
		double finishtime = job.getPrecompletetime();
		for (Task t : tasks) {
			if (t.getDcId() != job.getDestID()) {
				int MaxBW = maxBWOfpath(t.getSelectPath(), linkList);
				int levelBW = assignBWLevel(MaxBW);
				DC tmpdc = selectDC(t.getDcId(), dcList);
				int residualCPU = tmpdc.getResidualCPU();
				int levelcpu = assignDC(residualCPU);
				if (levelcpu == -1) {
					System.out.println(-1);
				}
				double processtime = processtime_update(t.getDatasize(), levelcpu);
				double transtime = transporttime(t.getDatasize(), levelBW);

				if (t.getPreCompleteTime() <= job.getPrecompletetime()) {
					Map<String, Integer> map = downShift(finishtime, t, levelcpu, levelBW);
					levelcpu = map.get("CPU_LEVEL");
					levelBW = map.get("BW_LEVEL");
					processtime = processtime_update(t.getDatasize(), levelcpu);
					transtime = transporttime(t.getDatasize(), levelBW);
					int usedCPU = levelDC(levelcpu);
					int usedBW = levelBW(levelBW);
					tmpdc.setResidualCPU(tmpdc.getResidualCPU() - usedCPU);
					assignBW(usedBW, t.getSelectPath(), linkList);
					t.setProcesstime(processtime);
					t.setTransporttime(transtime);
					t.setDequetime(waittime);
					t.setCpuLevel(levelcpu);
					t.setBWLevel(levelBW);
					noRealeasetask.add(t);
					num++;
				}
			}
		}
	}

	public Map<Task, Double> calculateTaskTime(Job job, List<DC> dcList, List<Link> linkList) {

		Map<Task, Double> taskTime = new HashMap<>();
		List<Task> tasks = job.getTasks();
		for (Task t : tasks) {
			if (t.getDcId() != job.getDestID()) {
				int MaxBW = maxBWOfpath(t.getSelectPath(), linkList);
				int levelBW = assignBWLevel(MaxBW);
				DC tmpdc = selectDC(t.getDcId(), dcList);
				int residualCPU = tmpdc.getResidualCPU();
				int levelcpu = assignDC(residualCPU);
				if (levelcpu == -1) {
					System.out.println(-1);
				}
				double processtime = processtime_update(t.getDatasize(), levelcpu);
				double transtime = transporttime(t.getDatasize(), levelBW);
				double finishtime = processtime + transtime;
				taskTime.put(t, finishtime);
				t.setPreCompleteTime(finishtime);
			}
		}
		return taskTime;
	}

	public void aggignPreCompletetime(Job job, List<DC> dcList, List<Link> linkList) {
		Map<Task, Double> taskTime = calculateTaskTime(job, dcList, linkList);
		double pretime = 0.0;
		for (Entry<Task, Double> m : taskTime.entrySet()) {
			if (m.getValue() > pretime) {
				pretime = m.getValue();
			}
		}
		job.setPrecompletetime(pretime);
	}

	/**
	 * 对比算法 分配最大的计算和存储资源
	 * 
	 * @param job
	 * @param dcList
	 * @param linkList
	 * @param waittime
	 * @param noreleaseTask
	 */
	public void assignJob1(Job job, List<DC> dcList, List<Link> linkList, double waittime, List<Task> noreleaseTask) {
		List<Task> tasks = job.getTasks();
		Collections.sort(tasks);
		for (Task t : tasks) {
			if (t.getDcId() != job.getDestID()) {
				int MaxBW = maxBWOfpath(t.getSelectPath(), linkList);
				int levelBW = assignBWLevel(MaxBW);
				DC tmpdc = selectDC(t.getDcId(), dcList);
				int residualCPU = tmpdc.getResidualCPU();
				int levelcpu = assignDC(residualCPU);
				double processtime = processtime_update(t.getDatasize(), levelcpu);
				double transtime = transporttime(t.getDatasize(), levelBW);
				int usedCPU = levelDC(levelcpu);
				int usedBW = levelBW(levelBW);
				tmpdc.setResidualCPU(tmpdc.getResidualCPU() - usedCPU);
				assignBW(usedBW, t.getSelectPath(), linkList);
				t.setProcesstime(processtime);
				t.setTransporttime(transtime);
				t.setDequetime(waittime);
				t.setCpuLevel(levelcpu);
				t.setBWLevel(levelBW);
				noreleaseTask.add(t);

			}
		}
	}

	/**
	 * 只调整带宽资源
	 * 
	 * @param job
	 * @param dcList
	 * @param linkList
	 * @param waittime
	 * @param noRealeasetask
	 */
	public void assignJobBW(Job job, List<DC> dcList, List<Link> linkList, double waittime, List<Task> noRealeasetask) {
		List<Task> tasks = job.getTasks();
		Collections.sort(tasks);
		aggignPreCompletetime(job, dcList, linkList);
		double finishtime = job.getPrecompletetime();
		for (Task t : tasks) {
			if (t.getDcId() != job.getDestID()) {
				int MaxBW = maxBWOfpath(t.getSelectPath(), linkList);
				int levelBW = assignBWLevel(MaxBW);
				DC tmpdc = selectDC(t.getDcId(), dcList);
				int residualCPU = tmpdc.getResidualCPU();
				int levelcpu = assignDC(residualCPU);
				if (levelcpu == -1) {
					System.out.println(-1);
				}
				double processtime = processtime_update(t.getDatasize(), levelcpu);
				double transtime = transporttime(t.getDatasize(), levelBW);
				if (t.getPreCompleteTime() <= finishtime) {
					Map<String, Integer> map = downShiftBW(finishtime, t, levelcpu, levelBW);
					levelcpu = map.get("CPU_LEVEL");
					levelBW = map.get("BW_LEVEL");
					processtime = processtime_update(t.getDatasize(), levelcpu);
					transtime = transporttime(t.getDatasize(), levelBW);
					int usedCPU = levelDC(levelcpu);
					int usedBW = levelBW(levelBW);
					tmpdc.setResidualCPU(tmpdc.getResidualCPU() - usedCPU);
					assignBW(usedBW, t.getSelectPath(), linkList);
					t.setProcesstime(processtime);
					t.setTransporttime(transtime);
					t.setDequetime(waittime);
					t.setCpuLevel(levelcpu);
					t.setBWLevel(levelBW);
					noRealeasetask.add(t);
				}
			}
		}
	}

	/**
	 * 只调整计算资源
	 * 
	 * @param job
	 * @param dcList
	 * @param linkList
	 * @param waittime
	 * @param noRealeasetask
	 */
	public void assignJobCPU(Job job, List<DC> dcList, List<Link> linkList, double waittime,
			List<Task> noRealeasetask) {
		List<Task> tasks = job.getTasks();
		Collections.sort(tasks);
		aggignPreCompletetime(job, dcList, linkList);
		double finishtime = job.getPrecompletetime();
		for (Task t : tasks) {
			if (t.getDcId() != job.getDestID()) {
				int MaxBW = maxBWOfpath(t.getSelectPath(), linkList);
				int levelBW = assignBWLevel(MaxBW);
				DC tmpdc = selectDC(t.getDcId(), dcList);
				int residualCPU = tmpdc.getResidualCPU();
				int levelcpu = assignDC(residualCPU);
				if (levelcpu == -1) {
					System.out.println(-1);
				}
				double processtime = processtime_update(t.getDatasize(), levelcpu);
				double transtime = transporttime(t.getDatasize(), levelBW);
				if (t.getPreCompleteTime() <= finishtime) {
					Map<String, Integer> map = downShiftCPU(finishtime, t, levelcpu, levelBW);
					levelcpu = map.get("CPU_LEVEL");
					levelBW = map.get("BW_LEVEL");
					processtime = processtime_update(t.getDatasize(), levelcpu);
					transtime = transporttime(t.getDatasize(), levelBW);
					int usedCPU = levelDC(levelcpu);
					int usedBW = levelBW(levelBW);
					tmpdc.setResidualCPU(tmpdc.getResidualCPU() - usedCPU);
					assignBW(usedBW, t.getSelectPath(), linkList);
					t.setProcesstime(processtime);
					t.setTransporttime(transtime);
					t.setDequetime(waittime);
					t.setCpuLevel(levelcpu);
					t.setBWLevel(levelBW);
					noRealeasetask.add(t);
				}
			}
		}
	}

	public void selectShortestPath(List<Job> jobList, int[][] matrix) {
		for (Job job : jobList) {
			int[][] jobmatrix = Utils.copyArray(matrix);
			for (Task t : job.getTasks()) {
				int[][] taskmatrix = Utils.copyArray(jobmatrix);
				if (t.getDcId() != job.getDestID()) {
					int[] tmppath = Dijkstra.dijkstra_alg(taskmatrix, t.getDcId(), job.getDestID());
					t.setSelectPath(tmppath);
					updateGraph(jobmatrix, tmppath);
				}
			}
		}
	}

	public Task getminTask(List<Task> listTask) {
		double finishtime = Double.MAX_VALUE;
		Task task = new Task();
		for (Task t : listTask) {
			double transtime = t.getTransporttime();
			double processtime = t.getProcesstime();
			double waittime = t.getDequetime();
			double t_finish = transtime + processtime + waittime;
			if (t_finish < finishtime) {
				finishtime = t_finish;
				task = t;
			}
		}
		return task;
	}

	public static void main(String[] args) {
		int count = 0;
		// double sumTime = 0.0;
		// double sumtime1 = 0.0;
		double averageTime = 0.0;
		double averageTime1 = 0.0;

		double averageUseCPU = 0.0;
		double averageUsedBW = 0.0;
		double averageUseCPU1 = 0.0;
		double averageUsedBW1 = 0.0;

		double averageTime_onlybw = 0.0;
		double averageUsedBW_onlybw = 0.0;
		double averageUseCPU_onlybw = 0.0;

		double averageTime_onlycpu = 0.0;
		double averageUseCPU_onlycpu = 0.0;
		double averageUsedBW_onlycpu = 0.0;

		double maxtime = 0.0;
		double maxtime1 = 0.0;
		double maxtime_onlybw = 0.0;
		double maxtime_onlycpu = 0.0;

		double diff = 0.0;
		double diff1 = 0.0;
		double diff_onlyBW = 0.0;
		double diff_onlyCPU = 0.0;

		while (count < 10000) {

			DB.dcList = new ArrayList<>();
			DB.jobList = new ArrayList<>();
			DB.linkList = new ArrayList<>();

			JobScheduler scheduler = new JobScheduler();
			scheduler.init();
			scheduler.selectShortestPath(DB.jobList, matrix);

			jobList = CloneUtils.deepCopy(DB.jobList);
			dclist = CloneUtils.deepCopy(DB.dcList);
			linkList = CloneUtils.deepCopy(DB.linkList);

			job_noscheduler = CloneUtils.deepCopy(DB.jobList);
			dc_noscheduler = CloneUtils.deepCopy(DB.dcList);
			link_noscheduler = CloneUtils.deepCopy(DB.linkList);

			job_onlybw = CloneUtils.deepCopy(DB.jobList);
			dc_onlybw = CloneUtils.deepCopy(DB.dcList);
			link_onlybw = CloneUtils.deepCopy(DB.linkList);

			job_onlycpu = CloneUtils.deepCopy(DB.jobList);
			dc_onlycpu = CloneUtils.deepCopy(DB.dcList);
			link_onlycpu = CloneUtils.deepCopy(DB.linkList);

			int[][] copyMatrix = Utils.copyArray(matrix);
			int[][] matrix_onlybw = Utils.copyArray(matrix);
			int[][] mattix_onlycpu = Utils.copyArray(matrix);

			List<Task> NoReleaseTasks = new ArrayList<>();
			List<Task> NoReleaseTasks1 = new ArrayList<Task>();
			List<Task> NoReleaseTasks_bw = new ArrayList<Task>();
			List<Task> NoReleaseTasks_cpu = new ArrayList<Task>();
			{
				/*
				 * 调整计算和带宽资源
				 */
				Collections.sort(DB.jobList);
				double waittime = 0;
				for (int i = 0; i < DB.jobList.size(); i++) {
					// System.out.println("job :" + i);
					Job tmpJob = DB.jobList.get(i);
					boolean isassign = false;
					while (!isassign) {
						if (scheduler.hasEnoughResources(matrix, DB.dcList, DB.linkList, tmpJob)) {
							scheduler.assignJob_update(tmpJob, DB.dcList, DB.linkList, waittime, NoReleaseTasks);
							isassign = true;
						} else {
							Task mintask = scheduler.getminTask(NoReleaseTasks);
							// System.out.println("task"+mintask);
							NoReleaseTasks.remove(mintask);
							waittime = mintask.getDequetime() + mintask.getProcesstime() + mintask.getTransporttime();
							// 释放资源
							scheduler.releaseResource(mintask, DB.dcList, DB.linkList);
						}
					}

				}
				scheduler.calcuateAlltime(DB.jobList);
			}
			{
				/*
				 * 对比算法
				 */
				Collections.sort(job_noscheduler);
				double waittime = 0;
				for (int i = 0; i < job_noscheduler.size(); i++) {
					// System.out.println("job :" + i);
					Job tmpJob = job_noscheduler.get(i);
					boolean isassign = false;
					while (!isassign) {
						if (scheduler.hasEnoughResources(copyMatrix, dc_noscheduler, link_noscheduler, tmpJob)) {
							scheduler.assignJob1(tmpJob, dc_noscheduler, link_noscheduler, waittime, NoReleaseTasks1);
							isassign = true;
						} else {
							Task mintask = scheduler.getminTask(NoReleaseTasks1);
							NoReleaseTasks1.remove(mintask);
							waittime = mintask.getDequetime() + mintask.getProcesstime() + mintask.getTransporttime();
							// 释放资源
							scheduler.releaseResource(mintask, dc_noscheduler, link_noscheduler);
						}
						//
					}

				}
				scheduler.calcuateAlltime(job_noscheduler);

			}

			{
				/*
				 * 调整带宽资源
				 */
				Collections.sort(job_onlybw);
				double waittime = 0;
				for (int i = 0; i < job_onlybw.size(); i++) {
					// System.out.println("job :" + i);
					Job tmpJob = job_onlybw.get(i);
					boolean isassign = false;
					while (!isassign) {
						if (scheduler.hasEnoughResources(matrix_onlybw, dc_onlybw, link_onlybw, tmpJob)) {
							scheduler.assignJobBW(tmpJob, dc_onlybw, link_onlybw, waittime, NoReleaseTasks_bw);
							isassign = true;
						} else {
							Task mintask = scheduler.getminTask(NoReleaseTasks_bw);
							NoReleaseTasks_bw.remove(mintask);
							waittime = mintask.getDequetime() + mintask.getProcesstime() + mintask.getTransporttime();
							// 释放资源
							scheduler.releaseResource(mintask, dc_onlybw, link_onlybw);
						}
						//
					}

				}
				scheduler.calcuateAlltime(job_onlybw);

			}

			{
				// 调整计算资源
				Collections.sort(job_onlycpu);
				double waittime = 0;
				for (int i = 0; i < job_onlycpu.size(); i++) {
					// System.out.println("job :" + i);
					Job tmpJob = job_onlycpu.get(i);
					boolean isassign = false;
					while (!isassign) {
						if (scheduler.hasEnoughResources(mattix_onlycpu, dc_onlycpu, link_onlycpu, tmpJob)) {
							scheduler.assignJobCPU(tmpJob, dc_onlycpu, link_onlycpu, waittime, NoReleaseTasks_cpu);
							isassign = true;
						} else {
							Task mintask = scheduler.getminTask(NoReleaseTasks_cpu);
							NoReleaseTasks_cpu.remove(mintask);
							waittime = mintask.getDequetime() + mintask.getProcesstime() + mintask.getTransporttime();
							// 释放资源
							scheduler.releaseResource(mintask, dc_onlycpu, link_onlycpu);
						}
						//
					}

				}
				scheduler.calcuateAlltime(job_onlycpu);

			}

			double averageJobtime = scheduler.averageJobTime(DB.jobList);
			double averageJobtime1 = scheduler.averageJobTime(job_noscheduler);
			double average_jobtime_bw = scheduler.averageJobTime(job_onlybw);
			double average_jobtime_cpu = scheduler.averageJobTime(job_onlycpu);
			averageTime += averageJobtime;
			averageTime1 += averageJobtime1;
			averageTime_onlybw += average_jobtime_bw;
			averageTime_onlycpu += average_jobtime_cpu;

			double usedCpu = scheduler.averageusedCpu(DB.jobList);
			double usedcpu1 = scheduler.averageusedCpu(job_noscheduler);
			double usedcpu_bw = scheduler.averageusedCpu(job_onlybw);
			double usedcpu_cpu = scheduler.averageusedCpu(job_onlycpu);
			averageUseCPU += usedCpu;
			averageUseCPU1 += usedcpu1;
			averageUseCPU_onlybw += usedcpu_bw;
			averageUseCPU_onlycpu += usedcpu_cpu;

			double usedBw = scheduler.averageusedBW(DB.jobList);
			double usedBW1 = scheduler.averageusedBW(job_noscheduler);
			double usedBw_bw = scheduler.averageusedBW(job_onlybw);
			double usedBw_cpu = scheduler.averageusedBW(job_onlycpu);
			averageUsedBW += usedBw;
			averageUsedBW1 += usedBW1;
			averageUsedBW_onlybw += usedBw_bw;
			averageUsedBW_onlycpu += usedBw_cpu;

			maxtime += scheduler.calcuteMaxtime(DB.jobList);
			maxtime1 += scheduler.calcuteMaxtime(job_noscheduler);
			maxtime_onlybw += scheduler.calcuteMaxtime(job_onlybw);
			maxtime_onlycpu += scheduler.calcuteMaxtime(job_onlycpu);
			count++;

			double tmpdiff = scheduler.CalculateTimeDiff(DB.jobList);
			double tmpdiff1 = scheduler.CalculateTimeDiff(job_noscheduler);
			double tmpdiff_onlyBW = scheduler.CalculateTimeDiff(job_onlybw);
			double tmpdiff_onlyCPU = scheduler.CalculateTimeDiff(job_onlycpu);
			diff += tmpdiff;
			diff1 += tmpdiff1;
			diff_onlyBW += tmpdiff_onlyBW;
			diff_onlyCPU += tmpdiff_onlyCPU;

		}

		System.out.println("averageTime: " + averageTime / 10000);
		System.out.println("averageTime1: " + averageTime1 / 10000);
		// System.out.println("averagedCPU: " + averageUseCPU / 10000);
		// System.out.println("averagedCPU1: " + averageUseCPU1 / 10000);
		// System.out.println("averagedBW: " + averageUsedBW / 10000);
		// System.out.println("averagedBW1: " + averageUsedBW1 / 10000);
		// System.out.println("num :" + num);
		System.out.println("averageTime_onlybw: " + averageTime_onlybw / 10000);
		System.out.println("averageTime_onlycpu: " + averageTime_onlycpu / 10000);

		// maxtime
		// System.out.println("maxtime: " + maxtime / 10000);
		// System.out.println("maxtime1: " + maxtime1 / 10000);
		// System.out.println("maxtime_onlybw: " + maxtime_onlybw / 10000);
		// System.out.println("maxtime_onlycpu: " + maxtime_onlycpu / 10000);
		// System.out.println("averagedCPU_bw: " + averageUseCPU_onlybw /
		// 10000);
		// System.out.println("averagedCPU_cpu: " + averageUseCPU_onlycpu /
		// 10000);
		// System.out.println("averagedBW_onlybw: " + averageTime_onlybw /
		// 10000);
		// System.out.println("averagedBW_onlycpu: " + averageUsedBW_onlycpu /
		// 10000);

		// time diff
		System.out.println("联合 时间差： " + diff / 10000);
		System.out.println("对比 时间差： " + diff1 / 10000);
		System.out.println("BW 时间差： " + diff_onlyBW / 10000);
		System.out.println("CPU 时间差： " + diff_onlyCPU / 10000);

	}

	public double CalculateTimeDiff(List<Job> jobList) {

		double alltimediff = 0.0;

		for (Job job : jobList) {
			List<Task> tasks = job.getTasks();
			List<Double> time = new ArrayList<>();
			for (Task t : tasks) {
				if (t.getDcId() != job.getDestID()) {
					time.add(t.getCompleteTime());
				}
			}
			Collections.sort(time);
			for (int i = 1; i < time.size(); i++) {
				alltimediff += time.get(i) - time.get(0);
			}
		}

		return alltimediff / jobList.size();
	}

	public double averageusedCpu(List<Job> jobList) {
		double usedcpu = 0.0;
		for (Job j : jobList) {
			List<Task> tasks = j.getTasks();
			for (Task t : tasks) {
				if (t.getDcId() != j.getDestID()) {
					usedcpu += levelDC(t.getCpuLevel());
				}
			}
		}
		return usedcpu / jobList.size();
	}

	public double averageusedBW(List<Job> jobList) {
		double usedBW = 0.0;
		for (Job j : jobList) {
			List<Task> tasks = j.getTasks();
			for (Task t : tasks) {
				if (t.getDcId() != j.getDestID()) {
					usedBW += levelBW(t.getBWLevel());
				}
			}
		}
		return usedBW / jobList.size();
	}

	public double averageJobTime(List<Job> jobList) {
		double alltime = 0.0;
		for (Job j : jobList) {
			alltime += j.getCompletetime();
		}
		return alltime / jobList.size();
	}

	public void calcuateAlltime(List<Job> jobList) {
		for (int i = 0; i < jobList.size(); i++) {
			double time = 0.0;
			List<Task> tasks = jobList.get(i).getTasks();
			for (int j = 0; j < tasks.size(); j++) {
				if (tasks.get(j).getDcId() != jobList.get(i).getDestID()) {
					double tmptime = tasks.get(j).getDequetime() + tasks.get(j).getTransporttime()
							+ tasks.get(j).getProcesstime();
					tasks.get(j).setCompleteTime(tmptime);
					if (time < tmptime) {
						time = tmptime;
					}
				}
			}
			jobList.get(i).setCompletetime(time);
		}
	}

	public double calcuteMaxtime(List<Job> jobList) {
		double maxtime = 0.0;
		for (Job j : jobList) {
			double time = j.getCompletetime();
			maxtime = maxtime >= time ? maxtime : time;
		}
		return maxtime;
	}

	/**
	 * release resource update network
	 * 
	 * @param task
	 */
	public void releaseResource(Task task, List<DC> dcList, List<Link> linkList) {
		int dcId = task.getDcId();
		DC tmpdc = selectDC(dcId, dcList);
		int[] path = task.getSelectPath();
		int usedBW = levelBW(task.getBWLevel());
		releaseBW(usedBW, path, linkList);
		int usedCPU = levelDC(task.getCpuLevel());
		tmpdc.setResidualCPU(tmpdc.getResidualCPU() + usedCPU);
	}

	public void releaseBW(int BW, int[] path, List<Link> linkList) {
		for (int i = 2; i < path.length - 3; i++) {
			Link tmp = getLink(path[i], path[i + 1], linkList);
			tmp.setResidualBW(tmp.getResidualBW() + BW);

		}
	}

	public static void releaseResource(Task task, int[][] matrix, List<Link> linkList) {

		Path path = Task_Path.get(task);
		int[] tmppath = path.getPath();
		for (int i = 2; i < tmppath.length - 3; i++) {
			matrix[tmppath[i]][tmppath[i + 1]] = 20;
			// System.out.println(matrix[path[i]][path[i+1]]);
		}
		// release Link BW
		for (Link l : path.getLink()) {
			Link objLink = searchLink(l, linkList);
			objLink.setResidualBW(objLink.getResidualBW() + path.getBW());
		}
	}

	public static Link searchLink(Link link, List<Link> linkList) {
		for (Link l : linkList) {
			if (link.getStart() == l.getStart() && link.getDest() == l.getDest()) {
				return l;
			}
		}
		return null;
	}

	public void updateGraph(int[][] matrix, int[] path) {
		for (int i = 2; i < path.length - 3; i++) {
			matrix[path[i]][path[i + 1]] = 10000;
		}
	}

	public static Task findNextReleaseTask(List<Job> noReleasJobs) {
		Task resTask = null;
		double time = Double.MAX_VALUE;
		for (Job j : noReleasJobs) {
			for (Task t : j.getTasks()) {
				if (t.getProcesstime() == 0) {
					continue;
				}
				double tmptime = t.getDequetime() + t.getProcesstime() + t.getTransporttime();
				if (time > tmptime) {
					time = tmptime;
					resTask = t;
				}
			}
		}
		return resTask;
	}

	public static void printCompleteJob() {
		for (Pair<Job, Boolean> p : isComplete) {
			if (p.getO2()) {
				Job j = p.getO1();
				System.out.println("job ID: " + j.getId() + " job completetime:" + j.getCompletetime()
						+ "job preCompleteTime:" + j.getPrecompletetime());
				for (Task t : j.getTasks()) {
					System.out.println("task.datasize:" + t.getDatasize() + " ;task:processtime " + t.getProcesstime()
							+ " tast:transportTime" + t.getTransporttime() + " task: dequeTime " + t.getDequetime()
							+ " task Alltime: " + (t.getProcesstime() + t.getTransporttime()) + "task.cpu_BW"
							+ t.getCpuLevel() + " " + t.getBWLevel());
				}
			}
		}
	}

	public static void printNoCompleteJob() {
		for (Pair<Job, Boolean> p : isComplete) {
			if (!p.getO2()) {
				Job j = p.getO1();
				System.out.println("job ID: " + j.getId() + " job completetime:" + j.getCompletetime()
						+ "job preCompleteTime:" + j.getPrecompletetime());
				for (Task t : j.getTasks()) {
					System.out.println("task.datasize:" + t.getDatasize() + " ;task:processtime " + t.getProcesstime()
							+ " tast:transportTime" + t.getTransporttime() + " task Alltime: "
							+ (t.getProcesstime() + t.getTransporttime()) + "task preCompleteTime"
							+ t.getPreCompleteTime());
				}
			}
		}
	}

	/**
	 * 网络中只有当前一个业务 计算最快完成时间
	 * 
	 * @param job
	 */
	public double competeJobPreTime(Job job) {
		int dest = findMaxTaskinJob(job.getTasks()).getDcId();
		double[] completeTask = new double[job.getTasks().size() - 1];
		int i = 0;
		for (Task task : job.getTasks()) {
			if (task.getDcId() != dest) {
				double tmpTrans = transporttime(task.getDatasize(), 0);
				double tmpProcess = processtime_update(task.getDatasize(), 0);
				double res = tmpTrans + tmpProcess;
				task.setPreCompleteTime(res);
				completeTask[i++] = res;
			}
		}
		double tmptime = 0.0;
		for (int j = 0; j < completeTask.length; j++) {
			if (completeTask[j] > tmptime) {
				tmptime = completeTask[j];
			}
		}
		job.setPrecompletetime(tmptime);
		return tmptime;
	}

	/**
	 * 返回最大的业务所在的目的点作为聚合点
	 * 
	 * @param tasks
	 * @return
	 */
	public Task findMaxTaskinJob(List<Task> tasks) {
		int datasize = 0;
		Task res = new Task();
		for (Task task : tasks) {
			if (task.getDatasize() > datasize) {
				datasize = task.getDatasize();
				res = task;
			}
		}
		return res;
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
				{ 27.814, 35.298, 47.446, 139.351 }, { 37.336, 47.159, 63.858, 187.055 },
				{ 46.684, 59.027, 78.001, 233.884 }, { 56.031, 71.124, 92.297, 280.712 },
				{ 65.725, 83.472, 106.701, 329.283 }, { 75.449, 95.713, 122.5334, 378.001 },
				{ 85.253, 106.981, 137.923, 427.12 }, { 95.104, 114.576, 154.3574, 476.471 },

		};
		return matrix[datasize - 1][cpu];
	}

	/**
	 * cpu 0:1000 1:500 2: 200 4:100
	 * 
	 * @param datasize
	 * @param cpu
	 * @return
	 */
	public double processtime_update(int datasize, int cpu) {
		// double[][] matrix = new double[][] { { 8.9385, 17.334, 44.781, 88.501
		// }, { 18.362, 32.451, 91.992, 1.144 },
		// { 27.814, 47.446, 139.351, 257.841 }, { 37.336, 63.858, 187.055,
		// 343.533 },
		// { 46.684, 78.001, 233.884, 442.057 }, { 56.031, 92.297, 280.712,
		// 545.774 },
		// { 65.725, 106.701, 329.283, 611.344 }, { 75.449, 122.5334, 378.001,
		// 713.201 },
		// { 85.253, 137.923, 427.12, 802.985 }, { 95.104, 154.3574, 476.471,
		// 891.294 },
		//
		// };
		double[][] matrix = new double[][] { { 8.9385, 11.607, 17.334, 44.781 }, { 18.362, 23.376, 32.451, 91.992 },
				{ 27.814, 35.298, 47.446, 139.351 }, { 37.336, 47.159, 63.858, 187.055 },
				{ 46.684, 59.027, 78.001, 233.884 }, { 56.031, 71.124, 92.297, 280.712 },
				{ 65.725, 83.472, 106.701, 329.283 }, { 75.449, 95.713, 122.5334, 378.001 },
				{ 85.253, 106.981, 137.923, 427.12 }, { 95.104, 114.576, 154.3574, 476.471 },

		};
		// double[][] matrix = new double[][] { { 11.2, 22, 45, 88 }, { 23.72,
		// 43.321, 88.203, 170.525 },
		// { 34.83, 67.567, 129.395, 258.485 }, { 45.777, 89.921, 171.382,
		// 341.163 },
		// { 57.187, 119.942, 215.371, 441.043 }, { 68.125, 138.912, 258.264,
		// 549.951 },
		// { 80.246, 166.589, 312.722, 614.099 }, { 94.104, 182.122, 352.021,
		// 712.941 },
		// { 106.821, 204.458, 392.039, 802.928 }, { 119.038, 231.841, 452.804,
		// 890.252 } };
		return matrix[datasize - 1][cpu];
	}

	/**
	 * 0:800: 1:400: 2:200 3:100:
	 * 
	 * @param datasize
	 * @param BW
	 * @return
	 */
	public static double transporttime(int datasize, int BW) {
		double[][] matrix = new double[][] { { 11.2, 22, 45, 88 }, { 23.72, 43.321, 88.203, 170.525 },
				{ 34.83, 67.567, 129.395, 258.485 }, { 45.777, 89.921, 171.382, 341.163 },
				{ 57.187, 119.942, 215.371, 441.043 }, { 68.125, 138.912, 258.264, 549.951 },
				{ 80.246, 166.589, 312.722, 614.099 }, { 94.104, 182.122, 352.021, 712.941 },
				{ 106.821, 204.458, 392.039, 802.928 }, { 119.038, 231.841, 452.804, 890.252 } };
		// double[][] matrix = new double[][] { { 8.9385, 17.334, 44.781, 88.501
		// }, { 18.362, 32.451, 91.992, 1.144 },
		// { 27.814, 47.446, 139.351, 257.841 }, { 37.336, 63.858, 187.055,
		// 343.533 },
		// { 46.684, 78.001, 233.884, 442.057 }, { 56.031, 92.297, 280.712,
		// 545.774 },
		// { 65.725, 106.701, 329.283, 611.344 }, { 75.449, 122.5334, 378.001,
		// 713.201 },
		// { 85.253, 137.923, 427.12, 802.985 }, { 95.104, 154.3574, 476.471,
		// 891.294 },
		//
		// };
		// double[][] matrix = new double[][] { { 8.9385, 11.607, 17.334, 44.781
		// }, { 18.362, 23.376, 32.451, 91.992 },
		// { 27.814, 35.298, 47.446, 139.351 }, { 37.336, 47.159, 63.858,
		// 187.055 },
		// { 46.684, 59.027, 78.001, 233.884 }, { 56.031, 71.124, 92.297,
		// 280.712 },
		// { 65.725, 83.472, 106.701, 329.283 }, { 75.449, 95.713, 122.5334,
		// 378.001 },
		// { 85.253, 106.981, 137.923, 427.12 }, { 95.104, 114.576, 154.3574,
		// 476.471 },

		// };
		return matrix[datasize - 1][BW];
	}
}
