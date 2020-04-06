package com.cf.domain;

import java.io.Serializable;
import java.util.List;

public class Path implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1269956236220505713L;
	private int[] path;
	private List<Link> link;
	private int BW;
	public Path(int[] path, List<Link> link, int bW) {
		super();
		this.path = path;
		this.link = link;
		BW = bW;
	}
	public Path() {
		// TODO Auto-generated constructor stub
	}
	public int[] getPath() {
		return path;
	}
	public void setPath(int[] path) {
		this.path = path;
	}
	public List<Link> getLink() {
		return link;
	}
	public void setLink(List<Link> link) {
		this.link = link;
	}
	public int getBW() {
		return BW;
	}
	public void setBW(int bW) {
		BW = bW;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String str = " ";
		for(int i=0;i<path.length;i++){
			str+=path[i]+" ";
		}
		for(int i=0;i<link.size();i++){
			str+=link.get(i);
		}
		return "BW: "+BW+str+"\n";
	}
	
}
