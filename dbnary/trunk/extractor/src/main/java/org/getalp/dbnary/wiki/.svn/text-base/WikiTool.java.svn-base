package org.getalp.dbnary.wiki;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiTool {

	// Parse a string of args, like: xxx=yyy|zzz=ttt

	/**
	 * @deprecated
	 * Parse the args of a Template.
	 * @param args the String containing all the args (the part of a templae contained after the first pipe).
	 * @return a Map associating each argument name with its value.
     */
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

	static Pattern htmlRefElement = Pattern.compile("(<ref(?:\\s[^>]*|\\s*)>)|(</ref>)");
	// WARN: not synchronized !
	public static String removeReferencesIn(String definition) {
		StringBuffer def = new StringBuffer();
		Matcher m = htmlRefElement.matcher(definition);
        boolean mute = false;
        int previousPos = 0;
		while (m.find()) {
			if (null != m.group(1) && m.group().endsWith("/>")) {
				// A opening/closing element
				if (! mute) def.append(definition.substring(previousPos, m.start()));
			} else if (null != m.group(1)) {
                // An opening element
				if (! mute) def.append(definition.substring(previousPos, m.start()));
				mute = true;
			} else if (null != m.group(2)) {
                // a closing element
                if (! mute) def.append(definition.substring(previousPos, m.start()));
                mute = false;
            }
            previousPos = m.end();
        }
        if (! mute) def.append(definition.substring(previousPos, definition.length()));
		return def.toString();
	}

    public static String toParameterString(Map<String, String> parameterMap) {
        StringBuffer buf = new StringBuffer();
        for (Map.Entry<String, String> stringStringEntry : parameterMap.entrySet()) {
            buf.append(stringStringEntry.getKey())
                    .append("=")
                    .append(stringStringEntry.getValue())
                    .append("|");
        }
        if (buf.length() > 0) buf.delete(buf.length()-1,buf.length());
        return buf.toString();
    }
}
