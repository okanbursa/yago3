package fromWikipedia;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fromOtherSources.PatternHardExtractor;
import fromOtherSources.WordnetExtractor;
import basics.FactSource;
import basics.FactWriter;
import basics.Theme;

/**
 * CategoryExtractorTester - YAGO2s
 * 
 * Tests CategoryMapper for different languages
 * 
 * @author Farzaneh Mahdisoltani
 * 
 */

public class CategoryMapperTester extends Extractor {

  private File inputFolder;
  private File outputFolder;
  
  @Override
  public Set<Theme> input() {
    Set<Theme> result = new TreeSet<Theme>();
    result.add(PatternHardExtractor.CATEGORYPATTERNS);
    result.add(PatternHardExtractor.TITLEPATTERNS);
    result.add(WordnetExtractor.WORDNETWORDS);
    for(String s:Extractor.languages){
      result.add(CategoryTranslator.CATEGORYTRANSLATEDFACTS_MAP.get(s));
      result.add(CategoryExtractor.CATEGORYMEMBERSHIP_MAP.get(s));
    }
    return result;
  }

  @Override
  public Set<Theme> output() {
    Set<Theme> result = new TreeSet<Theme>();
    for(String s:Extractor.languages){
      result.add(CategoryMapper.CATEGORYFACTS_TOREDIRECT_MAP.get(s));
      result.add(CategoryMapper.CATEGORYSOURCES_MAP.get(s));
    }
    return result;
  }
  
  @Override
  public void extract(File inputFolder, File outputFolder, String header) throws Exception {
    for(String s: Extractor.languages){
      CategoryMapper cm = new CategoryMapper(s);
      cm.extract(inputFolder, "Test on wikipedia article");
    }
    
  }
  
  public static void main(String[] args) throws Exception {
//  Extractor extractor = Extractor.forName("fromWikipedia.MultiInfoboxExtractorTester", null);
//  extractor.extract(new File("C:/Users/Administrator/data2/yago2s/"),
//      "blah blah");
  File f =  new File("D:/data2/yago2s");
  new CategoryMapperTester().extract( f, null);
}

  @Override
  public void extract(Map<Theme, FactWriter> output, Map<Theme, FactSource> input) throws Exception {
    // TODO Auto-generated method stub
    
  }

}