package acfast;

public class Global {
    //Type de Mots
    public static final int NONE=-1;
    public static final int NOUN=0;
    public static final int VERB=1;
    public static final int ADJECTIVE_SATELLITE=2;
    public static final int ADJECTIVE=3;
    public static final int ADVERB=4;


    public static int convert(String cat){

	cat=cat.toUpperCase();
	if(cat.equals("NOUN"))
	    return NOUN;
	else
	    if(cat.equals("VERB"))
		return VERB;
	    else
		if(cat.equals("ADJECTIVE_SATELLITE"))
		    return ADJECTIVE_SATELLITE;
		else
		    if(cat.equals("ADJECTIVE"))
			return ADJECTIVE;
		    else
			if(cat.equals("ADVERB"))
			    return ADVERB;
			else
			    return NONE;
    }
}
