package Dictionnary;

import java.util.Arrays;
import java.util.StringTokenizer;


public class Definition {
	private int[] def;
	
	public Definition(String d) {
		
		StringTokenizer st = new StringTokenizer(d);
		def = new int[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens()){
			def[i] = Integer.parseInt(st.nextToken());
			//System.out.println(def[i]);
			i++;
		}
	}
	
	public Definition(int size) {
		def = new int[size];
		for(int i = 0; i < size; i++){
			def[i]=-1;
		}
	}
	
	public int getSize() {
		return def.length;
	}
	
	public void setElement(int index, int e) {
		def[index] = e;
		Arrays.sort(def);
	}
	
	public int getElement(int index) {
		return def[index];
	}
	
	public int getSim(Definition d) {
	    
		int count = 0;
		int i = 0;
		int j = 0;
		while(i < this.def.length && j < d.def.length){
			if(this.def[i] == d.def[j]){
				count++;
				i++;
				j++;
			}else if (this.def[i] < d.def[j]){
				i++;
			}else{
				j++;
			}
		}
		return count;
	}
	
	
	public String toString() {
		String s = "";
		for(int i = 0; i < def.length; i++){
			s = s+def[i]+" ";
		}
		return s;
	}

}
