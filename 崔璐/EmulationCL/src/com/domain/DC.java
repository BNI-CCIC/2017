package com.cf.domain;

import java.io.Serializable;

public class DC implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4711066850315690882L;
	private int dcId;
	private int CPUCapacity;
	private int residualCPU;
	private int matrixId;
	public DC() {
		// TODO Auto-generated constructor stub
	}
	public DC(int dcId, int cPUCapacity, int residualCPU, int matrixId) {
		super();
		this.dcId = dcId;
		CPUCapacity = cPUCapacity;
		this.residualCPU = residualCPU;
		this.matrixId = matrixId;
	}
	public int getDcId() {
		return dcId;
	}
	public void setDcId(int dcId) {
		this.dcId = dcId;
	}
	public int getCPUCapacity() {
		return CPUCapacity;
	}
	public void setCPUCapacity(int cPUCapacity) {
		CPUCapacity = cPUCapacity;
	}
	public int getResidualCPU() {
		return residualCPU;
	}
	public void setResidualCPU(int residualCPU) {
		this.residualCPU = residualCPU;
	}
	public int getMatrixId() {
		return matrixId;
	}
	public void setMatrixId(int matrixId) {
		this.matrixId = matrixId;
	}
	
}
