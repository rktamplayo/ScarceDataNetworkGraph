package graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import util.Pair;
import util.SortedPair;

public class TopicCitation {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers/");
		TreeMap<Pair, Integer> graph = new TreeMap<Pair, Integer>();
		TreeMap<String, Integer> topicIndex = new TreeMap<String, Integer>();
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/abstract-topics.txt"));
			}
			catch(Exception e) {
				continue;
			}
			int a = s.nextInt();
			int b = s.nextInt();
			s.close();
			try {
				s = new Scanner(new FileReader(file.getPath() + "/cite-sentences-topics.txt"));
			}
			catch(Exception e) {
				continue;
			}
			while(s.hasNext()) {
				int c = s.nextInt();
				Pair ac = new Pair(a, c);
				if(!graph.containsKey(ac)) graph.put(ac, 0);
				graph.put(ac, graph.get(ac) + 1);
				Pair bc = new Pair(b, c);
				if(!graph.containsKey(bc)) graph.put(bc, 0);
				graph.put(bc, graph.get(bc) + 1);
			}
			s.close();
		}
		Scanner s = new Scanner(new FileReader("data/topics.txt"));
		while(s.hasNext()) {
			int idx = s.nextInt();
			String line = s.nextLine();
			Scanner in = new Scanner(line);
			String topic = in.next() + " " + in.next() + " " + in.next();
			topicIndex.put(topic, idx);
		}
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/topic-citation-edges.txt"));
		printer.println("Source\tTarget\tWeight");
		for(Map.Entry<Pair, Integer> entry : graph.entrySet()) {
			Pair p = entry.getKey();
			printer.println(p.a + "\t" + p.b + "\t" + entry.getValue());
		}
		printer.close();
		printer = new PrintWriter(new FileWriter("data/graph/topic-citation-nodes.txt"));
		printer.println("Id\tLabel");
		for(Map.Entry<String, Integer> entry : topicIndex.entrySet())
			printer.println(entry.getValue() + "\t" + entry.getKey());
		printer.close();
	}

}
