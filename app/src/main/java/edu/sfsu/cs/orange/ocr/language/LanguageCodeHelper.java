/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.cs.orange.ocr.language;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.app.labelswhispering.R;


/**
 * Class for handling functions relating to converting between standard language
 * codes, and converting language codes to language names.
 */
public class LanguageCodeHelper {
    public static final String TAG = "LanguageCodeHelper";

    /**
     * Private constructor to enforce noninstantiability
     */
    private LanguageCodeHelper() {
        throw new AssertionError();
    }

    /**
     * Map an ISO 639-3 language code to an ISO 639-1 language code.
     * <p/>
     * There is one entry here for each language recognized by the OCR engine.
     *
     * @param languageCode ISO 639-3 language code
     * @return ISO 639-1 language code
     */
    public static String mapLanguageCode(String languageCode) {
        switch (languageCode) {
            case "afr":  // Afrikaans
                return "af";
            case "sqi":  // Albanian
                return "sq";
            case "ara":  // Arabic
                return "ar";
            case "aze":  // Azeri
                return "az";
            case "eus":  // Basque
                return "eu";
            case "bel":  // Belarusian
                return "be";
            case "ben":  // Bengali
                return "bn";
            case "bul":  // Bulgarian
                return "bg";
            case "cat":  // Catalan
                return "ca";
            case "chi_sim":  // Chinese (Simplified)
                return "zh-CN";
            case "chi_tra":  // Chinese (Traditional)
                return "zh-TW";
            case "hrv":  // Croatian
                return "hr";
            case "ces":  // Czech
                return "cs";
            case "dan":  // Danish
                return "da";
            case "nld":  // Dutch
                return "nl";
            case "eng":  // English
                return "en";
            case "est":  // Estonian
                return "et";
            case "fin":  // Finnish
                return "fi";
            case "fra":  // French
                return "fr";
            case "glg":  // Galician
                return "gl";
            case "deu":  // German
                return "de";
            case "ell":  // Greek
                return "el";
            case "heb":  // Hebrew
                return "he";
            case "hin":  // Hindi
                return "hi";
            case "hun":  // Hungarian
                return "hu";
            case "isl":  // Icelandic
                return "is";
            case "ind":  // Indonesian
                return "id";
            case "ita":  // Italian
                return "it";
            case "jpn":  // Japanese
                return "ja";
            case "kan":  // Kannada
                return "kn";
            case "kor":  // Korean
                return "ko";
            case "lav":  // Latvian
                return "lv";
            case "lit":  // Lithuanian
                return "lt";
            case "mkd":  // Macedonian
                return "mk";
            case "msa":  // Malay
                return "ms";
            case "mal":  // Malayalam
                return "ml";
            case "mlt":  // Maltese
                return "mt";
            case "nor":  // Norwegian
                return "no";
            case "pol":  // Polish
                return "pl";
            case "por":  // Portuguese
                return "pt";
            case "ron":  // Romanian
                return "ro";
            case "rus":  // Russian
                return "ru";
            case "srp":  // Serbian (Latin) // TODO is google expecting Cyrillic?
                return "sr";
            case "slk":  // Slovak
                return "sk";
            case "slv":  // Slovenian
                return "sl";
            case "spa":  // Spanish
                return "es";
            case "swa":  // Swahili
                return "sw";
            case "swe":  // Swedish
                return "sv";
            case "tgl":  // Tagalog
                return "tl";
            case "tam":  // Tamil
                return "ta";
            case "tel":  // Telugu
                return "te";
            case "tha":  // Thai
                return "th";
            case "tur":  // Turkish
                return "tr";
            case "ukr":  // Ukrainian
                return "uk";
            case "vie":  // Vietnamese
                return "vi";
            default:
                return "";
        }
    }

    /**
     * Map the given ISO 639-3 language code to a name of a language, for example,
     * "Spanish"
     *
     * @param context      interface to calling application environment. Needed to access
     *                     values from strings.xml.
     * @param languageCode ISO 639-3 language code
     * @return language name
     */
    public static String getOcrLanguageName(Context context, String languageCode) {
        Resources res = context.getResources();
        String[] language6393 = res.getStringArray(R.array.iso6393);
        String[] languageNames = res.getStringArray(R.array.languagenames);
        int len;

        // Finds the given language code in the iso6393 array, and takes the name with the same index
        // from the languagenames array.
        for (len = 0; len < language6393.length; len++) {
            if (language6393[len].equals(languageCode)) {
                Log.d(TAG, "getOcrLanguageName: " + languageCode + "->"
                        + languageNames[len]);
                return languageNames[len];
            }
        }

        Log.d(TAG, "languageCode: Could not find language name for ISO 693-3: "
                + languageCode);
        return languageCode;
    }

}
