package graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import util.Pair;
import util.SortedPair;

public class AuthorCollaborationTraditional {

	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers");
		TreeMap<SortedPair, Integer> graph = new TreeMap<SortedPair, Integer>();
		TreeMap<String, Integer> authorIndex = new TreeMap<String, Integer>();
		int index = 0;
		for(File file : dir.listFiles()) {
			File temp = new File(file.getPath() + "/authors.txt");
			if(!temp.exists()) continue;
			Scanner s = new Scanner(new FileReader(file.getPath() + "/authors.txt"));
			Vector<String> authors = new Vector<String>();
			int count = 0;
			while(s.hasNext()) {
				String str = s.nextLine();
				authors.add(str);
				count++;
				if(count == 10) {
					String last = "";
					while(s.hasNext())
						last = s.nextLine();
					if(!last.isEmpty())
						authors.add(last);
					break;
				}
			}
			s.close();
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
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/author-cooccur-edges.txt"));
		printer.println("Source\tTarget\tWeight");
		for(Map.Entry<SortedPair, Integer> entry : graph.entrySet()) {
			SortedPair p = entry.getKey();
			printer.println(p.a + "\t" + p.b + "\t" + entry.getValue());
		}
		printer.close();
		printer = new PrintWriter(new FileWriter("data/graph/author-cooccur-nodes.txt"));
		printer.println("Id\tLabel");
		for(Map.Entry<String, Integer> entry : authorIndex.entrySet())
			printer.println(entry.getValue() + "\t" + entry.getKey());
		printer.close();
	}

}
