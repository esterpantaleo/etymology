package acfast;

//import helpers.SupressionPropositions;

import java.util.Date;

//import utils.TimeUtils;


public class FourmisLesk {

    public static void main(String argv[]) throws Exception {
	if(argv.length != 1){
	    System.out.println("Lesk fichierconfig.xml");
	}else{

	    Date D = new Date();
	    System.out.println("Creation du context");
	    Context context = new Context(argv[0]);
	    System.out.println("Fin de creation du context");

	    context.simulation();

	    //System.out.println("calcul en " + TimeUtils.formatTime((new Date()).getTime()-D.getTime()));
	    System.out.println("Debut ecriture du resultat");
	    int i = (int)(Math.random()*1000000);
	    context.writeResult(context.out+"answer"+i+".ans");
	  //  SupressionPropositions.suppressionPropositions(context.out+"answer"+i+".ans", context.fileTocompare, context.out+"ComparedAnswer"+i+".ans");
	    System.out.println("Fin ecriture du resultat");
	    
	}
    }
}