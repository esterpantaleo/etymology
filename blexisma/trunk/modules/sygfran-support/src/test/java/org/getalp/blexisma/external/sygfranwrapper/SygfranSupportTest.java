package org.getalp.blexisma.external.sygfranwrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.getalp.blexisma.external.sygfranwrapper.structure.tree.SygMorphoSyntacticTree;
import org.getalp.blexisma.external.sygfranwrapper.tools.MorphoTreeToXML;
import org.junit.Before;
import org.junit.Test;

public class SygfranSupportTest {

	private static String sygfran;

	static {
		sygfran = "ELEM(VariableAnalyseSyntaxique,STR([1]( 1 ( 2 ( 3 ( 4 ( 5 , 6 ( 7 , 8 , 9 , 10 ( 11 ))), 12 ( 13 ), 14 ), 15 ( 16 ( 17 ), 18 ( 19 "
				+ ", 20 ( 21 , 22 , 23 , 24 ( 25 ))), 26 ))))),VTQ( 1 (), 2 (Balise(PHAMBG),FLX(PHAMBG)), 3 (Balise(PHINF),POSITION(SOMMET_PHRASE),"
				+ "MAJUSCULE(1),EXTREMPH(1),CATRAC(VB),MODE(INFINITI),INF(PRES),CAT(V),SOUSV(INFI),TYP(TRANS,TRANSCONJ,RFLX),CASPRNML(NEXCIND),POT("
				+ "AVOIR),K(PHRASE),KPH(PHINF),TPH(DCL),TPREPTRIND(A),PLFIN(47 ),FOBJ(1)), 4 (Balise(GV),MAJUSCULE(1),EXTREMPH(1),CATRAC(VB),MODE(INFINITI)"
				+ ",INF(PRES),CAT(V),SOUSV(INFI),TYP(TRANS,TRANSCONJ),CASPRNML(NEXCIND),POT(AVOIR),K(GV),TPREPTRIND(A),FOBJ(1)), 5 (Balise(Communiquer),"
				+ "POSITION(MOT_TEXTE),MAJUSCULE(1),EXTREMPH(1),FRM(Communiquer),CATRAC(VB),MODE(INFINITI),INF(PRES),CAT(V),SOUSV(INFI),TYP(TRANS,TRANSCONJ)"
				+ ",CASPRNML(NEXCIND),POT(AVOIR),LEMME(communiquer),FS(GOV),TPREPTRIND(A),FLX(communiquer)), 6 (Balise(GNPREP),CATRAC(VB),GNR(FEM),"
				+ "NUM(SIN),CAT(N),SOUSN(NCOM),K(GNPREP),FS(OBJT),TPREPNM(DE),CASPREPSIMPLE(DE)), 7 (Balise(de),PLACEMOT(22 ),PLACEMOTCHAR(22 ),POSITION("
				+ "MOT_TEXTE),FRM(de),CAT(PREP),TYP(PARTITIF),LEMME(de),FS(DES),TPREPNM(DE),CASPREPSIMPLE(DE),FLX(de)), 8 (Balise(l’),PLACEMOT(25 )"
				+ ",PLACEMOTCHAR(25 ),POSITION(MOT_TEXTE),FRM(l’),GNR(MAS,FEM),NUM(SIN),PERS(3),CAT(DETERM),SOUSD(ARTD),LEMME(le),FLX(le)), 9 (Balise(information),"
				+ "PLACEMOT(29 ),PLACEMOTCHAR(27 ),MPHDER(ATION),POSITION(MOT_TEXTE),FRM(information),CATRAC(VB),GNR(FEM),NUM(SIN),CAT(N),SOUSN(NCOM)"
				+ ",LEMME(information),FS(GOV),FLX(informer)), 10 (Balise(GA),GNR(FEM),NUM(SIN),CAT(ADJOINT),SOUSA(ADNOM),K(GA),FS(ATTR)), 11 (Balise(fausse),"
				+ "PLACEMOT(41 ),PLACEMOTCHAR(39 ),POSITION(MOT_TEXTE),PONCTSUFF(1),FRM(fausse),GNR(FEM),NUM(SIN),CAT(ADJOINT),SOUSA(ADNOM),LEMME(faux),"
				+ "FS(GOV),FLX(faux)), 12 (Balise(GADV),CAT(ADJOINT),SOUSA(ADVERB),K(GADV),FS(COMPCIR)), 13 (Balise(sciemment),PLACEMOT(12 ),PLACEMOTCHAR(12 )"
				+ ",POSITION(MOT_TEXTE),FRM(sciemment),CAT(ADJOINT),SOUSA(ADVERB),LEMME(sciemment),FS(GOV),FLX(sciemment)), 14 (Balise(.),PLACEMOT(47 )"
				+ ",PLACEMOTCHAR(45 ),POSITION(MOT_TEXTE),EXTREMPH(1),FRM(.),CAT(PONCT),CATPONCT(POINT),FONCT(TERMINAISON),LEMME(.),FLX(.)), 15 (Balise(PHELIS),"
				+ "POSITION(SOMMET_PHRASE),MAJUSCULE(1),EXTREMPH(1),CATRAC(VB),MODE(INFINITI),INF(PRES),CAT(N),SOUSV(INFI),SOUSN(NPRO),TYP(TRANS,TRANSCONJ,"
				+ "TRANSIND,RFLX),CASPRNML(NEXCIND),POT(AVOIR),K(PHRASE),KPH(PHELIS),TPH(DCL),TPREPTRIND(A),MQORIGINE(27 )), 16 (Balise(GN),MAJUSCULE("
				+ "1),EXTREMPH(1),CATRAC(VB),MODE(INFINITI),INF(PRES),CAT(N),SOUSV(INFI),SOUSN(NPRO),TYP(TRANS,TRANSCONJ,TRANSIND,RFLX),CASPRNML(NEXCIND)"
				+ ",POT(AVOIR),K(GN),FS(SUJ),TPREPTRIND(A),MQORIGINE(27 )), 17 (Balise(Communiquer),POSITION(MOT_TEXTE),MAJUSCULE(1),EXTREMPH(1),FRM(Communiquer),"
				+ "CATRAC(VB),MODE(INFINITI),INF(PRES),CAT(N),SOUSV(INFI),SOUSN(NPRO),TYP(TRANS,TRANSCONJ,TRANSIND,RFLX),CASPRNML(NEXCIND),POT(AVOIR)"
				+ ",LEMME(communiquer),FS(GOV),TPREPTRIND(A),FLX(Nom\\ propre\\ généré),MQORIGINE(27 )), 18 (Balise(GADV),CAT(ADJOINT),SOUSA(ADVERB)"
				+ ",K(GADV),FS(COMPCIR),MQORIGINE(12 )), 19 (Balise(sciemment),PLACEMOT(12 ),PLACEMOTCHAR(12 ),POSITION(MOT_TEXTE),FRM(sciemment),CAT("
				+ "ADJOINT),SOUSA(ADVERB),LEMME(sciemment),FS(GOV),FLX(sciemment),MQORIGINE(13 )), 20 (Balise(GNPREP),CATRAC(VB),GNR(FEM),NUM(SIN),"
				+ "CAT(N),SOUSN(NCOM),K(GNPREP),FS(ATTR),TPREPNM(DE),CASPREPSIMPLE(DE),MQORIGINE(6 )), 21 (Balise(de),PLACEMOT(22 ),PLACEMOTCHAR(22 )"
				+ ",POSITION(MOT_TEXTE),FRM(de),CAT(PREP),LEMME(de),FS(DES),TPREPNM(DE),CASPREPSIMPLE(DE),FLX(de),MQORIGINE(7 )), 22 (Balise(l’),"
				+ "PLACEMOT(25 ),PLACEMOTCHAR(25 ),POSITION(MOT_TEXTE),FRM(l’),GNR(MAS,FEM),NUM(SIN),PERS(3),CAT(DETERM),SOUSD(ARTD),LEMME(le),FLX(le)"
				+ ",MQORIGINE(8 )), 23 (Balise(information),PLACEMOT(29 ),PLACEMOTCHAR(27 ),MPHDER(ATION),POSITION(MOT_TEXTE),FRM(information),CATRAC("
				+ "VB),GNR(FEM),NUM(SIN),CAT(N),SOUSN(NCOM),LEMME(information),FS(GOV),FLX(informer),MQORIGINE(9 )), 24 (Balise(GA),GNR(FEM),NUM(SIN)"
				+ ",CAT(ADJOINT),SOUSA(ADNOM),K(GA),FS(ATTR)), 25 (Balise(fausse),PLACEMOT(41 ),PLACEMOTCHAR(39 ),POSITION(MOT_TEXTE),PONCTSUFF(1),"
				+ "FRM(fausse),GNR(FEM),NUM(SIN),CAT(ADJOINT),SOUSA(ADNOM),LEMME(faux),FS(GOV),FLX(faux),MQORIGINE(11 )), 26 (Balise(.),PLACEMOT(47 )"
				+ ",PLACEMOTCHAR(45 ),POSITION(MOT_TEXTE),EXTREMPH(1),FRM(.),CAT(PONCT),CATPONCT(POINT),FONCT(TERMINAISON),LEMME(.),FLX(.),MQORIGINE(28 )"
				+ "), 27 (Balise(Communiquer),POSITION(MOT_TEXTE),MAJUSCULE(1),EXTREMPH(1),FRM(Communiquer),CATRAC(VB),MODE(INFINITI),INF(PRES),CAT("
				+ "N),SOUSV(INFI),SOUSN(NPRO),TYP(TRANS,TRANSCONJ,TRANSIND,RFLX),CASPRNML(NEXCIND),POT(AVOIR),LEMME(communiquer),TPREPTRIND(A),FLX(Nom\\ propre\\ généré)"
				+ ",MQORIGINE(5 )), 28 (Balise(.),PLACEMOT(47 ),PLACEMOTCHAR(45 ),POSITION(MOT_TEXTE),EXTREMPH(1),FRM(.),CAT(PONCT),CATPONCT(POINT)"
				+ ",FONCT(TERMINAISON),LEMME(.),FLX(.),UL(.),MQORIGINE(14 ))),NOM_ETIQUETTES())";
	}

	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParsing() {
		SygMorphoSyntacticTree tree = new SygMorphoSyntacticTree(sygfran);
		String rootlemma = tree.getInfos().getLEMME();
		assertEquals("Root lemma should be ROOT", rootlemma, "ROOT");
		assertEquals("Root should have only one child", tree.getChildren().size(), 1);
		assertFalse("Tree should not have a null child", containLeafWithNonNullChildren(tree));

		System.out.println(MorphoTreeToXML.outputXML(tree));
	}
	
	@Test
	public void testDefSciencesHumaines() throws IOException {
		InputStream ttt = this.getClass().getResourceAsStream("def_science_humaine.stx");
		BufferedReader br = new BufferedReader(new InputStreamReader(ttt,"UTF-8"));
		StringBuffer strb = new StringBuffer();
		String nl = System.getProperty("line.separator", "\n");
		String s = br.readLine();
		while (null != s) {
			strb.append(s);
			strb.append(nl); 
			s = br.readLine();	
		}
		SygMorphoSyntacticTree tree = new SygMorphoSyntacticTree(strb.toString());
		
		String rootlemma = tree.getInfos().getLEMME();
		assertEquals("Root lemma should be ROOT", rootlemma, "ROOT");
		assertEquals("Root should have only one child", tree.getChildren().size(), 1);
		
		assertTrue("Expected 165 nodes in tree", 165 == nbNodes(tree));
		assertFalse("Tree should not have a null child", containLeafWithNonNullChildren(tree));
		assertTrue("This particular tree has a leaf with an empty lemma", containLeafWithNullLemma(tree));
		// System.out.println(MorphoTreeToXML.outputXML(tree));
	}
	
	public static int nbNodes(SygMorphoSyntacticTree tree) {
		if (tree.isLeaf()) {
			return 1;
		} else {
			int n = 0;
			for (SygMorphoSyntacticTree child : tree.getChildren()) {
				n = n + nbNodes(child);
			}
			return n + 1;
		}
	}
	
	public static boolean containLeafWithNullLemma(SygMorphoSyntacticTree tree) {
		if (tree.isLeaf()) {
			return tree.getInfos().getLEMME() == null;
		} else {
			boolean c = false;
			for (SygMorphoSyntacticTree child : tree.getChildren()) {
				c = c || containLeafWithNullLemma(child);
			}
			return c;
		}
	}
	public static boolean containLeafWithNonNullChildren(SygMorphoSyntacticTree tree) {
		if (tree.isLeaf()) {
			return tree.getChildren() != null;
		} else {
			boolean c = false;
			for (SygMorphoSyntacticTree child : tree.getChildren()) {
				c = c || containLeafWithNonNullChildren(child);
			}
			return c;
		}
	}
}
