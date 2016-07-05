package org.getalp.dbnary;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class DbnaryModel {

	
	public static final String DBNARY_NS_PREFIX = "http://kaiko.getalp.org/dbnary";
	// protected static final String LMF = "http://www.lexicalmarkupframework.org/lmf/r14#";

	public static final String LEXVO = "http://lexvo.org/id/iso639-3/";

    public static Model tBox;

    static {
		// Create T-Box and read rdf schema associated to it.
		tBox = ModelFactory.createDefaultModel();

	}
	
	public static String uriEncode(String s) {
		StringBuffer res = new StringBuffer();
		uriEncode(s, res);
		return res.toString();
	}
	
	protected static void uriEncode(String s, StringBuffer res) {
		int i = 0;
		s = Normalizer.normalize(s, Normalizer.Form.NFKC);
		//                s.replaceAll("'", "__");
                //s.replaceAll("\\*", "_");
		while (i != s.length()) {
			char c = s.charAt(i);
			if (Character.isSpaceChar(c))
				res.append('_');
			//else if (c == '*') {
                        //    res.append('%2A');   
			//}
                        else if ((c >= '\u00A0' && c <= '\u00BF') ||
					(c == '<') || (c == '>') || (c == '%') ||
					(c == '"') || (c == '#') || (c == '[') || 
					(c == ']') || (c == '\\') || (c == '^') ||
					(c == '`') || (c == '{') || (c == '|') || 
				 (c == '}') || (c == '\u00D7') || (c == '\u00F7') 
					)
				try {
					res.append(URLEncoder.encode("" + c, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// Should never happen
					e.printStackTrace();
				}
			else if (Character.isISOControl(c))
				; // nop
			else if (c == '\u200e' || c == '\u200f') {
				; // ignore rRLM and LRM.
			} else
				res.append(c);
			i++;
		}
	}
	
	protected static String uriEncode(String s, String pos) {
		StringBuffer res = new StringBuffer();
		uriEncode(s, res);
		res.append("__");
		pos = Normalizer.normalize(pos, Normalizer.Form.NFKC);
		int i = 0;
		//s.replaceAll("'", "__");
                //s.replaceAll("\\*", "_");
		while (i != pos.length()) {
			char c = pos.charAt(i);
			if (Character.isSpaceChar(c))
				res.append('_');
                        //else if (c == '*') {
			//  res.append('%2A');
			//}
                        else if ((c >= '\u00A0' && c <= '\u00BF') ||
					(c == '<') || (c == '>') || (c == '%') ||
					(c == '"') || (c == '#') || (c == '[') || 
					(c == ']') || (c == '\\') || (c == '^') ||
					(c == '`') || (c == '{') || (c == '|') || 
					(c == '}') || (c == '\u00D7') || (c == '\u00F7') || 
					(c == '-') || (c == '_') || 
					Character.isISOControl(c))
				; // nop
			else if (c == '\u200e' || c == '\u200f') {
				; // ignore rRLM and LRM.
			} else
				res.append(c);
			i++;
		}
		return res.toString();
	}

}
