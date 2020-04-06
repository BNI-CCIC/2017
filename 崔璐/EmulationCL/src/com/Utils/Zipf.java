package com.cf.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

public class Zipf {
	private int R;     //number of data
	private double A;  //
	private final double C = 1;   //constant 1
	private double[] pf ;           //probability	
	public Zipf(int r, double a, double[] pf) {
		super();
		R = r;
		A = a;
		this.pf = pf;
	}
	
	public void generate(){
		double sum = 0.0;
		for (int i = 0; i < R; i++){        
			sum += C/Math.pow((double)(i+2), A);   
		}
		for (int i = 0; i < R; i++){        
			if (i == 0)            
				pf[i] = C/Math.pow((double)(i+2), A)/sum;        
			else            
				pf[i] = pf[i-1] + C/Math.pow((double)(i+2), A)/sum;    
		}
	}
	/**
	 * select n DCs
	 * @param n
	 * @return
	 */
	public List<Integer> pick(int n){
		//n = jobSize
		List<Integer> T = new ArrayList<Integer>();
		for (int i = 0; i < n; i++){
			int index = (int)(Math.random()*(R+1));
			while(index>(R-1)){
				index = (int)(Math.random()*(R+1));
			}
			double data = Math.random(); 
			while (data > pf[index]){	
				//find index
				if(index==(R-1)){
					break;
					}
				index++;
				}
			T.add(index);
		}
		return T;
	}
	
	/**
	 * 判断list是否有重复
	 * @param list
	 * @return
	 */
	public boolean isRepetition(List<Integer> list){
		Set<Integer> set = new HashSet<Integer>();
		for(int i: list){
			set.add(i);
		}
		if(set.size()==list.size()){
			return false;
		}else{
			return true;
		}
	}
	
	public List<Integer> NoRepetitionPick(int n){
		boolean flag = true;
		List<Integer> res = new ArrayList<>();
		while(flag){
			res = pick(n);
			flag = isRepetition(res);
		}
		return res;
		
	}
	
}
