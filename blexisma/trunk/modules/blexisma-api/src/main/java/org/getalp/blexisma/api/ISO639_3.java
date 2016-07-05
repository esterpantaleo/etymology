package org.getalp.blexisma.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author serasset
 * Support class for ISO 639-3 standard for language naming.
 * 
 *  This class is designed to be used as a singleton.
 *  
 *   Usage:
 *   <code>ISO639_3 isoLanguages = ISO639_3.sharedInstance;
 *   String french = isoLanguages.getLanguageNameInEnglish("fre");</code>
 *   
 */
public class ISO639_3 {

        public class Lang {
            /**
			 * @return the id
			 */
			public String getId() {
				return id;
			}

			/**
			 * @return the part2b
			 */
			public String getPart2b() {
				return part2b;
			}

			/**
			 * @return the part2t
			 */
			public String getPart2t() {
				return part2t;
			}

			/**
			 * @return the part1
			 */
			public String getPart1() {
				return part1;
			}

			/**
			 * @return the fr
			 */
			public String getFr() {
				return fr;
			}

			/**
			 * @return the en
			 */
			public String getEn() {
				return en;
			}

			/**
			 * @return the epo
			 */
			public String getEpo() {
				return epo;
			}

			private String id, part2b, part2t, part1, fr, en, epo;
            
        }
        private final static String linePatternString =
            "^(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)\t(.*?)$";
        private final static String epolinePatternString =
            "^(.*?)\t(.*?)$";
        private final static Pattern linePattern = Pattern.compile(linePatternString);
        private final static Pattern epolinePattern = Pattern.compile(epolinePatternString);
           
        public static ISO639_3 sharedInstance = new ISO639_3();
        private Map<String, Lang> langMap = new HashMap<String,Lang>();
        private Set<Lang> langSet = new HashSet<Lang>();

        private ISO639_3() {
            InputStream fis = null;
            try {
                fis = this.getClass().getResourceAsStream("iso-639-3.tab");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                
                Matcher matcher = linePattern.matcher(new String(""));

                String s = br.readLine();
                while (s != null) {
                    matcher.reset(s);
                    if (matcher.find()) {
                        Lang l = new Lang();
                        // Id   Part2B  Part2T  Part1   Scope   Language_Type   Ref_Name
                        l.id = matcher.group(1);
                        l.part2b = matcher.group(2);
                        l.part2t  = matcher.group(3);
                        l.part1  = matcher.group(4);
                        l.en  = matcher.group(7);
                        
                        langSet.add(l);
                        langMap.put(l.id, l);
                        langMap.put(l.part1, l);
                        langMap.put(l.part2b, l);
                        langMap.put(l.part2t, l);
                        
                    } else {
                        System.out.println("Unrecognized line:" + s);                    
                    }
                    s = br.readLine();
                }
                
                
            } catch (UnsupportedEncodingException e) {
                // This should really never happen
            } catch (IOException e) {
                // don't know what I should do here, as the data should be bundled with the code.
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // nop
                    }
            }
            // Get eponym language names
            // TODO: do it lazily.
            fis = null;
            try {
                fis = this.getClass().getResourceAsStream("ISO639-eponym.tab");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                
                Matcher matcher = epolinePattern.matcher(new String(""));

                String s = br.readLine();
                while (s != null) {
                    matcher.reset(s);
                    if (matcher.find()) {
                        // System.out.println(matcher.group(5));
                        // a3b, a3t, a2, en, fr
                        Lang l = langMap.get(matcher.group(1));
                        if (l != null)
                            l.epo = matcher.group(2);
                        // else 
                            // System.out.println("Unknown language code: " + matcher.group(1));        
                    } else {
                        System.out.println("Unrecognized line:" + s);                    
                    }
                    s = br.readLine();
                }
            } catch (UnsupportedEncodingException e) {
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        
                    }
            }
            // Get French names
            // TODO: do this lazily
            fis = null;
            try {
                fis = this.getClass().getResourceAsStream("ISO639-fr.tab");
                if (fis == null) throw new IOException("ISO639 French data not available");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                
                Matcher matcher = epolinePattern.matcher(new String(""));

                String s = br.readLine();
                while (s != null) {
                    matcher.reset(s);
                    if (matcher.find()) {
                        // System.out.println(matcher.group(5));
                        // a3b, a3t, a2, en, fr
                        Lang l = langMap.get(matcher.group(1));
                        if (l != null)
                            l.fr = matcher.group(2);
//                         else 
//                             System.out.println("Unknown language code: " + matcher.group(1));        
                    } else {
                        System.out.println("Unrecognized line:" + s);                    
                    }
                    s = br.readLine();
                }
            } catch (UnsupportedEncodingException e) {
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        
                    }
            }
        }
     
        public String getLanguageNameInFrench(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? l.fr : null ;
        }
        
        public String getLanguageNameInEnglish(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? l.en : null ;
        }
        
        public String getEponymLanguageName(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? l.epo : null ;
        }

        public String getBib3Code(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? l.part2b : null ;
        }
        
        public String getTerm3Code(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? (l.part2t == null) ? l.id : l.part2t : null ;
        }

        public String getIdCode(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? l.id : null ;
        }

        public String getTerm2Code(String langcode) {
            Lang l = langMap.get(langcode);
            return (l != null) ? l.part1 : null ;
        }
        
        public Lang getLang(String langcode) {
            return langMap.get(langcode);
        }
        
        public Iterator<Lang> knownLanguagesIterator() {
        	return langSet.iterator();
        }
        
        public static void main(String ars[]) {
            ;
        }
    

}
