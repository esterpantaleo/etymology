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
 * @author serasset
 *
 */
public class WiktionaryExtractor extends AbstractWiktionaryExtractor {

    //TODO: Handle Wikisaurus entries.
    //DONE: extract pronunciation
    //TODO: attach multiple pronounciation correctly
    static Logger log = LoggerFactory.getLogger(WiktionaryExtractor.class);

    protected final static String languageSectionPatternString = "==\\s*([^=]*)\\s*==";
    protected final static String sectionPatternString = "={2,5}\\s*([^=]*)\\s*={2,5}";
    protected final static String pronPatternString = "\\{\\{IPA\\|([^\\}\\|]*)(.*)\\}\\}";

    private enum Block {NOBLOCK, IGNOREPOS, TRADBLOCK, DEFBLOCK, INFLECTIONBLOCK, ORTHOALTBLOCK, NYMBLOCK, CONJUGATIONBLOCK, ETYMOLOGYBLOCK, PRONBLOCK}

    private WiktionaryDataHandler ewdh; // English specific version of the data handler.
    public WiktionaryExtractor(IWiktionaryDataHandler wdh) {
        super(wdh);
        if (wdh instanceof WiktionaryDataHandler) {
            ewdh = (WiktionaryDataHandler) wdh;
        } else {
            log.error("English Wiktionary Extractor instanciated with a non english data handler!");
        }
    }

    protected static Pattern languageSectionPattern;
    protected final static Pattern sectionPattern;
    protected final static HashMap<String, String> nymMarkerToNymName;
    protected final static Pattern pronPattern;

    static {
        languageSectionPattern = Pattern.compile(languageSectionPatternString);

        sectionPattern = Pattern.compile(sectionPatternString);
        pronPattern = Pattern.compile(pronPatternString);

        nymMarkerToNymName = new HashMap<String,String>(20);
        nymMarkerToNymName.put("Synonyms", "syn");
        nymMarkerToNymName.put("Antonyms", "ant");
        nymMarkerToNymName.put("Hyponyms", "hypo");
        nymMarkerToNymName.put("Hypernyms", "hyper");
        nymMarkerToNymName.put("Meronyms", "mero");
        nymMarkerToNymName.put("Holonyms", "holo");
        nymMarkerToNymName.put("Troponyms", "tropo");

        // TODO: Treat Abbreviations and Acronyms and contractions and Initialisms
        // TODO: Alternative forms
        // TODO: Extract quotations from definition block + from Quotations section

    }

    private Block currentBlock;
    private int blockStart = -1;

    private String currentNym = null;
    private ExpandAllWikiModel wikiExpander;
    @Override
    public void setWiktionaryIndex(WiktionaryIndex wi) {
        super.setWiktionaryIndex(wi);
        wikiExpander = new ExpandAllWikiModel(wi, Locale.ENGLISH, "--DO NOT USE IMAGE BASE URL FOR DEBUG--", "");
    }

    /* (non-Javadoc)
     * @see org.getalp.dbnary.WiktionaryExtractor#extractData(java.lang.String, org.getalp.blexisma.semnet.SemanticNetwork)
     */
    @Override
    public void extractData() {
        // TODO: adapt extractor to allow extraction of foreign data.
        wdh.initializePageExtraction(wiktionaryPageName);
        Matcher languageFilter = sectionPattern.matcher(pageContent);
        while (languageFilter.find() && ! languageFilter.group(1).equals("English")) {
            // NOP
        }
        // Either the filter is at end of sequence or on English language header.
        if (languageFilter.hitEnd()) {
            // There is no english data in this page.
            return ;
        }
        int englishSectionStartOffset = languageFilter.end();
        // Advance till end of sequence or new language section
        while (languageFilter.find() && languageFilter.group().charAt(2) == '=') {
            // NOP
        }
        // languageFilter.find();
        int englishSectionEndOffset = languageFilter.hitEnd() ? pageContent.length() : languageFilter.start();

        extractEnglishData(englishSectionStartOffset, englishSectionEndOffset);
        wdh.finalizePageExtraction();
    }

    protected void extractEnglishData(int startOffset, int endOffset) {
        Matcher m = sectionPattern.matcher(pageContent);
        m.region(startOffset, endOffset);
        wdh.initializeEntryExtraction(wiktionaryPageName);
        wikiExpander.setPageName(wiktionaryPageName);
        currentBlock = Block.NOBLOCK;

        while(m.find()) {
            HashMap<String, Object> context = new HashMap<String, Object>();
            Block nextBlock = computeNextBlock(m, context);

            if (nextBlock == null) continue;
            // If current block is IGNOREPOS, we should ignore everything but a new DEFBLOCK/INFLECTIONBLOCK
            if (Block.IGNOREPOS != currentBlock || (Block.DEFBLOCK == nextBlock || Block.INFLECTIONBLOCK == nextBlock)) {
                leaveCurrentBlock(m);
                gotoNextBlock(nextBlock, context);
            }
        }
        // Finalize the entry parsing
        leaveCurrentBlock(m);
        wdh.finalizeEntryExtraction();
    }

    private Block computeNextBlock(Matcher m, Map<String, Object> context) {
        String title = m.group(1).trim();
        String nym;
        context.put("start", m.end());

        if (title.equals("Pronunciation")) {
            return Block.PRONBLOCK;
        } else if (WiktionaryDataHandler.isValidPOS(title)) {
            context.put("pos", title);
            return Block.DEFBLOCK;
        } else if (title.equals("Translations")) { // TODO: some sections are using Translation in the singular form...
            return Block.TRADBLOCK;
        } else if (title.equals("Alternative spellings")) {
            return Block.ORTHOALTBLOCK;
        } else if (title.equals("Conjugation")) {
            return Block.CONJUGATIONBLOCK;
        } else if (title.startsWith("Etymology")) {
            return Block.ETYMOLOGYBLOCK;
        } else if (null != (nym = nymMarkerToNymName.get(title))) {
            context.put("nym", nym);
            return Block.NYMBLOCK;
        } else {
            log.debug("Ignoring content of section {} in {}", title, this.wiktionaryPageName);
            return Block.NOBLOCK;
        }
    }

    private void gotoNextBlock(Block nextBlock, HashMap<String, Object> context) {
        currentBlock = nextBlock;
        Object start = context.get("start");
        blockStart = (null == start) ? -1 : (int) start;
        switch (nextBlock) {
            case NOBLOCK:
            case IGNOREPOS:
                break;
            case DEFBLOCK:
                String pos = (String) context.get("pos");
                wdh.addPartOfSpeech(pos);
                break;
            case TRADBLOCK:
                break;
            case ORTHOALTBLOCK:
                break;
            case NYMBLOCK:
                currentNym = (String) context.get("nym");
                break;
            case PRONBLOCK:
                break;
            case CONJUGATIONBLOCK:
                break;
            case ETYMOLOGYBLOCK:
                break;
            default:
                assert false : "Unexpected block while parsing: " + wiktionaryPageName;
        }

    }

    private void leaveCurrentBlock(Matcher m) {
        if (blockStart == -1) {
            return;
        }

        int end = computeRegionEnd(blockStart, m);

        switch (currentBlock) {
            case NOBLOCK:
            case IGNOREPOS:
                break;
            case DEFBLOCK:
                extractMorphology(blockStart,end);
                extractDefinitions(blockStart, end);
                break;
            case TRADBLOCK:
                extractTranslations(blockStart, end);
                break;
            case ORTHOALTBLOCK:
                extractOrthoAlt(blockStart, end);
                break;
            case NYMBLOCK:
                extractNyms(currentNym, blockStart, end);
                currentNym = null;
                break;
            case PRONBLOCK:
                extractPron(blockStart, end);
                break;
            case CONJUGATIONBLOCK:
                extractConjugation(blockStart, end);
                break;
            case ETYMOLOGYBLOCK:
                extractEtymology(blockStart, end);
                break;
            default:
                assert false : "Unexpected block while parsing: " + wiktionaryPageName;
        }

        blockStart = -1;
    }

    private void extractEtymology(int blockStart, int end) {
        // TODO: extract the Etymology information
    }


    private void extractConjugation(int startOffset, int endOffset) {
        log.debug("Conjugation extraction not yet implemented in:\t{}", wiktionaryPageName);
    }

    private EnglishInflectionData plural = new EnglishInflectionData().plural();
    private EnglishInflectionData singular = new EnglishInflectionData().singular();
    private EnglishInflectionData comparative = new EnglishInflectionData().comparative();
    private EnglishInflectionData superlative = new EnglishInflectionData().superlative();
    private EnglishInflectionData pres3Sg = new EnglishInflectionData().presentTense().thirdPerson().singular();
    private EnglishInflectionData presPtc = new EnglishInflectionData().presentTense().participle();
    private EnglishInflectionData past = new EnglishInflectionData().pastTense();
    private EnglishInflectionData pastPtc = new EnglishInflectionData().pastTense().participle();

    private void extractMorphology(int startOffset, int endOffset) {
        // TODO: For some entries, there are several morphology information covering different word senses
        // TODO: Handle such cases (by creating another lexical entry ?) // Similar to reflexiveness in French wiktionary
        if (! ewdh.isEnabled(IWiktionaryDataHandler.Feature.MORPHOLOGY)) return;

        WikiText text = new WikiText(pageContent, startOffset, endOffset);
        HashSet<Class> templatesOnly = new HashSet<Class>();
        templatesOnly.add(WikiText.Template.class);

        WikiEventsSequence wikiTemplates = text.templates();

        // Matcher macroMatcher = WikiPatterns.macroPattern.matcher(pageContent);
        // macroMatcher.region(startOffset, endOffset);

        // while (macroMatcher.find()) {
        int nbTempl = 0;
        for (WikiText.Token wikiTemplate : wikiTemplates) {
            nbTempl++;
            WikiText.Template tmpl = (WikiText.Template) wikiTemplate;
            // String g1 = macroMatcher.group(1);
            String g1 = tmpl.getName();
            if (g1.equals("en-noun")) {
                // log.debug("MORPHOLOGY EXTRACTION FROM : {}\tin\t{}", tmpl.toString(), wiktionaryPageName);

                Map<String, String> args = tmpl.parseArgs();
                extractNounMorphology(args, false);
            } else if (g1.equals("en-proper noun") || g1.equals("en-proper-noun") || g1.equals("en-prop")) {
                // log.debug("MORPHOLOGY EXTRACTION FROM : {}\tin\t{}", tmpl.toString(), wiktionaryPageName);

                Map<String, String> args = tmpl.parseArgs();
                extractNounMorphology(args, true);
            } else if (g1.equals("en-plural noun")) {
                ewdh.addInflectionOnCanonicalForm(new EnglishInflectionData().plural());
                Map<String, String> args = tmpl.parseArgs();
                if (args.containsKey("sg")) {
                    String singularForm = args.get("sg");
                    addForm(singular.toPropertyObjectMap(), singularForm);
                    args.remove("sg");
                }
                if (! args.isEmpty()) {
                    log.debug("en-plural noun macro: Non handled parameters : \t{}\tin\t{}", args, this.wiktionaryPageName);
                }

            } else if (g1.equals("en-adj") || g1.equals("en-adv") ||
                    g1.equals("en-adjective") || g1.equals("en-adj-more") ||
                    g1.equals("en-adverb") || g1.equals("en-adv-more")) {
                // log.debug("MORPHOLOGY EXTRACTION FROM : {}\tin\t{}", tmpl.toString(), wiktionaryPageName);

                // TODO: mark canonicalForm as ????
                Map<String, String> args = tmpl.parseArgs();
                extractAdjectiveMorphology(args);


            } else if (g1.equals("en-verb")) {
                // TODO: extract verb morphology
                Map<String, String> args = tmpl.parseArgs();

                extractVerbMorphology(args);

            } else if (g1.equals("en-abbr")) {
                // TODO: extract abbreviation morphology
                log.debug("Use of deprecated en-abbr template in\t{}", wiktionaryPageName);
            } else if (g1.equals("en-acronym")) {
                // TODO: extract acronym morphology
                log.debug("Use of deprecated en-acronym template in\t{}", wiktionaryPageName);
            } else if (g1.equals("en-con")) {
                // nothing to extract...
            } else if (g1.equals("en-cont")) {
                // contraction
                log.debug("Use of deprecated en-cont (contraction) template in\t{}", wiktionaryPageName);
            } else if (g1.equals("en-det")) {
                // nothing to extract
            } else if (g1.equals("en-initialism")) {
                // TODO: extract initialism morphology
                log.debug("Use of deprecated en-initialism template in\t{}", wiktionaryPageName);
            } else if (g1.equals("en-interj")) {
                // nothing to extract
            } else if (g1.equals("en-part") || g1.equals("en-particle")) {
                // nothing to extract
            } else if (g1.equals("en-phrase")) {
                // nothing to extract
            } else if (g1.equals("en-prefix")) {
                // nothing to extract ??
                Map<String, String> args = tmpl.parseArgs();
                args.remove("sort");
                if (! args.isEmpty()) {
                    log.debug("other args in en-suffix template\t{}\tin\t{}", args, wiktionaryPageName);
                }
            } else if (g1.equals("en-prep")) {
                // nothing to extract
            } else if (g1.equals("en-prep phrase")) {
                // TODO: the first argument is the head if different from pagename...
            } else if (g1.equals("en-pron")) {
                // TODO: extract part morphology
                Map<String, String> args = tmpl.parseArgs();
                if (null != args.get("desc")) {
                    log.debug("desc argument in en-pron template in\t{}", wiktionaryPageName);
                    args.remove("desc");
                }
                if (! args.isEmpty()) {
                    log.debug("other args in en-pron template\t{}\tin\t{}", args, wiktionaryPageName);
                }
            } else if (g1.equals("en-proverb")) {
                // TODO: the first argument (or head argument) is the head if different from pagename...
            } else if (g1.equals("en-punctuation mark")) {
                // TODO: the first argument (or head argument) is the head if different from pagename...
            } else if (g1.equals("en-suffix")) {
                // TODO: cat2 and cat3 sontains some additional categories...
                Map<String, String> args = tmpl.parseArgs();
                args.remove("sort");
                if (! args.isEmpty()) {
                    log.debug("other args in en-suffix template\t{}\tin\t{}", args, wiktionaryPageName);
                }
            } else if (g1.equals("en-symbol")) {
                // TODO: the first argument is the head if different from pagename...
            } else if (g1.equals("en-number")) {
                // TODO:
            } else if (g1.equals("head")) {
                Map<String, String> args = tmpl.parseArgs();
                String pos = args.get("2");
                if (null != pos && pos.endsWith("form")) {
                    // This is a inflected form
                    // TODO: Check if the inflected form is available in the base word morphology.
                    // TODO: we should remove the lexical entry as it is a form, so that definitions are not extracted.
                } else {
                    log.debug("MORPH: direct call to head\t{}\tin\t{}", tmpl.toString(), this.wiktionaryPageName);
                }
            } else {
                // log.debug("NOMORPH PATTERN:\t {}\t in:\t{}", g1, wiktionaryPageName);
                nbTempl--;
            }
        }
        if (nbTempl > 1) {
            log.debug("MORPHTEMPLATE: more than 1 morph template in\t{}", this.wiktionaryPageName);
        }
    }

    private void extractNounMorphology(Map<String, String> args, boolean properNoun) {
        // DONE: mark canonicalForm as singular
        ewdh.addInflectionOnCanonicalForm(new EnglishInflectionData().singular());

        // TODO: head is used to point to constituants of MWE, extract them and make parts explicit.
        String head = args.get("head");
        String headword;
        if (head != null && ! head.trim().equals("")) {
            // TODO: evaluate the runtime impact of systematic expansion (Should I expand only if a template is present in the argument value ?)
            headword = wikiExpander.expandAll(head,null); // Expand everything in the head value and provide a text only version.
        } else {
            headword = wiktionaryPageName;
        }
        args.remove("head");
        int argnum = 1;
        String arg = args.get(Integer.toString(argnum));
        args.remove(Integer.toString(argnum));
        boolean uncountable = false;
        if (arg == null && ! properNoun) {
            // There are no positional arg, meaning that the plural uses a s suffix
            arg = "s";
        }
        if (args.containsKey("plqual")) {
            args.put("pl1qual", args.get("plqual"));
            args.remove("plqual");
        }
        while (arg != null) {
            String note = args.get("pl" + argnum + "qual");
            if (arg.equals("s")) {
                if (!uncountable) ewdh.countable();
                addForm(plural.toPropertyObjectMap(), headword + "s", note);
            } else if (arg.equals("es")) {
                if (!uncountable) ewdh.countable();
                addForm(plural.toPropertyObjectMap(), headword + "es", note);
            } else if (arg.equals("-")) {
                // uncountable or usually uncountable
                uncountable = true;
                ewdh.uncountable();
            } else if (arg.equals("~")) {
                // countable and uncountable
                ewdh.countable();
                ewdh.uncountable();
            } else if (arg.equals("!")) {
                // plural not attested (exit loop)
                break;
            } else if (arg.equals("?")) {
                // unknown or uncertain plural (exit loop)
                break;
            } else {
                // pluralForm
                // TODO: if plural form = singular form, note entry as invariant
                if (!uncountable) ewdh.countable();
                addForm(plural.toPropertyObjectMap(), arg, note);
            }

            arg = args.get(Integer.toString(++argnum));
            args.remove(Integer.toString(argnum));
        }

        if (! args.isEmpty()) {
            log.debug("en-noun macro: Non handled parameters : \t{}\tin\t{}", args, this.wiktionaryPageName);
        }
    }

    private void extractAdjectiveMorphology(Map<String, String> args) {
        // TODO: head is used to point to constituants of MWE, extract them and make parts explicit.
        String h = args.get("head");
        String headword;
        if (h != null && ! h.trim().equals("")) {
            // TODO: evaluate the runtime impact of systematic expansion (Should I expand only if a template is present in the argument value ?)
            headword = wikiExpander.expandAll(h,null); // Expand everything in the head value and provide a text only version.
        } else {
            headword = wiktionaryPageName;
        }
        args.remove("head");
        if (args.containsKey("sup")) {
            args.put("sup1", args.get("sup"));
            args.remove("sup");
        }
        int argnum = 1;
        String arg = args.get(Integer.toString(argnum));
        args.remove(Integer.toString(argnum));
        boolean notComparable = false;
        boolean comparativeOnly = false;
        if (arg == null) {
            // There are no positional arg, meaning that the adjective uses more and most as comparative markers
            arg = "more";
        }
        while (arg != null) {
            if (arg.equals("more") && ! "many".equals(wiktionaryPageName) && ! "much".equals(wiktionaryPageName)) {
                if (!notComparable) ewdh.comparable();
                addForm(comparative.toPropertyObjectMap(), "more " + headword);
                if (null != args.get("sup" + argnum)) log.debug("Irregular superlative with comparative with more in\t{}", wiktionaryPageName);
                addForm(superlative.toPropertyObjectMap(), "most " + headword);
            } else if (arg.equals("further") && ! "far".equals(wiktionaryPageName)) {
                if (!notComparable) ewdh.comparable();
                addForm(comparative.toPropertyObjectMap(), "further " + headword);
                if (null != args.get("sup" + argnum)) log.debug("Irregular superlative with comparative with further in\t{}", wiktionaryPageName);
                addForm(superlative.toPropertyObjectMap(), "furthest " + headword);
            } else if (arg.equals("er")) {
                if (!notComparable) ewdh.comparable();
                String stem = headword.replaceAll("([^aeiou])e?y$", "$1").replaceAll("e$", "");
                String comp = stem + "er";
                addForm(comparative.toPropertyObjectMap(), comp);
                String sup = getSuperlative(args, argnum, comp);
                if (null != sup)
                    addForm(superlative.toPropertyObjectMap(), sup);
            } else if (arg.equals("-")) {
                // not comparable or not usually comparable
                notComparable = true;
                ewdh.notComparable();
                String sup = getSuperlative(args, argnum, "-");
                if (null != sup) {
                    addForm(superlative.toPropertyObjectMap(), sup);
                }
            } else if (arg.equals("+")) {
                // comparative only
                ewdh.addInflectionOnCanonicalForm(comparative);
                break;
            } else if (arg.equals("?")) {
                // unknown or uncertain plural (exit loop)
                break;
            } else {
                // comparativeForm given
                if (!notComparable) ewdh.comparable();
                addForm(comparative.toPropertyObjectMap(), arg);
                String sup = getSuperlative(args, argnum, arg);
                if (null != sup)
                    addForm(superlative.toPropertyObjectMap(), sup);
            }
            arg = args.get(Integer.toString(++argnum));
            args.remove(Integer.toString(argnum));
            args.remove("sup" + argnum);
        }

        if (! args.isEmpty()) {
            log.debug("en-adj macro: Non handled parameters : \t{}\tin\t{}", args, this.wiktionaryPageName);
        }
    }

    private void extractVerbMorphology(Map<String, String> args) {
        // Code based on wiktionary en-headword Lua Module code (date: 01/02/2016)

        String par1 = args.get("1");
        String par2 = args.get("2");
        String par3 = args.get("3");
        String par4 = args.get("4");

        String pres3sgForm = (null != par1) ? par1 : this.wiktionaryPageName + "s";
        String presPtcForm = (null != par2) ? par2 : this.wiktionaryPageName + "ing";
        String pastForm = (null != par3) ? par3 : this.wiktionaryPageName + "ed";
        String pastPtcForm = null;

        if (null != par1 && null == par2 && null == par3) {
            // This is the "new" format, which uses only the first parameter.
            if ("es".equals(par1)) {
                pres3sgForm = this.wiktionaryPageName + "es";
            } else if ("ies".equals(par1)) {
                if (! this.wiktionaryPageName.endsWith("y")) {
                    log.debug("VERBMORPH : Incorrect en-verb parameter \"ies\" on non y ending verb\t{}",this.wiktionaryPageName);
                }
                String stem = this.wiktionaryPageName.substring(0,this.wiktionaryPageName.length()-1);
                pres3sgForm = stem + "ies";
                presPtcForm = stem + "ying";
                pastForm = stem + "ied";
            } else if ("d".equals(par1)) {
                pres3sgForm = this.wiktionaryPageName + "s";
                presPtcForm = this.wiktionaryPageName + "ing";
                pastForm = this.wiktionaryPageName + "d";
            } else {
                pres3sgForm = this.wiktionaryPageName + "s";
                presPtcForm = par1 + "ing";
                pastForm = par1 + "ed";
            }
        } else {
            // This is the "legacy" format, using the second and third parameters as well.
            // It is included here for backwards compatibility and to ease the transition.
            if (null != par3) {
                if ("es".equals(par3)) {
                    pres3sgForm = par1 + par2 + "es";
                    presPtcForm = par1 + par2 + "ing";
                    pastForm = par1 + par2 + "ed";
                } else if ("ing".equals(par3)) {
                    pres3sgForm = this.wiktionaryPageName + "s";
                    presPtcForm = par1 + par2 + "ing";

                    if ("y".equals(par2)) {
                        pastForm = this.wiktionaryPageName + "d";
                    } else {
                        pastForm = par1 + par2 + "ed";
                    }
                } else if ("ed".equals(par3)) {
                    if ("i".equals(par2)) {
                        pres3sgForm = par1 + par2 + "es";
                        presPtcForm = this.wiktionaryPageName + "ing";
                    } else {
                        pres3sgForm = this.wiktionaryPageName + "s";
                        presPtcForm = par1 + par2 + "ing";
                    }
                    pastForm = par1 + par2 + "ed";
                } else if ("d".equals(par3)) {
                    pres3sgForm = this.wiktionaryPageName + "s";
                    presPtcForm = par1 + par2 + "ing";
                    pastForm = par1 + par2 + "d";
                } else {
                    // log.debug("VERBMORPH : Incorrect en-verb 3rd parameter  on verb\t{}",this.wiktionaryPageName);
                }
            } else {
                if (null != par2) {
                    if ("es".equals(par2)) {
                        pres3sgForm = par1 + "es";
                        presPtcForm = par1 + "ing";
                        pastForm = par1 + "ed";
                    } else if ("ies".equals(par2)) {
                        if (!(par1 + "y").equals(this.wiktionaryPageName)) {
                            log.debug("VERBMORPH : Incorrect en-verb 2rd parameter ies  with stem different to pagename on verb\t{}", this.wiktionaryPageName);
                        }
                        pres3sgForm = par1 + "ies";
                        presPtcForm = par1 + "ying";
                        pastForm = par1 + "ied";
                    } else if ("ing".equals(par2)) {
                        pres3sgForm = this.wiktionaryPageName + "s";
                        presPtcForm = par1 + "ing";
                        pastForm = par1 + "ed";
                    } else if ("ed".equals(par2)) {
                        pres3sgForm = this.wiktionaryPageName + "s";
                        presPtcForm = par1 + "ing";
                        pastForm = par1 + "ed";
                    } else if ("d".equals(par2)) {
                        if (!this.wiktionaryPageName.equals(par1)) {
                            log.debug("VERBMORPH : Incorrect en-verb 2rd parameter d with par1 different to pagename on verb\t{}", this.wiktionaryPageName);
                        }
                        pres3sgForm = this.wiktionaryPageName + "s";
                        presPtcForm = par1 + "ing";
                        pastForm = par1 + "d";
                    } else {
                        log.debug("VERBMORPH : unexpected 2rd parameter \"{}\" on verb\t{}", par2, this.wiktionaryPageName);
                    }
                }
            }
        }

        if (null != par4) {
            pastPtcForm = par4;
        }
        String pres3SgQual = args.get("pres_3sg_qual");
        String presPtcQual = args.get("pres_ptc_qual");
        String pastQual = args.get("past_qual");
        String pastPtcQual = args.get("past_ptc_qual");

        Map<String,String> pastForms = new HashMap<>();
        Map<String,String> pastPtcForms = new HashMap<>();

        addForm(pres3Sg.toPropertyObjectMap(), pres3sgForm, pres3SgQual);
        addForm(presPtc.toPropertyObjectMap(), presPtcForm, presPtcQual);
        addForm(past.toPropertyObjectMap(), pastForm, pastQual);
        if (null != pastForm) pastForms.put("1", pastForm);
        addForm(pastPtc.toPropertyObjectMap(), pastPtcForm, pastPtcQual);
        if (null != pastPtcForm) pastPtcForms.put("1", pastPtcForm);

        args.remove("1");
        args.remove("2");
        args.remove("3");
        args.remove("4");
        args.remove("pres_3sg_qual");
        args.remove("pres_ptc_qual");
        args.remove("past_qual");
        args.remove("past_ptc_qual");

        Matcher p3sgM = Pattern.compile("^pres_3sg(\\d+)$").matcher("");
        Matcher presPtcM = Pattern.compile("^pres_ptc(\\d+)$").matcher("");
        Matcher pastM = Pattern.compile("^past(\\d+)$").matcher("");
        Matcher pastPtcM = Pattern.compile("^past_ptc(\\d+)$").matcher("");

        for (String k : args.keySet()) {
            if (p3sgM.reset(k).matches()) {
                String i = p3sgM.group(1);
                String qual = args.get("pres_3sg" + i + "_qual");
                addForm(pres3Sg.toPropertyObjectMap(), args.get(k), qual);
            } else if (presPtcM.reset(k).matches()) {
                String i = presPtcM.group(1);
                String qual = args.get("pres_ptc" + i + "_qual");
                addForm(presPtc.toPropertyObjectMap(), args.get(k), qual);
            } else if (pastM.reset(k).matches()) {
                String i = pastM.group(1);
                String qual = args.get("past" + i + "_qual");
                pastForms.put(i, args.get(k));
                addForm(past.toPropertyObjectMap(), args.get(k), qual);
            } else if (pastPtcM.reset(k).matches()) {
                String i = pastPtcM.group(1);
                String qual = args.get("past_ptc" + i + "_qual");
                pastPtcForms.put(i, args.get(k));
                addForm(pastPtc.toPropertyObjectMap(), args.get(k), qual);
            } else {
                // ignore this argument
            }
        }

        if (pastPtcForms.size() == 0) {
            // duplicate all past forms as pastPtc
            for (String v : pastForms.values()) {
                addForm(pastPtc.toPropertyObjectMap(), v);
            }
        }
    }


    private String getSuperlative(Map<String, String> args, int argnum, String comp) {
        String k = "sup" + argnum;
        String sup = null;
        if (args.containsKey(k)) {
            sup = args.get(k);
        } else {
            if (comp.endsWith("er")) {
                sup = comp.replaceAll("er$", "est");
            } else if (! "-".equals(sup)) {
                log.debug("Missing superlative for irregular comparative in\t{}", wiktionaryPageName);
            }
        }
        if ("-".equals(sup))
            return null;
        else
            return sup;
    }

    private void addForm(HashSet<PropertyObjectPair> infl, String s) {
        addForm(infl, s, null);
    }

    private void addForm(HashSet<PropertyObjectPair> infl, String s, String note) {
        if (null == s || s.length() == 0 || s.equals("—") || s.equals("-")) return;

        ewdh.registerInflection(s, note, infl);
    }

    private void extractTranslations(int startOffset, int endOffset) {
       Matcher macroMatcher = WikiPatterns.macroPattern.matcher(pageContent);
       macroMatcher.region(startOffset, endOffset);
       String currentGloss = null;
        // TODO: there are templates called "qualifier" used to further qualify the translation check and evaluate if extracting its data is useful.
        while (macroMatcher.find()) {
            String g1 = macroMatcher.group(1);

            if (g1.equals("t+") || g1.equals("t-") || g1.equals("tø") || g1.equals("t")) {
                // DONE: Sometimes translation links have a remaining info after the word, keep it.
                String g2 = macroMatcher.group(2);
                int i1, i2;
                String lang, word;
                if (g2 != null && (i1 = g2.indexOf('|')) != -1) {
                    lang = LangTools.normalize(g2.substring(0, i1));

                    String usage = null;
                    if ((i2 = g2.indexOf('|', i1+1)) == -1) {
                        word = g2.substring(i1+1);
                    } else {
                        word = g2.substring(i1+1, i2);
                        usage = g2.substring(i2+1);
                    }
                    lang=EnglishLangToCode.threeLettersCode(lang);
                    if(lang!=null){
                        wdh.registerTranslation(lang, currentGloss, usage, word);
                    }

                }
            } else if (g1.equals("trans-top")) {
                // Get the glose that should help disambiguate the source acception
                String g2 = macroMatcher.group(2);
                // Ignore glose if it is a macro
                if (g2 != null && ! g2.startsWith("{{")) {
                    currentGloss = g2;
                }
            } else if (g1.equals("checktrans-top")) {
                // forget glose.
                currentGloss = null;
            } else if (g1.equals("trans-mid")) {
                // just ignore it
            } else if (g1.equals("trans-bottom")) {
                // Forget the current glose
                currentGloss = null;
            }
        }
    }

    @Override
    public void extractExample(String example) {
        // TODO: current example extractor cannot handle English data where different lines are used to define the example.

    }

    protected void extractPron(int startOffset, int endOffset) {

        Matcher pronMatcher = pronPattern.matcher(pageContent);
        pronMatcher.region(startOffset,endOffset);
        while (pronMatcher.find()) {
            String pron = pronMatcher.group(1);

            if (null == pron || pron.equals("")) return;

            if (! pron.equals("")) wdh.registerPronunciation(pron, "en-fonipa");
        }
    }

}
