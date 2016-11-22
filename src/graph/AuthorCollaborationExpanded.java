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

import util.SortedPair;

public class AuthorCollaborationExpanded {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers");
		TreeMap<SortedPair, Integer> graph = new TreeMap<SortedPair, Integer>();
		TreeMap<String, Integer> authorIndex = new TreeMap<String, Integer>();
		int index = 0;
		for(File file : dir.listFiles()) {
			Scanner s;
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
				for(int i=0; i<authors.size(); i++)
					for(int j=i+1; j<authors.size(); j++) {
						String a = authors.get(i);
						a = a.replaceAll("[^A-Z ]", "").trim();
						if(a.trim().isEmpty()) continue;
						String b = authors.get(j);
						b = b.replaceAll("[^A-Z ]", "").trim();
						if(b.trim().isEmpty()) continue;
						if(!authorIndex.containsKey(a)) authorIndex.put(a, index++);
						if(!authorIndex.containsKey(b)) authorIndex.put(b, index++);
						int aidx = authorIndex.get(a);
						int bidx = authorIndex.get(b);
						SortedPair p = new SortedPair(aidx, bidx);
						if(!graph.containsKey(p)) graph.put(p, 0);
						graph.put(p, graph.get(p)+1);
					}
			}
			s.close();
		}
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/author-exp-edges.txt"));
		printer.println("Source\tTarget\tWeight");
		HashSet<Integer> set = new HashSet<Integer>();
		for(Map.Entry<SortedPair, Integer> entry : graph.entrySet()) {
			SortedPair p = entry.getKey();
			set.add(p.a);
			set.add(p.b);
			printer.println(p.a + "\t" + p.b + "\t" + entry.getValue());
		}
		printer.close();
		printer = new PrintWriter(new FileWriter("data/graph/author-exp-nodes.txt"));
		printer.println("Id\tLabel");
		for(Map.Entry<String, Integer> entry : authorIndex.entrySet())
			if(set.contains(entry.getValue()))
				printer.println(entry.getValue() + "\t" + entry.getKey());
		printer.close();
	}

}
