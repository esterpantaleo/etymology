/**
 * 
 */
package org.getalp.dbnary.eng;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.getalp.dbnary.LangTools;
import org.getalp.iso639.ISO639_3;

/**
 * @author Mariam, pantaleo
 *
 */
//from this list I have removed those codes that are present in data3 (all of them are 3 letters codes)
public class EnglishLangToCode extends LangTools {
    static HashMap<String,String> h = new HashMap<String,String>();
    static {
        add("Abkhaz","ab");
	add("Afar","aa");
	add("Afrikaans","af");
	add("Akan","ak");
	add("Albanian","sq");
	add("Amharic","am");
	add("Arabic","ar");
	add("Aragonese","an");
        add("Armenian","hy");
	add("Assamese","as");
	add("Avar","av");
	add("Avestan","ae");
		add("Aymara","ay");
		add("Azeri","az");
		add("Bambara","bm");
		add("Bashkir","ba");
		add("Basque","eu");
		add("Belarusian","be");
		add("Bengali","bn");
		add("Bikol","bik");//bikol is bcl in data3
		add("Bislama","bi");
		add("Bosnian","bs");
		add("Breton","br");
		add("Bulgarian","bg");
		add("Burmese","my");
		add("Catalan","ca");
		add("Chamorro","ch");
		add("Chechen","ce");
		add("Chichewa","ny");
		add("Chuvash","cv");
		add("Cornish","kw");
		add("Corsican","co");
		add("Cree","cr");
		add("Croatian","hr");
		add("Czech","cs");
		add("Danish","da");
		add("Dhivehi","dv");
		add("Dutch","nl");
		add("Dzongkha","dz");
		add("Eastern Frisian","frs");//this is stq in data3
		add("English","en");
		add("Esperanto","eo");
		add("Estonian","et");
		add("Ewe","ee");
		add("Fanti","fat");//this is not in data3
		add("Faroese","fo");
		add("Fijian","fj");
		add("Finnish","fi");
		add("French","fr");
		add("Fula","ff");
		add("Galician","gl");
		add("Georgian","ka");
		add("German","de");
		add("Greek","el");
		add("Greenlandic","kl");
		add("Guaraní","gn");
		add("Gujarati","gu");
		add("Haitian Creole","ht");
		add("Hausa","ha");
		add("Hebrew","he");
		add("Herero","hz");
		add("Hindi","hi");
		add("Hiri Motu","ho");
		add("Hmong","hmn");//this is not in data3
		add("Hungarian","hu");
		add("Icelandic","is");
		add("Ido","io");
		add("Igbo","ig");
		add("Indonesian","id");
		add("Interlingua","ia");
		add("Interlingue","ie");
		add("Inuktitut","iu");
		add("Inupiak","ik");
		add("Irish","ga");
		add("Italian","it");
		add("Japanese","ja");
		add("Javanese","jv");
		add("Kannada","kn");
		add("Kanuri","kr");
		add("Kashmiri","ks");
		add("Kazakh","kk");
		add("Khmer","km");
		add("Kikuyu","ki");
		add("Kinyarwanda","rw");
		add("Kirundi","rn");
		add("Komi-Zyrian","kv");
		add("Kongo","kg");
		add("Korean","ko");
		add("Kurdish","ku");
		add("Kwanyama","kj");
		add("Kyrgyz","ky");
		add("Lao","lo");
		add("Latin","la");
		add("Latvian","lv");
		add("Lenape","del");//this is not in data3
		add("Limburgish","li");
		add("Lingala","ln");
		add("Lithuanian","lt");
		add("Luba-Katanga","lu");
		add("Luganda","lg");
		add("Luxembourgish","lb");
		add("Macedonian","mk");
		add("Malagasy","mg");
		add("Malay","ms");
		add("Malayalam","ml");
		add("Maltese","mt");
		add("Mandarin","zh");
		add("Manx","gv");
		add("Maori","mi");
		add("Marathi","mr");
		add("Marshallese","mh");
		add("Mongolian","mn");
		add("Nauruan","na");
		add("Navajo","nv");
		add("Ndonga","ng");
		add("Nepali","ne");
		add("Northern Ndebele","nd");
		add("Northern Sami","se");
		add("Norwegian","no");
		add("Norwegian Bokmål","nb");
		add("Norwegian Nynorsk","nn");
		add("Occitan","oc");
		add("Ojibwe","oj");
		add("Old Church Slavonic","cu");
		add("Oriya","or");
		add("Oromo","om");
		add("Ossetian","os");
		add("Pahouin","fan");//this is Fan (Guinea) in data3
		add("Pali","pi");
		add("Pashto","ps");
		add("Persian","fa");
		add("Polish","pl");
		add("Portuguese","pt");
		add("Punjabi","pa");
		add("Quechua","qu");
		add("Romanian","ro");
		add("Romansch","rm");
		add("Russian","ru");
		add("Samoan","sm");
		add("Sango","sg");
		add("Sanskrit","sa");
		add("Sardinian","sc");
		add("Scottish Gaelic","gd");
		add("Serbian","sr");
		add("Serbo-Croatian","sh");
		add("Shona","sn");
		add("Sichuan Yi","ii");
		add("Sindhi","sd");
		add("Sinhalese","si");
		add("Slovak","sk");
		add("Slovene","sl");
		add("Somali","so");
		add("Sotho","st");
		add("Southern Ndebele","nr");
		add("Spanish","es");
		add("Sundanese","su");
		add("Swahili","sw");
		add("Swati","ss");
		add("Swedish","sv");
		add("Tagalog","tl");
		add("Tahitian","ty");
		add("Tajik","tg");
		add("Tamil","ta");
		add("Tatar","tt");
		add("Telugu","te");
		add("Template:fil","fil");//?
		add("Template:mis","mis");//?
		add("Template:mo","mo");//?
		add("Template:tlh","tlh");//?
		add("Template:zbl","zbl");//?
		add("Template:zxx","zxx");//?
		add("Thai","th");
		add("Tibetan","bo");
		add("Tigrinya","ti");
		add("Tongan","to");
		add("Tsonga","ts");
		add("Tswana","tn");
		add("Turkish","tr");
		add("Turkmen","tk");
		add("Twi","tw");
		add("Ukrainian","uk");
		add("Urdu","ur");
		add("Uyghur","ug");
		add("Uzbek","uz");
		add("Venda","ve");
		add("Vietnamese","vi");
		add("Volapük","vo");
		add("Walloon","wa");
		add("Welsh","cy");
		add("West Frisian","fy");
		add("Wolof","wo");
		add("Xhosa","xh");
		add("Yiddish","yi");
		add("Yoruba","yo");
		add("Zhuang","za");
	add("Zulu","zu");

        // uncommon languages
        add("Abhiri", "abh-prk");
        add("Abhiri Prakrit", "abh-prk");
        add("Acadian French", "fr-aca");
        add("Addu Dhivehi", "add-dv");
        add("Addu Divehi", "add-dv");
        add("Addu Bas", "add-dv");
        add("Aeolic Greek", "el-aeo");
        add("Lesbic Greek", "el-aeo");
        add("Lesbian Greek", "el-aeo");
        add("Aeolian Greek", "el-aeo");
        add("American English", "en-US");
        add("Amoy", "nan-amo");
        add("Xiamenese", "nan-amo");
        add("Arcadian Greek", "el-arc");
        add("Arcadocypriot Greek", "el-arp");
        add("Attic Greek", "el-att");
        add("Austrian German", "de-AT");
        add("Avanti", "prk-avt");
        add("Avanti Prakrit", "prk-avt");
        add("Bahliki", "bhl-prk");
        add("Bahliki Prakrit", "bhl-prk");
        add("Bombay Hindi", "hi-mum");
        add("Mumbai Hindi", "hi-mum");
        add("Bambai Hindi", "hi-mum");
        add("British English", "en-GB");
        add("Byzantine Greek", "gkm Medieval Greek");
        add("Medieval Greek", "gkm Medieval Greek");
        add("Cajun French", "frc");
        add("Louisiana French", "frc");
        add("Canadian French", "fr-CA");
        add("Candali", "cnd-prk");
        add("Candali Prakrit", "cnd-prk");
        add("Classical Tagalog", "tl-cls");
        add("Cretan Greek", "el-crt");
        add("Cypriotic Greek", "el-cyp");
        add("Daksinatya", "dks-prk");
        add("Daksinatya Prakrit", "dks-prk");
        add("Doric Greek", "el-dor");
        add("Dramili", "drm-prk");
        add("Dramili Prakrit", "drm-prk");
        add("Early Scots", "sco-osc");
        add("Old Scots", "sco-osc");
        add("O.Sc.", "sco-osc");
        add("Ecclesiastical Latin", "la-ecc");
        add("Church Latin", "la-ecc");
        add("EL.", "la-ecc");
        add("Elean Greek", "el-ela");
        add("Epic Greek", "el-epc");
        add("Griko", "el-grk");
        add("Grico", "el-grk");
        add("Hainanese", "nan-hai");
        add("Helu", "elu-prk");
        add("Hela", "elu-prk");
        add("Elu Prakrit", "elu-prk");
        add("Helu Prakrit", "elu-prk");
        add("Hela Prakrit", "elu-prk");
        add("Hokkien", "nan-hok");
        add("Homeric Greek", "el-hmr");
        add("Huvadhu Dhivehi", "hvd-dv");
        add("Huvadhu Divehi", "hvd-dv");
        add("Huvadhu Bas", "hvd-dv");
        add("Insular Scots", "sco-ins");
        add("Ins.Sc.", "sco-ins");
        add("Ionic Greek", "el-ion");
        add("Jewish Aramaic", "sem-jar");
        add("Kathiyawadi", "gu-kat");
        add("Kathiyawadi Gujarati", "gu-kat");
        add("Kathiawadi", "gu-kat");
        add("Koine Greek", "grc-koi Koine");
        add("Kromanti", "alv-kro");
        add("Late Latin", "la-lat");
        add("LL", "la-lat");
        add("LL.", "la-lat");
        add("Lunfardo", "es-lun Lunfardo");
        add("Medieval Latin", "la-med");
        add("ML", "la-med");
        add("ML.", "la-med");
        add("Medieval Sinhalese", "si-med");
        add("Medieval Sinhala", "si-med");
        add("Mercian Old English", "ang-mer");
        add("Middle Bengali", "bn-mid");
        add("Middle Gujarati", "gu-mid");
        add("Middle Iranian", "ira-mid ");
        add("MIr.", "ira-mid ");
        add("Middle Kannada", "kn-mid");
        add("Middle Konkani", "kok-mid");
        add("Medieval Konkani", "kok-mid");
        add("Middle Oriya", "or-mid");
        add("Middle Scots", "sco-smi");
        add("Mid.Sc.", "sco-smi");
        add("Middle Tamil", "ta-mid");
        add("Modern Greek", "el-GR ell");
        add("Modern Israeli Hebrew", "he-IL");
        add("Mulaku Dhivehi", "mlk-dv");
        add("Mulaku Divehi", "mlk-dv");
        add("Mulaku Bas", "mlk-dv");
        add("New Latin", "la-new");
        add("Modern Latin", "la-new");
        add("NL.", "la-new");
        add("Northern Scots", "sco-nor");
        add("Nor.Sc.", "sco-nor");
        add("Northumbrian Old English", "ang-nor");
        add("Odri", "odr-prk");
        add("Odri Prakrit", "odr-prk");
        add("Old Bengali", "bn-old");
        add("Old Gujarati", "gu-old");
        add("Old Hindi", "hi-old");
        add("Old Iranian", "ira-old");
        add("OIr.", "ira-old");
        add("Old Kannada", "kn-old");
        add("Old Konkani", "kok-old");
        add("Early Konkani", "kok-old");
        add("Old Northern French", "fro-nor");
        add("Old Norman", "fro-nor");
        add("Old Norman French", "fro-nor");
        add("ONF", "fro-nor");
        add("Old Oriya", "or-old");
        add("Old Picard", "fro-pic");
        add("Old Punjabi", "pa-old");
        add("Old Tagalog", "tl-old");
        add("Old Xiang", "hsn-old");
        add("Lou-Shao", "hsn-old");
        add("Opuntian Locrian", "loc-opu");
        add("Ozolian Locrian", "loc-ozo");
        add("Paisaci", "psc-prk");
        add("Paisaci Prakrit", "psc-prk");
        add("Pamphylian Greek", "el-pam");
        add("Paphian Greek", "el-pap");
        add("Philippine Hokkien", "nan-phl");
        add("Pinghua", "pinhua");
        add("Pracya", "prc-prk");
        add("Pracya Prakrit", "prc-prk");
        add("Pre-Greek", "qfa-sub-grc");
        add("Pre-Greek", "pregrc");
        add("pre-Roman (Balkans)", "und-bal");
        add("pre-Roman (Iberia)", "und-ibe");
        add("Proto-Baltic", "bat-pro");
        add("Proto-Canaanite", "sem-can-pro");
        add("Proto-Finno-Permic", "fiu-fpr-pro");
        add("Proto-Finno-Ugric", "fiu-pro");
        add("Provençal", "prv");
        add("Renaissance Latin", "la-ren");
        add("RL.", "la-ren");
        add("Sabari", "sbr-prk");
        add("Sabari Prakrit", "sbr-prk");
        add("Shanghainese", "wuu-sha");
        add("Sha.", "wuu-sha");
        add("Southern Scots", "sco-sou");
        add("Borders Scots", "sco-sou");
        add("Sou.Sc.", "sco-sou");
        add("Suevic", "gem-sue");
        add("Suebian", "gem-sue");
        add("Taishanese", "yue-tai");
        add("Teochew", "nan-teo");
        add("Thessalian Greek", "el-ths");
        add("Transalpine Gaulish", "xtg");
        add("Ulster Scots", "sco-uls");
        add("Uls.Sc.", "sco-uls");
        add("Viennese German", "de-AT-vie");
        add("VG.", "de-AT-vie");
        add("Vulgar Latin", "la-vul");
        add("VL.", "la-vul");
        add("Wuhua Chinese", "hak-wuh");

	//add lines from file data3.txt in resources folder
        InputStream fis = null;
        try {
            fis = EnglishLangToCode.class.getResourceAsStream("data3.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            String s = br.readLine();
            while (s != null) {
                String [] line = s.split("\t");
                if (line.length >= 2) {
                    String code = line[0];
                    for (int i = 1; i < line.length; i++) {
                        add(line[i], code);
                    }
               } else {
                    // System.err.println("Unrecognized line:" + s);
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

	//get codes from iso-639-3.tab
        for (ISO639_3.Lang l : ISO639_3.sharedInstance.knownLanguages()) {
            add(l.getEn(), l.getId());
        }
    }
	
    public static String threeLettersCode(String s) {
	return threeLettersCode(h, s);
    }

    private static void add(String n, String c) {
        h.put(n.toLowerCase(), c);
    }
}
