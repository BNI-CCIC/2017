package com.cf.BIZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cf.Utils.DB;
import com.cf.Utils.Zipf;
import com.cf.domain.Job;
import com.cf.domain.Task;

public class Request {
	
	private int NUM ;
	public Request() {
		// TODO Auto-generated constructor stub
	}
	public int getNUM() {
		return NUM;
	}
	public void setNUM(int nUM) {
		NUM = nUM;
	}
	
	public int frand(int min, int max){
		Random random = new Random();
		int fr = random.nextInt(max)%(max-min+1) + min;
		return fr;
	}
	public List<Job> generateJob(int n){
		
		List<Job> jobList = new ArrayList<>();
		for(int i =0; i<n ; i++){
			int num_task = frand(3, 3);
			Job job =new Job(i, num_task, -1, null);
			generateTask(job);
			assign(job);
			jobList.add(job);
		}
		return jobList;
	}
	
	public Job[] generateJobArray(int n){
		Job[] jobArray = new Job[n];
		for(int i =0; i<n ; i++){
			int num_task = frand(3, 3);
			Job job =new Job(i, num_task, -1, null);
			generateTask(job);
			assign(job);
			jobArray[i]=job;
		}
		return jobArray;
	}
	
	public void generateTask(Job job){
		List<Task> tasks = new ArrayList<>();
		for(int i=0;i<job.getNum_task();i++){
			Task tmptask = new Task();
			int datasize = frand(1, 10);
			tmptask.setDatasize(datasize);
			tasks.add(tmptask); 
		}
		job.setTasks(tasks);
	}
	
	public void selectDest(Job job){
		int destid = -1;
		int datasize = 0;
		for(int i=0;i<job.getTasks().size();i++){
			if(job.getTasks().get(i).getDatasize()>datasize){
				datasize = job.getTasks().get(i).getDatasize();
				destid = job.getTasks().get(i).getDcId();
			}
		}
		job.setDestID(destid);
	}
	
	public void assign(Job job){
		int R = DB.dcList.size();
		double A= 2.0;
		double[] pf = new double[R];
		Zipf zipf = new Zipf(R, A, pf);
		zipf.generate();
		List<Integer> list = zipf.NoRepetitionPick(job.getNum_task());
		for(int i=0;i<list.size();i++){
			job.getTasks().get(i).setDcId(list.get(i));
		}
		selectDest(job);
	}
	
	
}
