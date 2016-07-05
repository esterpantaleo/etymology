package org.getalp.dbnary.wiki;

import java.util.HashMap;
import java.util.Map;

public class WikiTool {

	// Parse a string of args, like: xxx=yyy|zzz=ttt
	public static Map<String,String> parseArgs(String args) {
		HashMap<String,String> res = new HashMap<String,String>();
		if (null == args) return res;
		int n = 1; // number for positional args.
		String[] pairs = args.split("\\|");
		for (int i = 0; i < pairs.length; i++) {
			if (null == pairs[i]) continue;
			String[] s = pairs[i].trim().split("\\=");
			if (s.length < 2) {
				// There is no argument name.
				res.put(""+n, s[0]);
				n++;
			} else {
				res.put(s[0], s[1]);
			}
		}
		return res;
	}
	
}
