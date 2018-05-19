package derek.project;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import derek.project.algorithm.RemovalStopwords;
import derek.project.config.AppConfig;
import derek.project.dao.ApiMethodDao;
import derek.project.dao.RequestParameterDao;
import derek.project.model.ApiMethod;
import derek.project.model.RequestParameter;
import javassist.bytecode.Descriptor.Iterator;

public class DocumentGroup {

	private final static String fileName = "document.csv";

	public static void main(String[] args) {
		List<List<String>> data = getDocumentGroup(true);
		writeDocument(data);
		// readDocument();

		/*
		 * String[] arr; for(ApiMethod m : list) { arr = m.getMethod().split(" ");
		 * for(int i=0; i<arr.length; i++) { if(arr[i].length() < 2) {
		 * out.println(m.getMethod()); } } }
		 */
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	public static List<List<String>> getDocumentGroup(boolean stemming) {
		Map<String, List<?>> map = getDataFromDB();

		List<List<String>> data = new ArrayList<>();

		List<String> temp;
		Predicate<String> p = word -> word.length() < 2;

		String[] arr;
		for (ApiMethod m : (List<ApiMethod>) map.get("ApiMethod")) {
			arr = Optional.ofNullable(m.getDescription()).orElseGet(() -> " ").split(" ");

			if (arr.length > 1) {
				temp = new ArrayList<>(Arrays.asList(arr));
				temp.removeIf(p);
				temp.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
				
				data.add(temp);
			}
		}

		for (RequestParameter m : (List<RequestParameter>) map.get("RequestParameter")) {
			arr = Optional.ofNullable(m.getDescription()).orElseGet(() -> " ").split(" ");

			if (arr.length > 1) {
				temp = new ArrayList<>(Arrays.asList(arr));
				temp.removeIf(p);
				temp.removeIf(RemovalStopwords.predicateForStopwordsRemoval);
				
				data.add(temp);
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

	public static void writeDocument(List<List<String>> list) {
		try (BufferedWriter bw = new BufferedWriter(
				new FileWriter(new ClassPathResource(fileName).getFile().getAbsolutePath()))) {
			for (List<String> arr : list) {
				for (String str : arr) {
					bw.write(str + ",");
				}
				bw.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readDocument() {
		String line = "";
		final String identifier = ",";
		String[] arr;

		try (BufferedReader br = new BufferedReader(
				new FileReader(new ClassPathResource(fileName).getFile().getAbsolutePath()))) {
			while ((line = br.readLine()) != null) {
				arr = line.split(identifier);
				out.print(arr.length);
				/*
				 * for (int i = 0; i < arr.length; i++) { out.print(arr[i]); }
				 */
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
