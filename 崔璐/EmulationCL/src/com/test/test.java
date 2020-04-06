package com.cf.test;

import java.util.Collections;
import java.util.List;
import org.junit.Test;

import com.cf.BIZ.JobScheduler;
import com.cf.BIZ.Request;
import com.cf.DAO.GraphDao;
import com.cf.Utils.CloneUtils;
import com.cf.Utils.DB;
import com.cf.Utils.Dijkstra;
import com.cf.Utils.Print;
import com.cf.Utils.Zipf;
import com.cf.domain.Job;
import com.cf.domain.Link;
import com.cf.domain.Task;

public class test {

	@Test
	public void testrequest() {

		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		// System.out.println(DB.dcList.size());
		int R = DB.dcList.size();
		double A = 2.0;
		double[] pf = new double[R];
		Zipf zipf = new Zipf(R, A, pf);
		zipf.generate();
		Job job = new Job(1, 3, A, null);

		List<Integer> list = zipf.NoRepetitionPick(job.getNum_task());
		Print<Integer> p = new Print<Integer>();
		p.print(list);

	}

	@Test
	public void testGenerateJob() {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(2);
		Print<Job> p = new Print();
		p.print(jobs);
	}

	@Test
	public void testTask() {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Job job = new Job(1, 3, -1, null);
		Request request = new Request();
		request.generateTask(job);
		request.assign(job);
		System.out.println(job.getTasks().get(1).getDatasize());
	}

	@Test
	public void testD() {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
//		int[] res = Dijkstra.dijkstra_alg(matrix, 0, 6);
		// Utils.printMatrix(matrix);
		Request request = new Request();
		List<Job> jobs = request.generateJob(1);
		for(Job j :jobs){
			Collections.sort(j.getTasks());
			int dest = j.getTasks().get(0).getDcId();
			int s1 = j.getTasks().get(1).getDcId();
			int s2 = j.getTasks().get(2).getDcId();
			Dijkstra.dijkstra_alg(matrix, s1, dest);
			Dijkstra.dijkstra_alg(matrix, s2, dest);
		}
	}

	@Test
	public void testLinkDb() {
		GraphDao dao = new GraphDao();
		dao.generateGraph();
		for (Link l : DB.linkList) {
			System.out.println(l);
		}
	}

	@Test
	public void testGenerateTaskRequest() {
		GraphDao dao = new GraphDao();
		dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(15);
		Print<Job> p = new Print();
		p.print(jobs);
	}

	@Test
	public void testCompleteTime() {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(2);
		Print<Job> p = new Print();
		p.print(jobs);
		JobScheduler scheduler = new JobScheduler();
		for (Job j : jobs) {
			System.out.println(scheduler.competeJobPreTime(j));
		}
	}

	@Test
	public void testFindDest() {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(2);
		Print<Job> p = new Print();
		p.print(jobs);
		JobScheduler scheduler = new JobScheduler();
		for (Job j : jobs) {
			System.out.println(scheduler.findMaxTaskinJob(j.getTasks()));
		}
	}

	@Test
	public void testCompareTo() {
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(3);
		JobScheduler scheduler = new JobScheduler();
		for (Job j : jobs) {
			System.out.println("Job time: "+scheduler.competeJobPreTime(j));
		}
		System.out.println();
		Collections.sort(jobs);
		for (Job j : jobs) {
			System.out.println("Job time: "+j.getPrecompletetime());
			Collections.sort(j.getTasks());
			for(Task t: j.getTasks()){
				System.out.print(t.getDatasize()+" "+t.getPreCompleteTime()+" ");
			}
			System.out.println();
		}
	}
	@Test
	public void testClone(){
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(1);
		Job j = jobs.get(0);
				CloneUtils cloneUtils = new CloneUtils();
		Job cloneJ = cloneUtils.clone(j);		
		System.out.println(j== cloneJ);		
		cloneJ.setId(5555);
		System.out.println("j:"+j+" "+j.getId());
		System.out.println("cloneJ:"+cloneJ);	
	}

	@Test
	public void testGraph(){
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		for(int i =0;i<matrix.length;i++){
			for(int j=0;j<matrix.length;j++){
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
	@Test
	public void testGenerateJobArray(){
		GraphDao dao = new GraphDao();
		dao.generateGraph();
		Request request = new Request();
		Job[] jobArray = request.generateJobArray(10);
		for(int i=0;i<10;i++){
			System.out.println(jobArray[i]);
		}
	}
	@Test
	public void testSelectTask(){
		GraphDao dao = new GraphDao();
		int[][] matrix = dao.generateGraph();
		Request request = new Request();
		List<Job> jobs = request.generateJob(3);
		for(Job job: jobs){
			System.out.println(job.getDestID());
			for(Task t: job.getTasks()){
				System.out.println(t.getDatasize()+" "+t.getDcId());
			}
		}
		
		
		
	}
}
