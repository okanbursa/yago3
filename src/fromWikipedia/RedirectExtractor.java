package fromWikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javatools.administrative.Announce;
import javatools.datatypes.FinalSet;
import javatools.filehandlers.FileLines;
import javatools.util.FileUtils;
import utils.MultilingualTheme;
import utils.Theme;
import basics.Fact;
import basics.FactComponent;
import extractors.MultilingualWikipediaExtractor;
import followUp.FollowUpExtractor;
import followUp.TypeChecker;
import fromOtherSources.PatternHardExtractor;

/**
 * Extracts all redirects from Wikipedia
 * 
 * @author Johannes Hoffart
 * 
 */
public class RedirectExtractor extends MultilingualWikipediaExtractor {

	@Override
	public Set<Theme> input() {
    return new FinalSet<Theme>(PatternHardExtractor.LANGUAGECODEMAPPING);
	}

	private static final Pattern pattern = Pattern
			.compile("\\[\\[([^#\\]]*?)\\]\\]");

	public static final MultilingualTheme REDIRECTFACTSDIRTY = new MultilingualTheme(
			"redirectLabelsDirty",
			"Redirect facts from Wikipedia redirect pages (to be type checked)");

	public static final MultilingualTheme REDIRECTFACTS = new MultilingualTheme(
			"yagoRedirectLabels",
			"Labels from Wikipedia redirect pages");

	@Override
	public Set<FollowUpExtractor> followUp() {
		HashSet<FollowUpExtractor> s=new HashSet<>();
		s.add(new TypeChecker(REDIRECTFACTSDIRTY.inLanguage(language), REDIRECTFACTS.inLanguage(language)));
		return s;
	}
	
	@Override
	public Set<Theme> output() {
		return new FinalSet<Theme>(
				REDIRECTFACTSDIRTY.inLanguage(this.language));
	}

	@Override
	public void extract() throws Exception {
		// Extract the information
		Announce.doing("Extracting Redirects");
		Map<String, String> redirects = new HashMap<>();
		
		Map<String, String> languagemap = PatternHardExtractor.LANGUAGECODEMAPPING
		    .factCollection().getStringMap("<hasThreeLetterLanguageCode>");

		BufferedReader in = FileUtils.getBufferedUTF8Reader(wikipedia);

		String titleEntity = null;
		redirect: while (true) {
			switch (FileLines.findIgnoreCase(in, "<title>", "<redirect")) {
			case -1:
				Announce.done();
				in.close();
				break redirect;
			case 0:
				titleEntity = FileLines.readToBoundary(in, "</title>");
				break;
			default:
				if (titleEntity == null)
					continue;
				FileLines.readTo(in, "<text");
				String redirectText = FileLines.readTo(in, "</text>")
						.toString().trim();
				String redirectTarget = getRedirectTarget(redirectText);

				if (redirectTarget != null) {
					redirects.put(titleEntity, redirectTarget);
				}
			}
		}

		Theme out = REDIRECTFACTSDIRTY.inLanguage(this.language);

		for (Entry<String, String> redirect : redirects.entrySet()) {
			out.write(new Fact(
					FactComponent.forYagoEntity(redirect.getValue()),
					"<redirectedFrom>", FactComponent.forStringWithLanguage(
							redirect.getKey(),
							this.language, languagemap)));
		}

		Announce.done();
	}

	private String getRedirectTarget(String redirect) {
		Matcher m = pattern.matcher(redirect);

		if (m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}

	public RedirectExtractor(String lang, File wikipedia) {
		super(lang, wikipedia);
	}

	public static void main(String[] args) throws Exception {
		Announce.setLevel(Announce.Level.DEBUG);
		new RedirectExtractor("en", new File("D:/en_wikitest.xml")).extract(
				new File("D:/data3/yago2s"), "Test on 1 wikipedia article");
	}
}
