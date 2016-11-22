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

import edu.stanford.nlp.simple.Sentence;
import util.Pair;

public class KeywordCitation {
	
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
			try {
				s = new Scanner(new FileReader(file.getPath() + "/cite-sentences-keywords.txt"));
			}
			catch(Exception e) {
				continue;
			}
			while(s.hasNext()) {
				Vector<String> list = new Vector<String>();
				String[] split2 = s.nextLine().split("\t");
				for(String str : split2) {
					String cleaned = clean(str.split("\\|")[0]);
					if(cleaned.isEmpty()) continue;
					list.add(cleaned);
				}
				for(int i=0; i<abs_list.size(); i++)
					for(int j=0; j<list.size(); j++) {
						String a = abs_list.get(i);
						String b = list.get(j);
						if(!keywordIndex.containsKey(a)) keywordIndex.put(a, index++);
						if(!keywordIndex.containsKey(b)) keywordIndex.put(b, index++);
						int idx1 = keywordIndex.get(a);
						int idx2 = keywordIndex.get(b);
						Pair p = new Pair(idx1, idx2);
						if(!graph.containsKey(p)) graph.put(p, 0);
						graph.put(p, graph.get(p) + 1);
					}
			}
			s.close();
		}
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/keyword-citation-edges.txt"));
		printer.println("Source\tTarget\tWeight");
		for(Map.Entry<Pair, Integer> entry : graph.entrySet()) {
			Pair p = entry.getKey();
			printer.println(p.a + "\t" + p.b + "\t" + entry.getValue());
		}
		printer.close();
		printer = new PrintWriter(new FileWriter("data/graph/keyword-citation-nodes.txt"));
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
