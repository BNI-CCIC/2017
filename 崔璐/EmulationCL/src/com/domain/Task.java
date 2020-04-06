package com.cf.domain;

import java.io.Serializable;

public class Task implements Comparable<Task>, Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 104580009464316275L;
	private int datasize;
	private int dcId;
	private double processtime;
	private double transporttime;
	private double dequetime;
	private double preCompleteTime;
	private int cpuLevel;
	private int BWLevel;
	private double completeTime;
	private int[] selectPath;
	
	
	public int[] getSelectPath() {
		return selectPath;
	}
	public void setSelectPath(int[] selectPath) {
		this.selectPath = selectPath;
	}
	public double getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(double completeTime) {
		this.completeTime = completeTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getCpuLevel() {
		return cpuLevel;
	}
	public void setCpuLevel(int cpuLevel) {
		this.cpuLevel = cpuLevel;
	}
	public int getBWLevel() {
		return BWLevel;
	}
	public void setBWLevel(int bWLevel) {
		BWLevel = bWLevel;
	}
	public Task(int datasize, int dcId, double processtime, double transporttime, double dequetime,
			double preCompleteTime) {
		super();
		this.datasize = datasize;
		this.dcId = dcId;
		this.processtime = processtime;
		this.transporttime = transporttime;
		this.dequetime = dequetime;
		this.preCompleteTime = preCompleteTime;
	}
	public double getPreCompleteTime() {
		return preCompleteTime;
	}
	public void setPreCompleteTime(double preCompleteTime) {
		this.preCompleteTime = preCompleteTime;
	}
	public Task() {
		// TODO Auto-generated constructor stub
	}
	public int getDatasize() {
		return datasize;
	}
	public void setDatasize(int datasize) {
		this.datasize = datasize;
	}
	public int getDcId() {
		return dcId;
	}
	public void setDcId(int dcId) {
		this.dcId = dcId;
	}
	public double getProcesstime() {
		return processtime;
	}
	public void setProcesstime(double processtime) {
		this.processtime = processtime;
	}
	public double getTransporttime() {
		return transporttime;
	}
	public void setTransporttime(double transporttime) {
		this.transporttime = transporttime;
	}
	public double getDequetime() {
		return dequetime;
	}
	public void setDequetime(double dequetime) {
		this.dequetime = dequetime;
	} 
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "<datasize--dcid>: <"+this.datasize+"--"+this.dcId+">";
	}
	@Override
	public int compareTo(Task o) {
		if(datasize>o.datasize){
			return -1;
		}else if(datasize<o.datasize){
			return 1;
		}
		return 0;
	}
}
