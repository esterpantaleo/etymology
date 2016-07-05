package org.getalp.blexisma.utils;

import org.getalp.blexisma.api.ISO639_3;

public class BaseFilter {
	private String filterString;
	
	public BaseFilter(String s) {
		filterString = s;
	}
	
	public boolean matchFilter(String s){
		if (filterString==null) return true;
		else return s.indexOf(filterString)>-1;
	}
	
	public static BaseFilter createDefFilter(){
		return new BaseFilter("#def|");
	}
	
	public static BaseFilter createEngLemmaFilter(){
		return new BaseFilter("#"+ISO639_3.sharedInstance.getIdCode("eng")+"|");
	}
	
	public static BaseFilter createFraLemmaFilter(){
		return new BaseFilter("#"+ISO639_3.sharedInstance.getIdCode("fra")+"|");
	}
	
	public static BaseFilter createGerLemmaFilter(){
		return new BaseFilter("#"+ISO639_3.sharedInstance.getIdCode("ger")+"|");
	}
	
	public static BaseFilter createYesFilter(){
		return new BaseFilter(null);
	}
}
