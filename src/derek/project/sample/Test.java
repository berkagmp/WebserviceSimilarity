package derek.project.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import derek.project.algorithm.RemovalStopwords;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "<I> aMd a on for	^&* )( boy Freind $ #?$ _";
		str = str.replaceAll("\\W", " ").replaceAll("_", " ").trim(); // \W : A non-word character
		
		while(str.indexOf("  ") > -1) {
			str = str.replaceAll("  ", " ");
		}
		System.out.println(str);
		
		str = "SMS";
		
		System.out.println(str.replaceAll("SMS", "Sms"));
		System.out.println(str.split("[A-Z]+").length);
		System.out.println(str.replaceAll("([A-Z])", " $1").toLowerCase());
		
		List<String> list = new ArrayList<>(Arrays.asList(new String[] {"123", "1", "asfdsdf", "on", "for", "a", "", " "}));
		System.out.println("\r\nOriginal list");
		list.forEach(System.out::println);
		
		Predicate<String> p = word -> word.length() < 2 || word.equals("");
        list.removeIf(w -> w.length() < 2);
        System.out.println("\r\nAfter removeIf");
        System.out.println(list);
        
        Predicate<String> ps = word -> RemovalStopwords.stopwordList.contains(word);
        list.removeIf(ps);
        System.out.println("\r\nAfter removeIf for stop words");
        System.out.println(list);
        
        List<List<String>> llist = new ArrayList<List<String>>();
        llist.add(list);
        list = new ArrayList<String>();
        System.out.println("\r\n= List");
        list.add("MILK");
        list.add("BREAD");
        list.add("BUTTER");
        list.add(1, "APPLE"); //1번자리에 APPLE을 넣고 원래 1번에 있던 BREAD를 2번으로 밀어낸다.
        System.out.println(list);
 
        //set BREAD를 GRAPE로 대체
        list.set(2, "GRAPE");
        System.out.println(list);
        llist.add(list);
        
        System.out.println(llist.size());
        System.out.println(llist);
        
        for(List<String> l : llist) {
			for(int i=0; i<l.size(); i++) {
				l.set(i, l.get(i) + "@");
			}
		}
        System.out.println(llist);
        
        for(List<String> l : llist) {
			for(String s : l) {
				s += "#";
			}
		}
        System.out.println(llist);
	}

}