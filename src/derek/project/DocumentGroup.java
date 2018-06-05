package derek.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import derek.project.algorithm.RemovalStopwords;
import derek.project.config.AppConfig;
import derek.project.dao.ApiMethodDao;
import derek.project.dao.RequestParameterDao;
import derek.project.model.ApiMethod;
import derek.project.model.RequestParameter;

public class DocumentGroup {

	/**
	 * Retrieve AIP data from MySQL DB
	 * @return Map of APIs: Methods and Parameters
	 */
	public static Map<String, List<?>> getDataFromDB() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		ApiMethodDao apiMethodDao = context.getBean(ApiMethodDao.class);
		RequestParameterDao requestParameterDao = context.getBean(RequestParameterDao.class);

		List<ApiMethod> list1 = apiMethodDao.list();
		List<RequestParameter> list2 = requestParameterDao.list();

		Map<String, List<?>> map = new HashMap<String, List<?>>();
		map.put("ApiMethod", list1);
		map.put("RequestParameter", list2);

		context.close();

		return map;
	}
	
	/**
	 * Tokenisation of arrays in the List
	 * @param list
	 * @return Vector
	 */
	public static List<String> mergeLists(List<String[]> list){
		List<String> result = new ArrayList<>();
		
		for(String[] arr : list) {
			result.addAll(tokenisationArray(arr));
		}
		
		return result;
	}
	
	/**
	 * Tokenisation based on space of String array
	 * @param str
	 * @return
	 */
	public static List<String> tokenisationArray(String[] str) {
		List<String> list = new ArrayList<>();		
		String temp="";
		
		for(int i=0; i<str.length; i++) {
			temp = tokenisation(str[i]);
			
			if(temp.split(" ").length > 1) {
				for(int a=0; a<temp.split(" ").length;a++) {
					list.add(temp.split(" ")[a]);
				}
			}else {
				list.add(temp);
			}
			
		}
		
		return list;
	}
	
	/**
	 * Tokenisation with some rules
	 * @param str
	 * @return Tokenised String
	 */
	public static String tokenisation(String str) {
		str = str.replaceAll("POST", "Post")
				.replaceAll("GET", " get")
				.replaceAll("PUT", " put")
				.replaceAll("URL", " url")
				.replaceAll("POST", " post")
				.replaceAll("DELETE", " delete")
				.replaceAll("MMS", " mms")
				.replaceAll("SMS", " sms")
				.replaceAll("PROJECT", " project")
				.replaceAll("ID", " id")
				.replaceAll("XML", " xml")
				.replaceAll("JSON", " json")
				.replaceAll("SSH", " ssh")
				.replaceAll("UPC", " upc")
				.replaceAll("ISRC", " isrc")
				.replaceAll("IP", " ip")
				.replaceAll("AP", " ap")
				.replaceAll("API", " api")
				.replaceAll("PIN", " pin");
		
		if(str.split("[A-Z]+").length > 1) {
			str = str.replaceAll("([A-Z])", " $1").toLowerCase();
		}
		
		str = str.replaceAll("\\W", " ").replaceAll("_", " ").trim();
		
		while(str.indexOf("  ") > -1) {
			str = str.replaceAll("  ", " ");
		}
		
		return str;
	}

	/**
	 * Make document group for IDF with data of method, description of method or parameters
	 * @param stemming stemming or not
	 * @param paramYN includes parameters or not
	 * @return Document group
	 */
	@SuppressWarnings("unchecked")
	public static List<List<String>> getDocumentGroup(boolean stemming, boolean paramYN) {
		Map<String, List<?>> map = getDataFromDB();

		List<List<String>> data = new ArrayList<>();

		List<String> temp;
		Predicate<String> p = word -> word.length() < 2 || word.equals("");;

		String[] arr;
		for (ApiMethod m : (List<ApiMethod>) map.get("ApiMethod")) {
			arr = Optional.ofNullable(m.getDescription()).orElseGet(() -> " ").split(" ");

			if (arr.length > 1) {
				temp = new ArrayList<>(Arrays.asList(arr));
				temp.removeIf(p);
				temp.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
				
				data.add(temp);
			}
			
			arr = Optional.ofNullable(m.getMethod()).orElseGet(() -> " ").split(" ");

			if (arr.length > 1) {
				temp = new ArrayList<>(Arrays.asList(arr));
				temp.removeIf(p);
				temp.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
				
				data.add(temp);
			}
		}

		if(paramYN) {
			for (RequestParameter m : (List<RequestParameter>) map.get("RequestParameter")) {
				arr = Optional.ofNullable(m.getParam()).orElseGet(() -> " ").split(" ");
				
				if (arr.length > 1) {
					temp = new ArrayList<>(Arrays.asList(arr));
					temp.removeIf(p);
					temp.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
					
					data.add(temp);
				}
			}
		}
		
		if(stemming) {
			SnowballStemmer snowballStemmer = new englishStemmer();

			for(List<String> l : data) {
				for(int i=0; i<l.size(); i++) {
					snowballStemmer.setCurrent(l.get(i));
			        snowballStemmer.stem();
					l.set(i, snowballStemmer.getCurrent());
				}
			}
		}

		return data;
	}

}
