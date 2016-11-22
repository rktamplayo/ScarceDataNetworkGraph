package graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import util.Pair;

public class AuthorCitation {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers");
		TreeMap<Pair, Integer> graph = new TreeMap<Pair, Integer>();
		TreeMap<String, Integer> authorIndex = new TreeMap<String, Integer>();
		int index = 0;
		for(File file : dir.listFiles()) {
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/authors.txt"));
			}
			catch(Exception e) {
				continue;
			}
			Vector<String> abs_list = new Vector<String>();
			int count = 0;
			while(s.hasNext()) {
				String str = s.nextLine();
				abs_list.add(str);
				count++;
				if(count == 10) {
					String last = "";
					while(s.hasNext())
						last = s.nextLine();
					if(!last.isEmpty())
						abs_list.add(last);
					break;
				}
			}
			s.close();
			try {
				s = new Scanner(new FileReader(file.getPath() + "/author-expanded.txt"));
			}
			catch(Exception e) {
				continue;
			}
			while(s.hasNext()) {
				String[] split = s.nextLine().split("\t");
				Vector<String> authors = new Vector<String>();
				for(int i=0; i<Math.min(split.length, 10); i++)
					authors.add(split[i]);
				if(split.length > 10) authors.add(split[split.length-1]);
				Collections.sort(authors);
				System.out.println(authors);
				for(int i=0; i<abs_list.size(); i++)
					for(int j=0; j<authors.size(); j++) {
						String a = abs_list.get(i);
						a = a.replaceAll("[^A-Z ]", "").trim();
						if(a.trim().isEmpty()) continue;
						String b = authors.get(j);
						b = b.replaceAll("[^A-Z ]", "").trim();
						if(b.trim().isEmpty()) continue;
						if(!authorIndex.containsKey(a)) authorIndex.put(a, index++);
						if(!authorIndex.containsKey(b)) authorIndex.put(b, index++);
						int aidx = authorIndex.get(a);
						int bidx = authorIndex.get(b);
						Pair p = new Pair(aidx, bidx);
						if(!graph.containsKey(p)) graph.put(p, 0);
						graph.put(p, graph.get(p)+1);
					}
			}
			s.close();
		}
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/author-citation-edges.txt"));
		printer.println("Source\tTarget\tWeight");
		HashSet<Integer> set = new HashSet<Integer>();
		for(Map.Entry<Pair, Integer> entry : graph.entrySet()) {
			Pair p = entry.getKey();
			set.add(p.a);
			set.add(p.b);
			printer.println(p.a + "\t" + p.b + "\t" + entry.getValue());
		}
		printer.close();
		printer = new PrintWriter(new FileWriter("data/graph/author-citation-nodes.txt"));
		printer.println("Id\tLabel");
		for(Map.Entry<String, Integer> entry : authorIndex.entrySet())
			if(set.contains(entry.getValue()))
				printer.println(entry.getValue() + "\t" + entry.getKey());
		printer.close();
	}

}
