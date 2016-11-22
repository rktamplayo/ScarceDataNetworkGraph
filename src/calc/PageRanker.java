package calc;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class PageRanker {

	public static void main(String[] args) throws Exception {
		String type = "bio-entity-exp";
		UndirectedSparseGraph<Integer, Integer> graph = new UndirectedSparseGraph<Integer, Integer>();
		Scanner s = new Scanner(new FileReader("data/graph/" + type + "-nodes.txt"));
		s.nextLine();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		while(s.hasNext()) {
			String[] spl = s.nextLine().split("\t");
			int idx = Integer.parseInt(spl[0]);
			String node = spl[1];
			map.put(idx, node);
			graph.addVertex(idx);
		}
		s.close();
		s = new Scanner(new FileReader("data/graph/" + type + "-edges.txt"));
		s.nextLine();
		int index = 0;
		HashMap<Integer, Integer> weightMap = new HashMap<Integer, Integer>();
		while(s.hasNext()) {
			String[] spl = s.nextLine().split("\t");
			int a = Integer.parseInt(spl[0]);
			int b = Integer.parseInt(spl[1]);
			int w = Integer.parseInt(spl[2]);
			graph.addEdge(index, a, b);
			weightMap.put(index, w);
			index++;
		}
		s.close();
		Transformer edgeWeights = MapTransformer.getInstance(weightMap);
		PageRank<Integer, Integer> ranker = new PageRank<Integer, Integer>(graph, edgeWeights, 0.5);
		ranker.evaluate();
		PrintWriter printer = new PrintWriter(new FileWriter("data/graph/" + type + "-pagerank.txt"));
		for(Map.Entry<Integer, String> entry : map.entrySet()) {
			printer.println(entry.getValue() + "\t" + ranker.getVertexScore(entry.getKey()));
			System.out.println(entry.getValue() + "\t" + ranker.getVertexScore(entry.getKey()));
		}
		printer.close();
	}

}
