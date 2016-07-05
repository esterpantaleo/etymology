/**
 * 
 */
package org.getalp.dbnary.hbs;

import org.getalp.dbnary.LangTools;
import java.util.HashMap;

/**
 * @author roques
 *
 */
public class SerboCroatianLangToCode extends LangTools {
	static HashMap<String,String> h = new HashMap<>();
	static {
		// based on EnglishLangToCode
        // Warning : translated into Croatian,
        // it may be that certain language is not well translated into Serbo Croatian
        h.put("Abhazijcima","ab");
        h.put("acehnese","ace");
        h.put("acholi","ach");
        h.put("adangme","ada");
        h.put("adigejski","ady");
        h.put("izdaleka","aa");
        h.put("afrihili","afh");
        h.put("afrikanski","af");
        h.put("Ainu","ain");
        h.put("Akan","ak");
        h.put("akadski","akk");
        h.put("albanski","sq");
        h.put("alemanski","gsw");
        h.put("aleutski","ale");
        h.put("amharski","am");
        h.put("starogrčki","grc");
        h.put("angika","anp");
        h.put("arapski","ar");
        h.put("aragonski","an");
        h.put("aramejski","arc");
        h.put("Arapaho","arp");
        h.put("arawak","arw");
        h.put("jermenski","hy");
        h.put("aromunjski","rup");
        h.put("Asamski","as");
        h.put("Asturijski","ast");
        h.put("Avar","av");
        h.put("avestan","ae");
        h.put("awadhi","awa");
        h.put("Aymara","ay");
        h.put("azerbejdžanski","az");
        h.put("balijsko","ban");
        h.put("baluchi","bal");
        h.put("Bambara","bm");
        h.put("basaa","bas");
        h.put("baškirski","ba");
        h.put("baskijski","eu");
        h.put("Beja","bej");
        h.put("bjeloruski","be");
        h.put("beloruski","be");
        h.put("Bemba","bem");
        h.put("bengalski","bn");
        h.put("bhojpuri","bho");
        h.put("bikol","bik");
        h.put("Bini","bin");
        h.put("Bislama","bi");
        h.put("Blackfoot","bla");
        h.put("Blin","byn");
        h.put("bosanski","bs");
        h.put("braj","bra");
        h.put("Breton","br");
        h.put("buginski","bug");
        h.put("bugarski","bg");
        h.put("burmanski","my");
        h.put("Buryat","bua");
        h.put("CADDO","cad");
        h.put("katalonski","ca");
        h.put("Cebuano","ceb");
        h.put("chagatai","chg");
        h.put("Chamorro","ch");
        h.put("Čečenski","ce");
        h.put("Cherokee","chr");
        h.put("Cheyenne","chy");
        h.put("chibcha","chb");
        h.put("Chichewa","ny");
        h.put("Chinook žargon","chn");
        h.put("chipewyan","chp");
        h.put("Choctaw","cho");
        h.put("chuukese","chk");
        h.put("kineski", "zh");
        h.put("kuvaski","cv");
        h.put("klasična newari","nwc");
        h.put("klasični sirski","syc");
        h.put("koptski","cop");
        h.put("kornvalski","kw");
        h.put("korzikanski","co");
        h.put("čuvaški","co");
        h.put("cree","cr");
        h.put("potok","mus");
        h.put("Name","crh");
        h.put("hrvatski","hr");
        h.put("češki","cs");
        h.put("Dakota","dak");
        h.put("danski","da");
        h.put("dargwa","dar");
        h.put("dhivehi","dv");
        h.put("Dinka","din");
        h.put("dogri","doi");
        h.put("dogrib","dgr");
        h.put("duala","dua");
        h.put("holandski","nl");
        h.put("dyula","dyu");
        h.put("dzongkha","dz");
        h.put("Istočni Frizijski","frs");
        h.put("efik","efi");
        h.put("Egipćanin","egy");
        h.put("ekajuk","eka");
        h.put("elamite","elx");
        h.put("engleski","en");
        h.put("erzya","myv");
        h.put("esperanto","eo");
        h.put("estonski","et");
        h.put("ovca","ee");
        h.put("ewondo","ewo");
        h.put("Fanti","fat");
        h.put("Farski","fo");
        h.put("fidžijski","fj");
        h.put("finski","fi");
        h.put("fon","fon");
        h.put("francuski","fr");
        h.put("furlanski","fur");
        h.put("Fula","ff");
        h.put("GA","gaa");
        h.put("galibi Carib","car");
        h.put("galicijski","gl");
        h.put("galski","gl");
        h.put("gayo","gay");
        h.put("gbaya","gba");
        h.put("ge'ez","gez");
        h.put("gruzijski","ka");
        h.put("njemački","de");
        h.put("nemački","de");
        h.put("gilbertski","gil");
        h.put("gondi","gon");
        h.put("Gorontalo","gor");
        h.put("gotika","got");
        h.put("Grebo","grb");
        h.put("grčki","el");
        h.put("tetumski","kl");
        h.put("Guaraní","gn");
        h.put("gudžarati","gu");
        h.put("gwich'in","gwi");
        h.put("Haida","hai");
        h.put("Haićanski kreolski","ht");
        h.put("Hausa","ha");
        h.put("havajski","haw");
        h.put("hebrejski","he");
        h.put("herero","hz");
        h.put("hiligaynon","hil");
        h.put("hindi","hi");
        h.put("hiri motu","ho");
        h.put("Hetita","hit");
        h.put("Hmong","hmn");
        h.put("mađarski","hu");
        h.put("hupa","hup");
        h.put("IBAN","iba");
        h.put("islandski","is");
        h.put("Ido","io");
        h.put("Igbo","ig");
        h.put("ilocano","ilo");
        h.put("Inari Sami","smn");
        h.put("indonezijski","id");
        h.put("inguški","inh");
        h.put("interlingva","ia");
        h.put("interlingve","ie");
        h.put("Inuktitut","iu");
        h.put("Inupiak","ik");
        h.put("irski","ga");
        h.put("talijanski","it");
        h.put("japanski","ja");
        h.put("javanski","jv");
        h.put("jingpho","kac");
        h.put("Judeo-arapski","jrb");
        h.put("Judeo-Persian","jpr");
        h.put("kabardian","kbd");
        h.put("kabyle","kab");
        h.put("kalmički","xal");
        h.put("Kamba","kam");
        h.put("kanadski","kn");
        h.put("kanuri","kr");
        h.put("kapampangan","pam");
        h.put("Karachay-balkar","krc");
        h.put("karakalpak","kaa");
        h.put("karelijanski","krl");
        h.put("Kašmirski","ks");
        h.put("kašupski","csb");
        h.put("kazaški","kk");
        h.put("Khasi","kha");
        h.put("Khmer","km");
        h.put("khotanese","kho");
        h.put("kikuyu","ki");
        h.put("Kimbundu","kmb");
        h.put("Kinyarwanda","rw");
        h.put("Kirundi","rn");
        h.put("komi-zyrian","kv");
        h.put("Kongo","kg");
        h.put("Konkani","kok");
        h.put("korejski","ko");
        h.put("kosraean","kos");
        h.put("kpelle","kpe");
        h.put("kumyk","kum");
        h.put("kurdski","ku");
        h.put("kurukh","kru");
        h.put("kutenai","kut");
        h.put("kwanyama","kj");
        h.put("kirgiski","ky");
        h.put("ladino","lad");
        h.put("lahnda","lah");
        h.put("Lamba","lam");
        h.put("Lao","lo");
        h.put("latinski","la");
        h.put("letonski","lv");
        h.put("Lenapa","del");
        h.put("lezgi","lez");
        h.put("limburgijski","li");
        h.put("lingala","ln");
        h.put("litvanski","lt");
        h.put("lojban","jbo");
        h.put("niska njemački","nds");
        h.put("lužičkosrpski","dsb");
        h.put("lozi","loz");
        h.put("Luba-Katanga","lu");
        h.put("luganda","lg");
        h.put("LUISENO","lui");
        h.put("Lule Sami","smj");
        h.put("Lunda","lun");
        h.put("Luo","luo");
        h.put("luksemburški","lb");
        h.put("Maasai","mas");
        h.put("makedonski","mk");
        h.put("madurese","mad");
        h.put("magahi","mag");
        h.put("maithili","mai");
        h.put("makasar","mak");
        h.put("malagasi","mg");
        h.put("malajski","ms");
        h.put("Malayalam","ml");
        h.put("malteški","mt");
        h.put("maltski","mt");
        h.put("Manchu","mnc");
        h.put("mandar","mdr");
        h.put("mandarin","zh");
        h.put("mandingo","man");
        h.put("manipuri","mni");
        h.put("manski","max");
        h.put("s ostrva Mana","gv");
        h.put("Maor","mi");
        h.put("mapudungun","arn");
        h.put("marati","mr");
        h.put("mari","chm");
        h.put("maršalski","mh");
        h.put("Marwari","mwr");
        h.put("Mende","men");
        h.put("Srednji dutch","dum");
        h.put("Srednji english","enm");
        h.put("Srednji francuski","frm");
        h.put("srednje visoki njemački","gmh");
        h.put("Srednji irish","mga");
        h.put("Srednji Perzijski","pal");
        h.put("mi'kmaq","mic");
        h.put("minangkabau","min");
        h.put("mirandese","mwl");
        h.put("Mizo","lus");
        h.put("Mohikanac","moh");
        h.put("Moksha","mdf");
        h.put("Mongo","lol");
        h.put("mongolski","mn");
        h.put("više","mos");
        h.put("nauruan","na");
        h.put("Navajo","nv");
        h.put("ndonga","ng");
        h.put("napolitanski","nap");
        h.put("nepalski","ne");
        h.put("newari","new");
        h.put("nias","nia");
        h.put("niuean","niu");
        h.put("n'ko","nqo");
        h.put("nogai","nog");
        h.put("sjeverno Frizijski","frr");
        h.put("Sjeverna endebelski","nd");
        h.put("južni sami","se");
        h.put("sjeverni sotho","nso");
        h.put("norveški","no");
        h.put("književni norveški","nb");
        h.put("novonorveški","nn");
        h.put("nyamwezi","nym");
        h.put("nyankole","nyn");
        h.put("nyoro","nyo");
        h.put("nzima","nzi");
        h.put("oksitanski","oc");
        h.put("Chippewa","oj");
        h.put("Staroslavenski","cu");
        h.put("Stari engleski","ang");
        h.put("stari francuski","fro");
        h.put("Stara visoki njemački","goh");
        h.put("Stari Irski","sga");
        h.put("Stara Javanski","kaw");
        h.put("platdojč","non");
        h.put("Stara Provençal","pro");
        h.put("staroperzijski","peo");
        h.put("Oriya","or");
        h.put("Oromo","om");
        h.put("Osage","osa");
        h.put("ossetian","os");
        h.put("turski","ota");
        h.put("pahouin","fan");
        h.put("palauan","pau");
        h.put("pali","pi");
        h.put("Pangasinan","pag");
        h.put("papiamentu","pap");
        h.put("paštu","ps");
        h.put("perzijski","fa");
        h.put("feničanski","phn");
        h.put("pohnpeian","pon");
        h.put("poljski","pl");
        h.put("portugalski","pt");
        h.put("pendžabski","pa");
        h.put("kečua","qu");
        h.put("radžastanska","raj");
        h.put("Rapa Nui","rap");
        h.put("rarotongan","rar");
        h.put("Romani","rom");
        h.put("rumunski","ro");
        h.put("rumunjski","ro");
        h.put("retoromanski","rm");
        h.put("ruski","ru");
        h.put("Samarijanac aramejski","sam");
        h.put("Samoanski","sm");
        h.put("sandawe","sad");
        h.put("Sango","sg");
        h.put("sanskrit","sa");
        h.put("santali","sat");
        h.put("sardinijski","sc");
        h.put("sasak","sas");
        h.put("škotski","sco");
        h.put("Škotski galski","gd");
        h.put("selkup","sel");
        h.put("srpski","sr");
        h.put("srpsko-hrvatski","sh");
        h.put("serer","srr");
        h.put("shan","shn");
        h.put("Shona","sn");
        h.put("Sichuan yi","ii");
        h.put("Sicilijanac","scn");
        h.put("Sidamo","sid");
        h.put("sindhi","sd");
        h.put("sinhaleški","sin");
        h.put("cejlonski","si");
        h.put("Siska","tog");
        h.put("skolt Sami","sms");
        h.put("služavka","den");
        h.put("slovački","sk");
        h.put("slovenski","sl");
        h.put("sogdian","sog");
        h.put("somalijski","so");
        h.put("soninke","snk");
        h.put("Sotho","st");
        h.put("Južni Altai","alt");
        h.put("Južni endebelski","nr");
        h.put("Južni Sami","sma");
        h.put("španski","es");
        h.put("sranan tongo","srn");
        h.put("sukuma","suk");
        h.put("sumerski","sux");
        h.put("Sudanski","su");
        h.put("susu","sus");
        h.put("svahili","sw");
        h.put("Swati","ss");
        h.put("švedski","sv");
        h.put("sirijski","syr");
        h.put("tagalski","tl");
        h.put("Tahitian","ty");
        h.put("Tadžik","tg");
        h.put("tamashek","tmh");
        h.put("tamilski","ta");
        h.put("Tatar","tt");
        h.put("telugu","te");
        h.put("Predložak: fil","fil");
        h.put("Predložak: mis","mis");
        h.put("Predložak: MO","mo");
        h.put("Predložak: tlh","tlh");
        h.put("Predložak: Zbl","zbl");
        h.put("Predložak: zxx","zxx");
        h.put("tereno","ter");
        h.put("tetum","tet");
        h.put("tajski","th");
        h.put("tatarski","crh");
        h.put("Tibetanski","bo");
        h.put("Tigre","tig");
        h.put("Tigrinya","ti");
        h.put("timne","tem");
        h.put("tivi","tiv");
        h.put("Tlingit","tli");
        h.put("tok pisin","tpi");
        h.put("tokelauan","tkl");
        h.put("Tongan","to");
        h.put("translingual","mul");
        h.put("tshiluba","lua");
        h.put("Tsimshian","tsi");
        h.put("Tsonga","ts");
        h.put("cvana","tn");
        h.put("tumbuka","tum");
        h.put("turski","tr");
        h.put("turkmenski","tk");
        h.put("tuvaluan","tvl");
        h.put("tuvan","tyv");
        h.put("Twi","tw");
        h.put("udmurt","udm");
        h.put("ugaritski","uga");
        h.put("ukrajinski","uk");
        h.put("Umbundu","umb");
        h.put("neodređen","und");
        h.put("gornjolužički","hsb");
        h.put("urdu","ur");
        h.put("ujgurski","ug");
        h.put("uzbečki","uz");
        h.put("Vai","vai");
        h.put("Venda","ve");
        h.put("vijetnamski","vi");
        h.put("volapük","vo");
        h.put("votic","vot");
        h.put("walamo","wal");
        h.put("valonski","wa");
        h.put("waray-waray","war");
        h.put("washo","was");
        h.put("velški","cy");
        h.put("zapad Frizijski","fy");
        h.put("Wolof","wo");
        h.put("Xhosa","xh");
        h.put("Jakut","sah");
        h.put("Yao","yao");
        h.put("yapese","yap");
        h.put("jidiš","yi");
        h.put("Joruba","yo");
        h.put("Zapotec","zap");
        h.put("Zazaki","zza");
        h.put("zenaga","zen");
        h.put("Zhuang","za");
        h.put("zulu","zu");
        h.put("Zuñi","zun");
	}

	public static String threeLettersCode(String s) { 
		return threeLettersCode(h, s);
	}
}
