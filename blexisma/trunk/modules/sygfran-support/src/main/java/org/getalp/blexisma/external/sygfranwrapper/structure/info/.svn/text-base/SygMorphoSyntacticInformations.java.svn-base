package org.getalp.blexisma.external.sygfranwrapper.structure.info;

import org.getalp.blexisma.utils.Patterner;


/**
 * @author Didier Schwab, Alexandre Labadié
 * Class managing MorphoSyntacticInformations for SYGFRAN analysis
 * */
public class SygMorphoSyntacticInformations implements MorphoSyntacticInformations
{

	/**
	 * Generated ID
	 */
	public static final long serialVersionUID = -2576090306136268305L;
	public String BAL; //balise
	public String FRM; //forme
	public String GNR; //genre : MAS, FEM
	public String NUM; //nombre : SIN
	public String CAT; //catégorie morphologique : V, REP
	public String FLX; // flexion : inconnue *INC
	public String LEMME; //lemme
	public String FS; //fonction syntaxique
	public String ASSERT; //assertion (négative) : NEG

	public String SOUSV;
	public String SOUSA;
	public String SOUSN;
	public String SOUSD;
	public String SOUSR;
	public String SOUSC;
	public String SOUSP;
	public String VECTS; //vecteur
	
	/**
	 * Class builder
	 * */
	public SygMorphoSyntacticInformations() 
	{
		BAL = null;
	    FRM = null;
	    GNR = null;
	    NUM = null;
	    CAT = null;
	    FLX = null;
	    LEMME = null;
	    FS = null;
	    ASSERT = null;
	    SOUSV = null;
	    SOUSA = null;
	    SOUSN = null;
	    SOUSD = null;
	    SOUSR = null;
	    SOUSC = null;
	    SOUSP = null;
	}
	
	/**
	 * @return Formated morphological information
	 * */
	public String getMorpho()
	{
	    StringBuffer rep = new StringBuffer();
	    String morpho = null;

	    if(CAT!=null)
	    {
	      if(CAT.equals("ADJOINT"))
	      {

	        if(SOUSA!=null) rep.append(SOUSA);
	        else System.out.println("SOUSA null");
	      }
	      else if(CAT.equals("v"))
	      {
	      rep.append(CAT);
	      }
	      else if(CAT.equals("n"))
	      {
	        if(GNR!=null) rep.append(GNR+" ");
	        if(NUM!=null) rep.append(NUM +" ");
	        if(SOUSN!=null) rep.append(CAT +" "+SOUSN);
	        else System.out.println("SOUSN null");
	      }
	      else
	      {
	            rep.append(CAT);
	      }

	    }

	    morpho = Patterner.patterner(rep.toString(), ",", " ");

	    return morpho;
	}
	
	/**
	 * @return Morpho-syntactic informations in String format
	 * */
	public String toString()
	{
	    return "[BALISE = "+BAL+", LEMME = "+LEMME+ ", ASSERT = " + ASSERT + ", FS = "+FS+", CAT = "+CAT+", FLX = "+FLX+", FRM = "+FRM+", GNR = "+GNR+", NUM = "+NUM+", SOUSV = "+SOUSV+", SOUSA = "+SOUSA+", SOUSN = "+SOUSN+", SOUSD = "+SOUSD+", SOUSR = "+SOUSR+", SOUSC = "+SOUSC+", SOUSP = "+SOUSP+']';
	}

	public String getFRM() {
		return FRM;
	}

	public void setFRM(String fRM) {
		FRM = fRM;
	}

	public String getGNR() {
		return GNR;
	}

	public void setGNR(String gNR) {
		GNR = gNR;
	}

	public String getNUM() {
		return NUM;
	}

	public void setNUM(String nUM) {
		NUM = nUM;
	}

	public String getCAT() {
		return CAT;
	}

	public void setCAT(String cAT) {
		CAT = cAT;
	}

	public String getFLX() {
		return FLX;
	}

	public void setFLX(String fLX) {
		FLX = fLX;
	}

	public String getLEMME() {
		return LEMME;
	}

	public void setLEMME(String lEMME) {
		LEMME = lEMME;
	}

	public String getFS() {
		return FS;
	}

	public void setFS(String fS) {
		FS = fS;
	}

	public String getASSERT() {
		return ASSERT;
	}

	public void setASSERT(String aSSERT) {
		ASSERT = aSSERT;
	}

	public String getSOUSV() {
		return SOUSV;
	}

	public void setSOUSV(String sOUSV) {
		SOUSV = sOUSV;
	}

	public String getSOUSA() {
		return SOUSA;
	}

	public void setSOUSA(String sOUSA) {
		SOUSA = sOUSA;
	}

	public String getSOUSN() {
		return SOUSN;
	}

	public void setSOUSN(String sOUSN) {
		SOUSN = sOUSN;
	}

	public String getSOUSD() {
		return SOUSD;
	}

	public void setSOUSD(String sOUSD) {
		SOUSD = sOUSD;
	}

	public String getSOUSR() {
		return SOUSR;
	}

	public void setSOUSR(String sOUSR) {
		SOUSR = sOUSR;
	}

	public String getSOUSC() {
		return SOUSC;
	}

	public void setSOUSC(String sOUSC) {
		SOUSC = sOUSC;
	}

	public String getSOUSP() {
		return SOUSP;
	}

	public void setSOUSP(String sOUSP) {
		SOUSP = sOUSP;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

