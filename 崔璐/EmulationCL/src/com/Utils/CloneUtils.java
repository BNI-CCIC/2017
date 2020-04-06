package com.cf.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class CloneUtils {

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(T obj){
		T cloneObj = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream obs = new ObjectOutputStream(out);
			obs.writeObject(obj);
			obs.close();
			
			ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(ios);
			cloneObj = (T) ois.readObject();
			ois.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cloneObj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> deepCopy(List<T> src) {  
	    List<T> dest = null; 
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(src);  
		    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
		    ObjectInputStream in = new ObjectInputStream(byteIn);   
		    dest=(List<T>) in.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	    return dest;  
	}  

}
