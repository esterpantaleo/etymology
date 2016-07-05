package org.getalp.dbnary.nor;

import org.getalp.dbnary.LangTools;

import java.util.HashMap;

/**
 * @author roques
 *
 */
public class NorskLangToCode extends LangTools {
	static HashMap<String,String> h = new HashMap<>();
	static {
		h.put("abkhasiske","ab");
		h.put("acehnese","ace");
		h.put("acholi","ach");
		h.put("adangme","ada");
		h.put("adyghe","ady");
		h.put("afar","aa");
		h.put("afrihili","afh");
		h.put("afrikaans","af");
		h.put("ainu","ain");
		h.put("akan","ak");
		h.put("akkadisk","akk");
		h.put("albansk","sq");
		h.put("alemannic tysk","gsw");
		h.put("aleutisk","ale");
		h.put("amharisk","am");
		h.put("ancient greek","grc");
		h.put("angika","anp");
		h.put("arabic","ar");
		h.put("aragonese","an");
		h.put("arameisk","arc");
		h.put("arapaho","arp");
		h.put("arawak","arw");
		h.put("armenian","hy");
		h.put("aromansk","rup");
		h.put("assamesisk","as");
		h.put("asturianske","ast");
		h.put("avar","av");
		h.put("avestisk","ae");
		h.put("awadhi","awa");
		h.put("aymara","ay");
		h.put("azeri","az");
		h.put("balinesisk","ban");
		h.put("baluchi","bal");
		h.put("bambara","bm");
		h.put("basaa","bas");
		h.put("basjkirsk","ba");
		h.put("basque","eu");
		h.put("beja","bej");
		h.put("belarusian","be");
		h.put("bemba","bem");
		h.put("bengali","bn");
		h.put("bhojpuri","bho");
		h.put("bikol","bik");
		h.put("bini","bin");
		h.put("bislama","bi");
		h.put("blackfoot","bla");
		h.put("blin","byn");
		h.put("bosnisk","bs");
		h.put("braj","bra");
		h.put("breton","br");
		h.put("buginesisk","bug");
		h.put("bulgarian","bg");
		h.put("burmesisk","my");
		h.put("buryat","bua");
		h.put("caddo","cad");
		h.put("catalan","ca");
		h.put("cebuano","ceb");
		h.put("chagatai","chg");
		h.put("chamorro","ch");
		h.put("tsjetsjenske","ce");
		h.put("cherokee","chr");
		h.put("cheyenne","chy");
		h.put("chibcha","chb");
		h.put("chichewa","ny");
		h.put("chinook","chn");
		h.put("chipewyan","chp");
		h.put("choctaw","cho");
		h.put("chuukese","chk");
		h.put("tsjuvansk","cv");
		h.put("classical newari","nwc");
		h.put("klassisk syrisk","syc");
		h.put("koptisk","cop");
		h.put("cornish","kw");
		h.put("korsikanske","co");
		h.put("cree","cr");
		h.put("creek","mus");
		h.put("krim tatar","crh");
		h.put("kroatisk","hr");
		h.put("czech","cs");
		h.put("dakota","dak");
		h.put("danish","da");
		h.put("dargwa","dar");
		h.put("dhivehi","dv");
		h.put("dinka","din");
		h.put("dogri","doi");
		h.put("dogrib","dgr");
		h.put("duala","dua");
		h.put("dutch","nl");
		h.put("dyula","dyu");
		h.put("dzongkha","dz");
		h.put("eastern frisian","frs");
		h.put("efik","efi");
		h.put("egyptisk","egy");
		h.put("ekajuk","eka");
		h.put("elamittisk","elx");
		h.put("engelsk","en");
		h.put("erzya","myv");
		h.put("esperanto","eo");
		h.put("estonian","et");
		h.put("ewe","ee");
		h.put("ewondo","ewo");
		h.put("fanti","fat");
		h.put("færøysk","fo");
		h.put("fijian","fj");
		h.put("finnish","fi");
		h.put("fon","fon");
		h.put("fransk","fr");
		h.put("friulian","fur");
		h.put("fula","ff");
		h.put("ga","gaa");
		h.put("galibi carib","car");
		h.put("galisisk","gl");
		h.put("gayo","gay");
		h.put("gbaya","gba");
		h.put("ge'ez","gez");
		h.put("georgian","ka");
		h.put("tysk","de");
		h.put("kiribatisk","gil");
		h.put("gondi","gon");
		h.put("gorontalo","gor");
		h.put("gothic","got");
		h.put("grebo","grb");
		h.put("gresk","el");
		h.put("grønlandsk","kl");
		h.put("guarani","gn");
		h.put("gujarati","gu");
		h.put("gwichin","gwi");
		h.put("haida","hai");
		h.put("haitian creole","ht");
		h.put("hausa","ha");
		h.put("hawaiian","haw");
		h.put("hebrew","he");
		h.put("herero","hz");
		h.put("hiligaynon","hil");
		h.put("hindi","hi");
		h.put("hiri motu","ho");
		h.put("hettittisk","hit");
		h.put("hmong","hmn");
		h.put("hungarian","hu");
		h.put("hupa","hup");
		h.put("iban","iba");
		h.put("icelandic","is");
		h.put("jeg gjør","io");
		h.put("ibo","ig");
		h.put("ilocano","ilo");
		h.put("enaresamisk","smn");
		h.put("indonesisk","id");
		h.put("ingusjisk","inh");
		h.put("interlingua","ia");
		h.put("interlingua","ie");
		h.put("inuktitut","iu");
		h.put("inupiak","ik");
		h.put("irish","ga");
		h.put("italiensk","it");
		h.put("japansk","ja");
		h.put("javanesisk","jv");
		h.put("jingpho","kac");
		h.put("jødisk-arabisk","jrb");
		h.put("judeo-persisk","jpr");
		h.put("kabardisk","kbd");
		h.put("kabyle","kab");
		h.put("kalmyk","xal");
		h.put("kamba","kam");
		h.put("kannada","kn");
		h.put("kanuri","kr");
		h.put("kapampangan","pam");
		h.put("karachay-balkar","krc");
		h.put("karakalpak","kaa");
		h.put("karelske","krl");
		h.put("kashmiri","ks");
		h.put("kashubian","csb");
		h.put("kazakh","kk");
		h.put("khasi","kha");
		h.put("khmer","km");
		h.put("khotanese","kho");
		h.put("kikuyu","ki");
		h.put("kimbundu","kmb");
		h.put("kinyarwanda","rw");
		h.put("kirundi","rn");
		h.put("komi-zyrian","kv");
		h.put("kongo","kg");
		h.put("konkani","kok");
		h.put("korean","ko");
		h.put("kosraean","kos");
		h.put("kpelle","kpe");
		h.put("kumyk","kum");
		h.put("kurdisk","ku");
		h.put("kurukh","kru");
		h.put("kutenai","kut");
		h.put("kwanyama","kj");
		h.put("kirgisistan","ky");
		h.put("ladino","lad");
		h.put("lahnda","lah");
		h.put("lamba","lam");
		h.put("lao","lo");
		h.put("latin","la");
		h.put("latvisk","lv");
		h.put("lenape","del");
		h.put("lezgi","lez");
		h.put("limburgisk","li");
		h.put("lingala","ln");
		h.put("litauisk","lt");
		h.put("lojban","jbo");
		h.put("lavtysk","nds");
		h.put("lavsorbisk","dsb");
		h.put("lozi","loz");
		h.put("luba-katanga","lu");
		h.put("luganda","lg");
		h.put("luiseno","lui");
		h.put("lulesamisk","smj");
		h.put("lunda","lun");
		h.put("luo","luo");
		h.put("luxembourgish","lb");
		h.put("maasai","mas");
		h.put("makedonsk","mk");
		h.put("maduresisk","mad");
		h.put("magahi","mag");
		h.put("maithili","mai");
		h.put("makasar","mak");
		h.put("gassisk","mg");
		h.put("malay","ms");
		h.put("malayalam","ml");
		h.put("maltese","mt");
		h.put("manchu","mnc");
		h.put("mandar","mdr");
		h.put("mandarin","zh");
		h.put("mandingo","man");
		h.put("manipuri","mni");
		h.put("manx","gv");
		h.put("maori","mi");
		h.put("mapudungun","arn");
		h.put("marathi","mr");
		h.put("mari","chm");
		h.put("marshallesisk","mh");
		h.put("marwari","mwr");
		h.put("mende","men");
		h.put("middle dutch","dum");
		h.put("middle english","enm");
		h.put("middle french","frm");
		h.put("tysk, medium høy","gmh");
		h.put("middle irish","mga");
		h.put("middle persian","pal");
		h.put("mi'kmaq","mic");
		h.put("minangkabau","min");
		h.put("mirandesisk","mwl");
		h.put("mizo","lus");
		h.put("mohawk","moh");
		h.put("moksha","mdf");
		h.put("mongo","lol");
		h.put("mongolsk","mn");
		h.put("mer","mos");
		h.put("nauruan","na");
		h.put("navajo","nv");
		h.put("ndonga","ng");
		h.put("napolitanske","nap");
		h.put("nepali","ne");
		h.put("newari","new");
		h.put("nias","nia");
		h.put("niuean","niu");
		h.put("n'ko","nqo");
		h.put("nogai","nog");
		h.put("nordfrisisk","frr");
		h.put("northern ndebele","nd");
		h.put("nordsamisk","se");
		h.put("nord-sotho","nso");
		h.put("norsk","no");
		h.put("norsk bokm","nb");
		h.put("norsk nynorsk","nn");
		h.put("nyamwezi","nym");
		h.put("nyankole","nyn");
		h.put("nyoro","nyo");
		h.put("nzima","nzi");
		h.put("oksitansk","oc");
		h.put("ojibwe","oj");
		h.put("gamle kirkeslavisk","cu");
		h.put("old english","ang");
		h.put("old french","fro");
		h.put("tysk, old høy","goh");
		h.put("gammelirsk","sga");
		h.put("gammel javanesisk","kaw");
		h.put("norse, gamle","non");
		h.put("provencal, gamle","pro");
		h.put("persisk, gamle","peo");
		h.put("oriya","or");
		h.put("oromo","om");
		h.put("osage","osa");
		h.put("ossetiske","os");
		h.put("ottoman turkish","ota");
		h.put("pahouin","fan");
		h.put("palauan","pau");
		h.put("pali","pi");
		h.put("pangasinan","pag");
		h.put("papiamentu","pap");
		h.put("pashto","ps");
		h.put("persian","fa");
		h.put("fønikiske","phn");
		h.put("ponapisk","pon");
		h.put("polsk","pl");
		h.put("portugisisk","pt");
		h.put("punjabi","pa");
		h.put("quechua","qu");
		h.put("rajasthani","raj");
		h.put("rapa nui","rap");
		h.put("rarotongan","rar");
		h.put("romani","rom");
		h.put("rumensk","ro");
		h.put("romansk","rm");
		h.put("russisk","ru");
		h.put("samaritan arameisk","sam");
		h.put("samoan","sm");
		h.put("sandawe","sad");
		h.put("sango","sg");
		h.put("sanskrit","sa");
		h.put("santali","sat");
		h.put("sardinsk","sc");
		h.put("sasak","sas");
		h.put("scots","sco");
		h.put("skotsk gælisk","gd");
		h.put("selkup","sel");
		h.put("serbisk","sr");
		h.put("serbokroatisk","sh");
		h.put("serer","srr");
		h.put("shan","shn");
		h.put("shona","sn");
		h.put("sichuan yi","ii");
		h.put("sicilian","scn");
		h.put("sidamo","sid");
		h.put("sindhi","sd");
		h.put("singalesiske","si");
		h.put("siska","tog");
		h.put("skoltesamisk","sms");
		h.put("slavey","den");
		h.put("slovak","sk");
		h.put("slovensk","sl");
		h.put("sogdian","sog");
		h.put("somali","so");
		h.put("soninke","snk");
		h.put("sotho","st");
		h.put("søraltaisk","alt");
		h.put("southern ndebele","nr");
		h.put("sørsamisk","sma");
		h.put("spansk","es");
		h.put("sranan tongo","srn");
		h.put("sukuma","suk");
		h.put("sumerisk","sux");
		h.put("sundanesisk","su");
		h.put("susu","sus");
		h.put("swahili","sw");
		h.put("swati","ss");
		h.put("swedish","sv");
		h.put("syrisk","syr");
		h.put("tagalog","tl");
		h.put("tahitisk","ty");
		h.put("tajik","tg");
		h.put("tamashek","tmh");
		h.put("tamil","ta");
		h.put("tatar","tt");
		h.put("telugu","te");
		h.put("mal: fil","fil");
		h.put("mal: mis","mis");
		h.put("mal: mo","mo");
		h.put("mal: tlh","tlh");
		h.put("mal: zbl","zbl");
		h.put("mal: sonenavn zxx","zxx");
		h.put("tereno","ter");
		h.put("tetum","tet");
		h.put("thai","th");
		h.put("tibetansk","bo");
		h.put("tigre","tig");
		h.put("tigrinja","ti");
		h.put("timne","tem");
		h.put("tivi","tiv");
		h.put("tlingit","tli");
		h.put("tok pisin","tpi");
		h.put("tokelauan","tkl");
		h.put("tonganske","to");
		h.put("translingual","mul");
		h.put("tshiluba","lua");
		h.put("tsimshian","tsi");
		h.put("tsonga","ts");
		h.put("setswana","tn");
		h.put("tumbuka","tum");
		h.put("turkish","tr");
		h.put("turkmensk","tk");
		h.put("tuvalsk","tvl");
		h.put("tuvan","tyv");
		h.put("twi","tw");
		h.put("udmurt","udm");
		h.put("ugaritic","uga");
		h.put("ukrainsk","uk");
		h.put("umbundu","umb");
		h.put("ubestemt","und");
		h.put("Øvresorbiskname","hsb");
		h.put("urdu","ur");
		h.put("uyghur","ug");
		h.put("usbekisk","uz");
		h.put("vai","vai");
		h.put("venda","ve");
		h.put("vietnamesisk","vi");
		h.put("volapük","vo");
		h.put("votisk","vot");
		h.put("walamo","wal");
		h.put("walloon","wa");
		h.put("waray-waray","war");
		h.put("washo","was");
		h.put("welsh","cy");
		h.put("west frisian","fy");
		h.put("wolof","wo");
		h.put("xhosa","xh");
		h.put("yakut","sah");
		h.put("yao","yao");
		h.put("yapese","yap");
		h.put("yiddish","yi");
		h.put("yoruba","yo");
		h.put("zapotec","zap");
		h.put("zazaki","zza");
		h.put("zenaga","zen");
		h.put("zhuang","za");
		h.put("zulu","zu");
		h.put("zuni","zun");

	}
	
	public static String threeLettersCode(String s) {
		return threeLettersCode(h, s);
	}
}
