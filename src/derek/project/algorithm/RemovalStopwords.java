package derek.project.algorithm;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class RemovalStopwords {
	public static final List<String> stopwordList = Arrays.asList(new String[] {"on", "at", "of", "to", "by", "in", "a", "an", "the", "and", "but", "or", "so", "for", ""});
	public static final Predicate<String> predicateForStopwordsRemoval = word -> stopwordList.contains(word);
	
	public String removeStopword(String str) {
		return str;
		
	}
}
