package org.getalp.dbnary.tools;

import java.util.HashMap;

public class CounterSet {
	
	class MutableInteger {
		int _val = 0;
		
		MutableInteger(int v) {
			super();
			_val = v;
		}
		
		void set(int v) {
			_val = v;
		}
		
		int incr() {
			return ++_val;
		}
		
		void reset() {
			_val = 0;
		}
		
		public String toString() {
			return Integer.toString(_val);
		}
	}
	private HashMap<String,MutableInteger> counters;

	public CounterSet() {
		super();
		counters = new HashMap<String,MutableInteger>();
	}
	
	public void clear() {
		counters.clear();
	}
	
	public void resetAll() {
		for (MutableInteger i : counters.values()) {
			i.reset();
		}
	}
	
	public void reset(String key) {
		MutableInteger i = counters.get(key);
		if (null != i) {
			i.reset();
		} 
	}
	
	public int get(String key) {
		MutableInteger i = counters.get(key);
		return (null != i) ? i._val: 0;
	}
	
	public int incr(String key) {
		MutableInteger i = counters.get(key);
		if (null != i) {
			return i.incr();
		} else {
			counters.put(key, new MutableInteger(1));
			return 1;
		}
	}
}
