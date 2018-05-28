package derek.project;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.tools.DocumentationTool.DocumentationTask;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import derek.project.algorithm.DiceCoefficient;
import derek.project.algorithm.RemovalStopwords;
import derek.project.algorithm.TFIDF;
import derek.project.algorithm.WordNet;
import derek.project.sample.DocumentGroup;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;

public class App {

	private static WordNet wn = new WordNet();
	static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator rcs = new WuPalmer(db);

	private static JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
	private static DiceCoefficient diceCoefficient = new DiceCoefficient();

	private static SnowballStemmer snowballStemmer = new englishStemmer();

	private static final int DICE = 0;
	private static final int JACCARD = 1;
	
	public static void main(String[] args) {
		// Make arrays
		String[] resource1 = {"flickr"};
		String[] method1 = {"flickr","photos","geo","photos","for","location"};
		String[] description1 = {"return","a","list","of","photos","for","the","calling","user","at","a","specific","latitude","longitude","and","accuracy"};
		String[] parameters1 = {"api","key","","rest","","lat","","lon","","accuracy","","extras","","per","page","","page"};
		
		String[] resource2 = {"flickr"};
		String[] method2 = {"flickr","photos","notes","edit"};
		String[] description2 = {"Edit","a","note","on","a","photo.","Coordinates","and","sizes","are","in","pixels,","based","on","the","500px","image","size","shown","on","individual","photo","pages."};
		String[] parameters2 = {"api_key","note_id","note_x","note_y","note_w","note_h","note_text"};
		
		String[] resource3 = {"onenote"};
		String[] method3 = {"get", "notebook", "sections"};
		String[] description3 = {"returns","a","collection","of","sections","within","a","specific","notebook"};
		String[] parameters3 = {"count","","filter","","orderby","","select","","top","","expand","","skip"};
		
		String[] resource4 = {"flickr"};
		String[] method4 = {"flickr","photos","geo","getlocation"};
		String[] description4 = {"get","the","geo","data","latitude","and","longitude","and","the","accuracy","level","for","a","photo"};
		String[] parameters4 = {"api","key","","rest","","photo","id"};
		
		String[] resource5 = {"photoslibrary"};
		String[] method5 = {"photoslibrary","mediaItems","get"};
		String[] description5 = {"returns","the","media","item","specified","based","on","a","given","media","item","id"};
		String[] parameters5 = {"description","required","type","pattern","location"};
		
		List<String[]> list1 = new ArrayList<>();
		List<String[]> list2 = new ArrayList<>();
		List<String[]> list3 = new ArrayList<>();
		List<String[]> list4 = new ArrayList<>();
		List<String[]> list5 = new ArrayList<>();
		//list1.add(resource1);	list2.add(resource2);	list3.add(resource3);	list4.add(resource4);	list5.add(resource5);
		list1.add(method1);		list2.add(method2);		list3.add(method3);		list4.add(method4);		list5.add(method5);
		list1.add(description1);list2.add(description2);list3.add(description3);list4.add(description4);list5.add(description5);
		//list1.add(parameters1);	list2.add(parameters2);	list3.add(parameters3);	list4.add(parameters4);list5.add(parameters5);
		
		//out.println(DocumentGroup.mergeLists(list1));
		
		List<List<String>> d = DocumentGroup.getDocumentGroup(false, false);
		List<List<String>> ds = DocumentGroup.getDocumentGroup(true, false);
		
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list2), d, ds);
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list3), d, ds);
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list4), d, ds);
		calSimilarity(DocumentGroup.mergeLists(list1), DocumentGroup.mergeLists(list5), d, ds);
//		calSimilarity(DocumentGroup.mergeLists(list2), DocumentGroup.mergeLists(list5), d, ds);
//		calSimilarity(DocumentGroup.mergeLists(list2), DocumentGroup.mergeLists(list4), d, ds);
//		calSimilarity(DocumentGroup.mergeLists(list3), DocumentGroup.mergeLists(list5), d, ds);
//		calSimilarity(DocumentGroup.mergeLists(list4), DocumentGroup.mergeLists(list5), d, ds);
	}
	
	public static void calSimilarity(List<String> list1, List<String> list2, List<List<String>> d, List<List<String>> ds) {
		TFIDF tfidf = new TFIDF();
		
		// Change array to List
		List<String> v1 = list1;
		List<String> v2 = list2;

		// Stop words removal
		v1.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
		v2.removeIf(RemovalStopwords.predicateForStopwordsRemoval);

		// Ordering
		Descending descending = new Descending();
		Collections.sort(v1, descending);
		Collections.sort(v2, descending);

		// Stemming
		List<String> vs1 = stemmingForList(v1);
		List<String> vs2 = stemmingForList(v2);

		// Make standard vector
		List<String> v3 = new ArrayList<String>();
		v3.addAll(v1);
		v3.addAll(v2);
		// Remove redundancy
		HashSet<String> hashSet = new HashSet<>(v3);
		v3 = new ArrayList<String>(hashSet);
		// Ordering
		Collections.sort(v3, descending);
		
		// Make stemming standard vector
		List<String> vs3 = new ArrayList<String>();
		vs3.addAll(vs1);
		vs3.addAll(vs2);
		// Remove redundancy
		hashSet = new HashSet<>(vs3);
		vs3 = new ArrayList<String>(hashSet);
		// Ordering
		Collections.sort(vs3, descending);

		// Make document set for TF-IDF
		d.add(v1);
		d.add(v2);
		// List<List<String>> d = Arrays.asList(v1, v2);
		//out.println(d.size());

		// Make stemming document set for TF-IDF
		ds.add(vs1);
		ds.add(vs2);
		// List<List<String>> ds = Arrays.asList(vs1, vs2);
		//out.println(ds.size());

		out.println("Vectors without Stemming");
		out.println(Arrays.toString(v1.toArray()));
		out.println(Arrays.toString(v2.toArray()));
		out.println(Arrays.toString(v3.toArray()));

		/*out.println("Vectors with Stemming");
		out.println(Arrays.toString(vs1.toArray()));
		out.println(Arrays.toString(vs2.toArray()));
		out.println(Arrays.toString(vs3.toArray()));*/

		// Make TF-IDF array
		double[] t1 = new double[v1.size()];
		double[] t2 = new double[v2.size()];
		double[] ts1 = new double[vs1.size()];
		double[] ts2 = new double[vs2.size()];

		// Set TF-IDF array
		int c = 0;
		for (String w : v1)
			t1[c++] = tfidf.tfIdf(v1, d, w);
		c = 0;
		for (String w : vs1)
			ts1[c++] = tfidf.tfIdf(vs1, ds, w);
		c = 0;
		for (String w : v2)
			t2[c++] = tfidf.tfIdf(v2, d, w);
		c = 0;
		for (String w : vs2)
			ts2[c++] = tfidf.tfIdf(vs2, ds, w);

		/*out.println();
		out.println("TF-IDF Vector");
		out.println(Arrays.toString(t1));
		out.println(Arrays.toString(t2));
		out.println("TF-IDF Vector after stemming");
		out.println(Arrays.toString(ts1));
		out.println(Arrays.toString(ts2));*/

		// Make list for excel
		List<Map<String, Double>> resultList = new ArrayList<>();
		Map<String, Double> resultMap;

		// Make final vectors
		double[] j1;
		double[] j2;
		double[] js1;
		double[] js2;
		double[] d1;
		double[] d2;
		double[] ds1;
		double[] ds2;

		boolean tfidfYn = false;

		for (double semanticRatio = 0; semanticRatio < 110; semanticRatio += 10) {

			tfidfYn = false;
//			j1 = getVector(v1, v3, t1, tfidfYn, JACCARD, semanticRatio);
//			j2 = getVector(v2, v3, t2, tfidfYn, JACCARD, semanticRatio);
//			js1 = getVector(vs1, vs3, ts1, tfidfYn, JACCARD, semanticRatio);
//			js2 = getVector(vs2, vs3, ts2, tfidfYn, JACCARD, semanticRatio);
			d1 = getVector(v1, v3, t1, tfidfYn, DICE, semanticRatio);
			d2 = getVector(v2, v3, t2, tfidfYn, DICE, semanticRatio);
			ds1 = getVector(vs1, vs3, ts1, tfidfYn, DICE, semanticRatio);
			ds2 = getVector(vs2, vs3, ts2, tfidfYn, DICE, semanticRatio);

			// Result
//			out.println(semanticRatio + "\tj\t" + cosineSimilarity(j1, j2));
			out.println(semanticRatio + "\td\t" + cosineSimilarity(d1, d2));
//			out.println(semanticRatio + "\tjs\t" + cosineSimilarity(js1, js2));
			out.println(semanticRatio + "\tds\t" + cosineSimilarity(ds1, ds2));

			// Make final vectors with TF-IDF
			tfidfYn = true;
//			j1 = getVector(v1, v3, t1, tfidfYn, JACCARD, semanticRatio);
//			j2 = getVector(v2, v3, t2, tfidfYn, JACCARD, semanticRatio);
//			js1 = getVector(vs1, vs3, ts1, tfidfYn, JACCARD, semanticRatio);
//			js2 = getVector(vs2, vs3, ts2, tfidfYn, JACCARD, semanticRatio);
			d1 = getVector(v1, v3, t1, tfidfYn, DICE, semanticRatio);
			d2 = getVector(v2, v3, t2, tfidfYn, DICE, semanticRatio);
			ds1 = getVector(vs1, vs3, ts1, tfidfYn, DICE, semanticRatio);
			ds2 = getVector(vs2, vs3, ts2, tfidfYn, DICE, semanticRatio);

			// Result
//			out.println(semanticRatio + "\tjt\t" + cosineSimilarity(j1, j2));
			out.println(semanticRatio + "\tdt\t" + cosineSimilarity(d1, d2));
//			out.println(semanticRatio + "\tjst\t" + cosineSimilarity(js1, js2));
			out.println(semanticRatio + "\tdst\t" + cosineSimilarity(ds1, ds2));
		}
	}

	public static String[] stemmingForArr(String[] args) {
		String[] result = new String[args.length];

		for (int i = 0; i < args.length; i++) {
			snowballStemmer.setCurrent(args[i]);
			snowballStemmer.stem();
			result[i] = snowballStemmer.getCurrent();
		}

		return result;
	}

	public static List<String> stemmingForList(List<String> args) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < args.size(); i++) {
			snowballStemmer.setCurrent(args.get(i));
			snowballStemmer.stem();
			result.add(snowballStemmer.getCurrent());
		}

		return result;
	}

	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;

		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}

		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	public static double[] getVector(List<String> doc1, List<String> target, double[] tfidf, boolean tfidfYn, int syntax, double semanticRatio) {
		final double ratioForSemantic = semanticRatio / 100;
		final double ratioForSyntax = (100 - semanticRatio) / 100;
		double[] result = new double[target.size()];
		int i = 0;
		double temp = 0;
		double value = 0;
		
		for (String s1 : target) {
			temp = 0;

			for (int k = 0; k < doc1.size(); k++) {
				//out.println(s1 + "/" + doc1.get(k));
				if (s1.equals(doc1.get(k))) {
					temp = (tfidfYn) ? tfidf[k] : 1;
					break;
				} else {
					for (int w = 0; w < 1; w++) {
						//wordnet = wn.cal(rcs[w], s1, doc1.get(k));
						
						if (syntax == DICE) {
							value = (wn.calByMax(rcs, s1, doc1.get(k)) * ratioForSemantic)
									+ (diceCoefficient.diceCoefficientOptimized(s1, doc1.get(k)) * ratioForSyntax);
						} else if (syntax == JACCARD) {
							value = (wn.calByMax(rcs, s1, doc1.get(k)) * ratioForSemantic)
									+ (jaccardSimilarity.apply(s1, doc1.get(k)) * ratioForSyntax);
						}
						
						if (temp < value) {
							temp = value;
						}
					}

					//out.println("temp-" + temp);
					if (k == doc1.size() - 1)
						temp *= (tfidfYn) ? tfidf[k] : 1;
				}
			}
			//out.println();
			result[i++] = temp;
		}

		return result;
	}
	
	public static double[] getVector_old(List<String> v1, List<String> target, double[] tfidf, boolean tfidfYn, int syntax,
			double semanticRatio) {
		final double ratioForSemantic = semanticRatio / 100;
		final double ratioForSyntax = (100 - semanticRatio) / 100;

		double[] result = new double[target.size()];
		int i = 0;
		double value = 0;

		for (String s1 : target) {
			for (int k = 0; k < v1.size(); k++) {
				if (s1.equals(v1.get(k))) {
					value = (tfidfYn) ? tfidf[k] : 1;
					break;
				} else {
					if (syntax == DICE) {
						value = (wn.calByMax(rcs, s1, v1.get(k)) * ratioForSemantic)
								+ (diceCoefficient.diceCoefficientOptimized(s1, v1.get(k)) * ratioForSyntax);
					} else if (syntax == JACCARD) {
						value = (wn.calByMax(rcs, s1, v1.get(k)) * ratioForSemantic)
								+ (jaccardSimilarity.apply(s1, v1.get(k)) * ratioForSyntax);
					}

				}
			}
			result[i++] = value;
		}

		return result;
	}

}

class Descending implements Comparator<String> {
	@Override
	public int compare(String o1, String o2) {
		return o2.compareTo(o1);
	}
}