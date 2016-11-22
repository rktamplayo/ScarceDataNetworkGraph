package calc;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.project.api.ProjectController;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

public class CommunityDetection {
	
	public static void main(String[] args) throws Exception {
		String type = "author-cooccur";
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		
		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
		UndirectedGraph graph = graphModel.getUndirectedGraph();
		Scanner s = new Scanner(new FileReader("data/graph/" + type + "-nodes.txt"));
		s.nextLine();
		HashMap<Integer, Node> map = new HashMap<Integer, Node>();
		while(s.hasNext()) {
			String[] spl = s.nextLine().split("\t");
			int idx = Integer.parseInt(spl[0]);
			String n = spl[1];
			Node node = graphModel.factory().newNode(""+idx);
			node.setLabel(n);
			map.put(idx, node);
			graph.addNode(node);
		}
		s.close();
		s = new Scanner(new FileReader("data/graph/" + type + "-edges.txt"));
		s.nextLine();
		while(s.hasNext()) {
			String[] spl = s.nextLine().split("\t");
			int a = Integer.parseInt(spl[0]);
			int b = Integer.parseInt(spl[1]);
			int w = Integer.parseInt(spl[2]);
			Edge edge = graphModel.factory().newEdge(map.get(a), map.get(b), w, false);
			graph.addEdge(edge);
		}
		s.close();
		Modularity modularity = new Modularity();
		modularity.setUseWeight(true);
		modularity.setResolution(1);
		modularity.setRandom(true);
		modularity.execute(graphModel);
		
		PrintWriter printer  = new PrintWriter(new FileWriter("data/graph/" + type + "-modularity.txt"));
		for(Node node : graph.getNodes()) {
			printer.println(node.getId() + "\t" + node.getLabel() + "\t" + node.getAttribute(Modularity.MODULARITY_CLASS));
		}
		printer.close();
	}

}
