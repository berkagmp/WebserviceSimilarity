package derek.project.algorithm;

import java.util.List;

/**
 * @author Mohamed Guendouz
 * https://gist.github.com/guenodz/d5add59b31114a3a3c66
 */
public class TFIDF {
	/**
	 * @param doc
	 *            list of strings
	 * @param term
	 *            String represents a term
	 * @return term frequency of term in document
	 */
	public static double tf(List<String> doc, String term) {
		double result = 0;
		for (String word : doc) {
			if (term.equalsIgnoreCase(word))
				result++;
		}
		return result / doc.size();
	}

	/**
	 * @param docs
	 *            list of list of strings represents the dataset
	 * @param term
	 *            String represents a term
	 * @return the inverse term frequency of term in documents
	 */
	public static double idf(List<List<String>> docs, String term) {
		double n = 0;
		for (List<String> doc : docs) {
			for (String word : doc) {
				if (term.equalsIgnoreCase(word)) {
					n++;
					break;
				}
			}
		}
		return Math.log(docs.size() / n);
	}

	/**
	 * @param doc
	 *            a text document
	 * @param docs
	 *            all documents
	 * @param term
	 *            term
	 * @return the TF-IDF of term
	 */
	public static double tfIdf(List<String> doc, List<List<String>> docs, String term) {
		return tf(doc, term) * idf(docs, term);

	}

}
