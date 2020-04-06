package com.cf.Utils;

import java.util.ArrayList;
import java.util.List;

import com.cf.domain.DC;
import com.cf.domain.Job;
import com.cf.domain.Link;

public class DB {
	
	public static List<DC> dcList = new ArrayList<DC>(); 
	public static List<Link> linkList = new ArrayList<Link>();
	public static List<Job> jobList = new ArrayList<Job>();
	
	public static Link getLink(int start, int dest){
		Link res = new Link();
		for(Link link: linkList){
			if(link.getStart()==start && link.getDest()==dest){
				res = link;
				return res;
			}
		}
		return null;
	}
}
