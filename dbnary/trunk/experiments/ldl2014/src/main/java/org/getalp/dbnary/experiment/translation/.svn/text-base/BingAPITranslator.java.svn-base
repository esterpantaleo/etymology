package org.getalp.dbnary.experiment.translation;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import org.getalp.blexisma.api.ISO639_3;

public class BingAPITranslator implements Translator {

	public BingAPITranslator(String translatorId, String translatorPass) {
		Translate.setClientId(translatorId);
		Translate.setClientSecret(translatorPass);
	}

	@Override
	public String translate(String source, String slang, String tlang) {

		String translatedText = null;

		try {

			Language s = get2LetterCode(slang);
			Language t = get2LetterCode(tlang);

			if (s != null && t != null)
				translatedText = Translate.execute(source, s, t);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return translatedText;
	}

	private Language get2LetterCode(String lang) {
		String twoLetterCode = ISO639_3.sharedInstance.getTerm2Code(lang);
		if (null != twoLetterCode) {
			return Language.fromString(twoLetterCode);
		}
		return null;
	}

}
