package com.cf.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.omg.CORBA.OMGVMCID;

public class Job implements Comparable<Job>, Serializable {
	

	private static final long serialVersionUID = -505125724603291959L;
	private int id;
	private int num_task;
	private double completetime;
	private double precompletetime;
	private List<Task> tasks;
	private int destID;
	public Job() {
		// TODO Auto-generated constructor stub
	}

	public Job(int id, int num_task, double completetime, List<Task> tasks) {
		super();
		this.id = id;
		this.num_task = num_task;
		this.completetime = completetime;
		this.tasks = tasks;
	}
	
	public Job(int id, int num_task, double completetime, double precompletetime, List<Task> tasks) {
		super();
		this.id = id;
		this.num_task = num_task;
		this.completetime = completetime;
		this.precompletetime = precompletetime;
		this.tasks = tasks;
	}
	
	public int getDestID() {
		return destID;
	}

	public void setDestID(int destID) {
		this.destID = destID;
	}

	public double getPrecompletetime() {
		return precompletetime;
	}

	public void setPrecompletetime(double precompletetime) {
		this.precompletetime = precompletetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum_task() {
		return num_task;
	}

	public void setNum_task(int num_task) {
		this.num_task = num_task;
	}

	public double getCompletetime() {
		return completetime;
	}

	public void setCompletetime(double completetime) {
		this.completetime = completetime;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	@Override
	public String toString() {
		String str = "precompletetime: "+this.precompletetime+ " ";
		for(Task t:this.tasks){
			str+=t+"\n";
		}
		return "numstask:"+this.num_task+"\n"+str+"\n";
	}

	@Override
	public int compareTo(Job o) {
		// TODO Auto-generated method stub
		if(precompletetime>o.precompletetime){
			return 1;
		}else if(precompletetime<o.precompletetime){
			return -1;
		}
		return 0;
	}
	

}
