package com.cf.domain;

import java.io.Serializable;

/**
 * @author Miracle
 *
 */
public class Link implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5266181556086717333L;
	private int start;
	private int dest;
	private int totalBW;
	private int residualBW;
	private int distence;
	public Link(int start, int dest, int totalBW, int residualBW, int distence) {
		super();
		this.start = start;
		this.dest = dest;
		this.totalBW = totalBW;
		this.residualBW = residualBW;
		this.distence = distence;
	}
	public Link() {
		// TODO Auto-generated constructor stub
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getDest() {
		return dest;
	}
	public void setDest(int dest) {
		this.dest = dest;
	}
	public int getTotalBW() {
		return totalBW;
	}
	public void setTotalBW(int totalBW) {
		this.totalBW = totalBW;
	}
	public int getResidualBW() {
		return residualBW;
	}
	public void setResidualBW(int residualBW) {
		this.residualBW = residualBW;
	}
	public int getDistence() {
		return distence;
	}
	public void setDistence(int distence) {
		this.distence = distence;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "start->dest:"+this.start+"-->"+this.dest+", BW:"+this.totalBW+", residual: "+this.residualBW+" distance:"+this.distence;
	}
	
}
