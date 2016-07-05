/**
 *
 */
package org.getalp.dbnary.eng;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.*;
import org.getalp.dbnary.wiki.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pantaleo
 *
 */

public class ArrayListPOE extends ArrayList<POE>{
    static Logger log = LoggerFactory.getLogger(ArrayListPOE.class);

    public void replaceMatch(ArrayList<Pair> m){
        StringBuilder string = new StringBuilder();  
        for (int i=m.size()-1; i>=0; i--){//iterate starting from ArrayList end
            //System.out.format("%s\n", i);
            //System.out.format("working on match (%s,%s) ", m.get(i).start, m.get(i).end);
            int counter = 0; 
            string.setLength(0);
            wholeloop:for (int k=m.get(i).start; k<m.get(i).end+1; k++){
                //System.out.format("loop on k: k=%s\n", k);
	        partloop: for (int j=0; j<this.get(k).part.size(); j++){
                    //System.out.format("loop on j: j=%s\n", j);  
                    //System.out.format("this.get(k).args=%s\n", this.get(k).args);
                    if (this.get(k).part.get(j).equals("LEMMA")){
                        //System.out.format("equals LEMMA, counter=%s\n", counter);
                        if (counter == 0){
                            string.append("compound|");
                            //System.out.format("string=%s\n", string);
			    if (this.get(k).args.get("lang") != null){
                                string.append(this.get(k).args.get("lang"));
			    } else {
                                log.debug("no language in {}", this.get(k).args);
			    }
			    string.append("|");
                            string.append(this.get(k).args.get("word1"));
                            string.append("|");
                            counter = 1;
                            //System.out.format("string=%s; breaking partloop\n", string);
                            break partloop;
		        } else {
                            if (this.get(k).args != null){
			        string.append(this.get(k).args.get("word1"));
                            } 
			    //System.out.format("replacing compound with string: %s\n", string.toString());
			    POE p = new POE(string.toString(), 1);//1 corresponds to "TEMPLATE"
			    this.set(m.get(i).start, p);
			    if (m.get(i).end>m.get(i).start){
			        this.subList(m.get(i).start+1, m.get(i).end+1).clear();
			    }
			    //System.out.format("this.subList(%s,%s).clear()", m.get(i).start, m.get(i).end+1);
                            break wholeloop; 
			}
		    }
		}
	    }
            //System.out.format("\n");
	}
                                                                                                                                                                      
        /*int j;         
        for (int i=0; i<matches.size(); i++){                                                                                                                                    
            j = matches.get(matches.size()-i-2);                                                                                                                                        
            System.out.format("%s\n", a.get(j).args.get("1"));                                                                                                                                      
            if (j == matches.get(matches.size()-i-1)){                                                                                                                                            
                System.out.format("the match has length 1, so cannot be compressed anymore; leave everything as it is only replace with LEMMA??");                   
            } else if (a.get(j).args.get("1").equals("compound")){                                                                              
                System.out.format("use COMPOUND template and compress to LEMMA");                                                                              
            } else if (a.get(j).args.get("1").equals("etycomp")){//All parameters except word1= can be omitted.                                                         
                System.out.format("use ETYCOMP template and compress to LEMMA");//{{etycomp|lang1=de|inf1=|case1=|word1=dumm|trans1=dumb|lang2=|inf2=|case2=|word2=Kopf|trans2=head}}                   
          } else if (a.get(j).args.get("1").equals("calque")){                                                    
                System.out.format("use CALQUE template and compress to LEMMA");             
            } else if (a.get(j).args.get("1").equals("blend")){                                        
                System.out.format("use BLEND template and compress to LEMMA");                                                                   
            } else {                                                                                                                 
                System.out.format("replace with a brand new COMPOUND template and compress to LEMMA");  
            }                                                                                                  
            System.out.format("\n");                                                                           
            i++;
	    }*/
    }

    public ArrayList<Pair> match(Pattern p){
        ArrayList<Integer> arrayListInteger = new ArrayList<Integer>();
        ArrayList<Integer> arrayListPosition = new ArrayList<Integer>();
        int c = 0;

        arrayListPosition.add(c);
        for (int i=0; i<this.size(); i++){
            for (int j=0; j<this.get(i).part.size(); j++){
                arrayListInteger.add(i);
                c += this.get(i).part.get(j).length()+1;
                arrayListPosition.add(c);
	    }
	}
        Matcher m = p.matcher(this.toString());

        ArrayList<Pair> toreturn = new ArrayList<Pair>(); 
        while (m.find()) {
            int start = -1, end = -1;
            for (int i=0; i<arrayListPosition.size()-1; i++){
                if (arrayListPosition.get(i) == m.start()){
                    start = arrayListInteger.get(i);
		} else if (arrayListPosition.get(i+1) == m.end()){
                    end = arrayListInteger.get(i);
		}
	    }
            if (start<0 || end<0){
                System.out.format("Error: start or end of match are not available\n");
	    }
            toreturn.add(new Pair(start, end));
        }
        return toreturn;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
   
        for (int i=0; i<this.size(); i++){
            for (int j=0; j<this.get(i).part.size(); j++){
                s.append(this.get(i).part.get(j));
                if (!(this.get(i).part.equals("ERROR"))){
		    s.append(" ");
		}
	    }
	}
        return s.toString();
    }

    public int getIndexOfCognateOr(){
        for (int i=0; i<this.size(); i++){
            if (this.get(i).part.size()>0){
                if (this.get(i).part.get(0).equals("COGNATE_WITH") || this.get(i).part.get(0).equals("OR")){
		    return i;
		}
	    }
	}
        //if there is no "COGNATE_WITH" POE return size 
        return this.size();
    }
}