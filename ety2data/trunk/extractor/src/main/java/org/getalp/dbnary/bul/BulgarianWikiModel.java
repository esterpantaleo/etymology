package org.getalp.dbnary.bul;

import info.bliki.wiki.filter.WikipediaParser;

import org.getalp.dbnary.DbnaryWikiModel;
import org.getalp.dbnary.IWiktionaryDataHandler;
import org.getalp.dbnary.WiktionaryIndex;
import org.getalp.dbnary.wiki.WikiPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BulgarianWikiModel extends DbnaryWikiModel {

	private Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static Set<String> bulgarianPOS = new TreeSet<String>();
    protected final static HashMap<String, String> nymMarkerToNymName;

    protected final static String translationExpression = "\\s?\\*?\\s?.*\\s?:\\s*.*";
    protected final static Pattern translationPattern = Pattern.compile(BulgarianWikiModel.translationExpression);
    protected final static String glossExpression = "(\\]|\\})(\\]|\\})[^\\]\\[\\}\\{\\:\\n]*((\\[|\\{)(\\[|\\{)|$)";
    static final Pattern glossPattern = Pattern.compile(glossExpression);
    protected final static String translationLangExpression = "\\s*\\*\\s*[^\\:]*";
    protected final static Pattern translationLangPattern = Pattern.compile(BulgarianWikiModel.translationLangExpression);
    //protected final static String translationBodyExpression = "(\\[\\[[^\\]]+\\]\\]\\s?\\(?[^\\)\\[\\,]*\\)?\\)?)";
    //protected final static String translationBodyExpression = "(\\[\\[[^\\]]+\\]\\]\\s?\\(?[^\\)\\[]+\\)?;?)";
    protected final static String translationBodyExpression = "([^:\\*]*(\\[|\\{)*)$";
    protected final static Pattern translationBodyPattern = Pattern.compile(BulgarianWikiModel.translationBodyExpression);

    static {


        bulgarianPOS.add("Съществително нарицателно име"); // Common Noun
        bulgarianPOS.add("Съществително собствено име"); // Proper Noun
        bulgarianPOS.add("Прилагателно име"); // Adjective
        bulgarianPOS.add("Глагол"); // Verb
        bulgarianPOS.add("Наречие"); //  Adverb
        bulgarianPOS.add("Частица"); // Particle
        bulgarianPOS.add("Числително име"); //Ordinal
        bulgarianPOS.add("Предлог"); // Preposition
        bulgarianPOS.add("междуметие"); // Interjection
        bulgarianPOS.add("съюз"); // Conjunction
    }

    static {

        nymMarkerToNymName = new HashMap<String, String>(20);
        nymMarkerToNymName.put("Синоними", "syn"); //
        nymMarkerToNymName.put("Антоними", "ant"); //

        nymMarkerToNymName.put("Гипонимы", "hypo");
        nymMarkerToNymName.put("Хипоними", "hyper");
        nymMarkerToNymName.put("Мероним", "mero");
        nymMarkerToNymName.put("Холоним", "holo");
    }

    protected final static String nymExpression = "(\\[\\[[^\\]]*\\]\\])";
    protected final static Pattern nymPattern = Pattern.compile(BulgarianWikiModel.nymExpression);
    static final Pattern linkPattern = Pattern.compile("\\[\\[([^\\]]*)\\]\\]");
    static final Pattern macroPattern = Pattern.compile("\\{\\{([^\\}]*)\\}\\}");
    static final Pattern parens = Pattern.compile("\\(([^\\)]*)\\)");
    private IWiktionaryDataHandler delegate;
    private boolean hasAPOS = false;

	private DefinitionsWikiModel expander;
	Set<String> templates = null;

    public BulgarianWikiModel(IWiktionaryDataHandler we, Locale locale, String imageBaseURL, String linkBaseURL) {
        this(we, (WiktionaryIndex) null, locale, imageBaseURL, linkBaseURL);
    }

    public BulgarianWikiModel(IWiktionaryDataHandler we, WiktionaryIndex wi, Locale locale, String imageBaseURL, String linkBaseURL) {
        super(wi, locale, imageBaseURL, linkBaseURL);
        this.delegate = we;
        if (log.isDebugEnabled()) templates = new HashSet<String>();
        this.expander = new DefinitionsWikiModel(wi, this.fLocale, this.getImageBaseURL(), this.getWikiBaseURL(), templates);
    }

    public boolean parseBulgarianBlock(String block) {
        initialize();
        if (block == null) {
            return false;
        }
        WikipediaParser.parse(block, this, true, null);
        initialize();
        boolean r = hasAPOS;
        hasAPOS = false;
        return r;
    }

    @Override
    public void substituteTemplateCall(String templateName,
                                       Map<String, String> parameterMap, Appendable writer)
            throws IOException {
        String pos = getPOS(templateName);
        if (null != pos) {
            hasAPOS = true;
            delegate.addPartOfSpeech(pos);

            for (String section : parameterMap.keySet()) {
                if (section.contains("ЗНАЧЕНИЕ")) {	
                	extractDefinitions(parameterMap.get(section));
                } else if (section.contains("ПРЕВОД")) {
                    String sectionContent = parameterMap.get(section).replaceAll("\\[\\[:[^:]*:[^\\|]*\\|\\s*\\(?[^\\)\\]]*\\)?\\s*\\]\\]", "");
                    sectionContent = sectionContent.replaceAll("\\[\\[\\s*\\]\\]", "");
                    // if (sectionContent.contains("\n# ")) log.debug("Translation with sens number in {}", this.getPageName());
                    // TODO: use a shared instance of the translation parser.
                    TranslationsParser tp = new TranslationsParser();
                    tp.extractTranslations(sectionContent, delegate);

                    // extractTranslationsFromRawWikiCode(sectionContent)
                    //delegate.registerTranslation();
                } else if (section.contains("ID")) { // ID, same as page name for Bulgarian
                } else if (section.contains("РОД")) { //Gender
                } else if (section.contains("ТИП")) { // Type
                } else if (section.contains("ИЗРАЗИ")) { // Examples
                } else if (section.contains("ЕТИМОЛОГИЯ")) { // Etymology
                } else if (section.contains("ПРОИЗВОДНИ ДУМИ")) { // Derived Terms
                } else if (section.contains("ДРУГИ")) { // Related Words
                } else {
                    for (String rt : nymMarkerToNymName.keySet()) {
                        String body = parameterMap.get(section);
                        if (section.toLowerCase().contains(rt.toLowerCase()) && !body.isEmpty()) {
                            Matcher nymMatcher = nymPattern.matcher(body);
                            while (nymMatcher.find()) {
                                String name = nymMatcher.group().replaceAll("\\[", "").replaceAll("\\]", "");
                                // System.err.println(name);
                                delegate.registerNymRelation(name, nymMarkerToNymName.get(rt));
                            }
                        }
                    }
                }
            }
        } else if ("п".equals(templateName))  {
            String trad = parameterMap.get("2");
            if (null != trad) writer.append(trad);
        } else {
            // Just ignore the other template calls (uncomment to expand the template calls).
            // super.substituteTemplateCall(templateName, parameterMap, writer);
            appendTemplateCall(templateName, parameterMap, writer);
        }
    }

    private void extractTranslationsFromRawWikiCode(String sectionContent) {
        Matcher langTranslations = translationPattern.matcher(sectionContent);
        while (langTranslations.find()) {
            String trans = langTranslations.group();
            String lang = "";
            String body = "";
            String gloss = "";
            if (!trans.isEmpty()) {
                Matcher translationLangMatcher = translationLangPattern.matcher(trans);
                if (translationLangMatcher.find()) {
                    lang = BulgarianLangtoCode.threeLettersCode(translationLangMatcher.group().replace("*", "").replace("{", "").replace("}", ""));

                    Matcher translationBodyMatcher = translationBodyPattern.matcher(trans);
                    if (translationBodyMatcher.find()) {
                        body = translationBodyMatcher.group();
                        body = body.replaceAll("\\[\\[([^\\]\\[]*)\\s*,\\s*([^\\]\\[]*)\\]\\]","\\[\\[$1\\]\\],\\[\\[$2\\]\\]");
                    }
                    extractTranslations(lang, body);
                }
            }
        }
    }

    private void appendTemplateCall(String templateName, Map<String, String> parameterMap, Appendable writer) {
        try {
            writer.append("{{").append(templateName);
            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                // gwtWiki provides a parameter map that is sorted by insertion time
                // and unnamed parameters are inserted in the expected order.
                String key = entry.getKey();
                String val = entry.getValue();
                if (key.matches("\\d+")) {
                    writer.append("|").append(val);
                } else {
                    writer.append("|").append(key).append("=").append(val);
                }
            }
        writer.append("}}");
        } catch (IOException e) {
            // NOP
        }
    }

    private void extractDefinitions(String defSection) {
    	String protectedDefs = defSection.replaceAll("(?m)^(#{1,2})", "__$1__");
    	String def = expander.expandAll(protectedDefs);
    	def = def.replaceAll("(?m)^__(#{1,2})__", "$1");
    	Matcher definitionMatcher = WikiPatterns.definitionPattern.matcher(def);
        while (definitionMatcher.find()) {
            if (definitionMatcher.group(1).startsWith("Значението на думата все още не е въведено")) continue;
            delegate.registerNewDefinition(definitionMatcher.group(1));
        }
	}

	private String getPOS(String templateName) {
        for (String p : bulgarianPOS) {
            if (templateName.startsWith(p)) return p;
        }
        return null;
    }

    private void extractTranslations(String lang, String value) {
        // First black out commas that appear inside a pair of parenthesis
        value = blackoutCommas(value);
        String translations[] = value.split("[,;]");
        for (int i = 0; i < translations.length; i++) {
            String gloss="";
            Matcher glossMatcher = glossPattern.matcher(translations[i]);
            if (glossMatcher.find()) {
                gloss = glossMatcher.group();
                gloss = gloss.replaceAll("[\\[\\]\\{\\}]","").replaceAll("''","").replaceAll("\\.,",".");
            }
            extractTranslation(gloss, lang, translations[i]);
        }
    }

    private String blackoutCommas(String value) {
        Matcher m = parens.matcher(value);
        StringBuffer sb = new StringBuffer((int) (value.length() * 1.4));
        String inParens;
        while (m.find()) {
            inParens = m.group(1);
            inParens = inParens.replaceAll(";", "@@SEMICOLON@@");
            inParens = inParens.replaceAll(",", "@@COMMA@@");
            m.appendReplacement(sb, "(" + inParens + ")");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String restoreCommas(String value) {
        value = value.replaceAll("@@SEMICOLON@@", ";");
        value = value.replaceAll("@@COMMA@@", ",");
        return value;
    }

    private void extractTranslation(String gloss, String lang, String trans) {
        trans = restoreCommas(trans);
        Matcher macros = macroPattern.matcher(trans);
        String word = macros.replaceAll("");
        Matcher links = linkPattern.matcher(word);
        word = links.replaceAll("$1").trim();
        StringBuffer usage = new StringBuffer();
        StringBuffer w = new StringBuffer();
        Matcher m = glossPattern.matcher(word);
        while (m.find()) {
            usage.append(m.group(0));
            usage.append(" ");
            m.appendReplacement(w, " ");
        }
        m.appendTail(w);
        word = w.toString().trim().replaceAll("''","").replace(gloss.trim(),"").trim();
        Matcher m2 = parens.matcher(word);
        StringBuffer w2 = new StringBuffer();
        while (m2.find()) {
            usage.append(m2.group(0));
            usage.append(" ");
            m2.appendReplacement(w2, " ");
        }
        m2.appendTail(w2);
        word = w2.toString().trim();
        if (usage.length() > 0) usage.deleteCharAt(usage.length() - 1);
        if (word != null && !word.equals("") && lang != null && !lang.isEmpty()) {
            delegate.registerTranslation(lang, gloss, usage.toString(), word);
        }
    }

    public void displayUsedTemplates() {
    	if (templates != null && templates.size() > 0)
    		log.debug("{}: {}", this.delegate.currentLexEntry(), templates.toString());
    }
}
