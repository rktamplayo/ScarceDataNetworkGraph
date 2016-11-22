package graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import util.Pair;
import util.SortedPair;
import edu.stanford.nlp.simple.Sentence;

public class KeywordCooccurrence {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers/");
		TreeMap<Pair, Integer> graph = new TreeMap<Pair, Integer>();
		TreeMap<String, Integer> keywordIndex = new TreeMap<String, Integer>();
		int index = 0;
		for(File file : dir.listFiles()) {
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/abstract-keywords.txt"));
			}
			catch(Exception e) {
				continue;
			}
			Vector<String> abs_list = new Vector<String>();
			String[] split = s.nextLine().split("\t");
			for(String str : split) {
				String cleaned = clean(str.split("\\|")[0]);
				if(cleaned.isEmpty()) continue;
				abs_list.add(cleaned);
			}
			s.close();
			for(int i=0; i<abs_list.size(); i++)
				for(int j=i+1; j<abs_list.size(); j++) {
					String a = abs_list.get(i);
					String b = abs_list.get(j);
					if(!keywordIndex.containsKey(a)) keywordIndex.put(a, index++);
					if(!keywordIndex.containsKey(b)) keywordIndex.put(b, index++);
					SortedPair p = new SortedPair(keywordIndex.get(a), keywordIndex.get(b));
					if(!graph.containsKey(p)) graph.put(p, 0);
					graph.put(p, graph.get(p)+1);
				}
		}
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/keyword-cooccur-edges.txt"));
		printer.println("Source\tTarget\tWeight");
		for(Map.Entry<Pair, Integer> entry : graph.entrySet()) {
			Pair p = entry.getKey();
			printer.println(p.a + "\t" + p.b + "\t" + entry.getValue());
		}
		printer.close();
		printer = new PrintWriter(new FileWriter("data/graph/keyword-cooccur-nodes.txt"));
		printer.println("Id\tLabel");
		for(Map.Entry<String, Integer> entry : keywordIndex.entrySet())
			printer.println(entry.getValue() + "\t" + entry.getKey());
		printer.close();
	}
	
	public static String clean(String keyword) {
		keyword = keyword.toLowerCase();
		keyword = keyword.replaceAll("\\[.+\\]", "");
		keyword = keyword.replaceAll("\\[\\S+$", "");
		keyword = keyword.replaceAll("\\s+", " ");
		keyword = keyword.replaceAll("[^a-zA-Z0-9 ]", "");
		keyword = keyword.replaceAll("\\s+", " ");
		keyword = keyword.replaceAll("\\[|\\]", "");
		keyword = keyword.trim();
		if(keyword.length() < 3) return "";
		String[] words = keyword.split(" ");
		for(int i=0; i<words.length; i++) {
			try {
				int test = Integer.parseInt(words[i]);
			}
			catch(Exception e) {
				break;
			}
			words[i] = "";
		}
		keyword = "";
		for(int i=0; i<words.length; i++)
			if(!words[i].isEmpty()) keyword += words[i] + " ";
		keyword = keyword.trim();
		if(keyword.length() < 3) return "";
		if(words.length > 1 && keyword.length() < words.length*2-1 + words.length/2)
			return "";
		Sentence sent = new Sentence(keyword);
		List<String> posTags = sent.posTags();
		String rebuild = "";
		for(int i=0; i<sent.length(); i++)
			if(!posTags.get(i).startsWith("V"))
				rebuild += sent.lemma(i) + " ";
		rebuild = rebuild.trim();
		return rebuild;
	}

}
