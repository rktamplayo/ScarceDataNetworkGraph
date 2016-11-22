package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PMCCrawler {

	public static void main(String[] args) throws Exception {
		Vector<String> pmcid_list = new Vector<String>();
		Scanner s = new Scanner(new FileReader("data/search_result.txt"));
		while(s.hasNext())
			pmcid_list.add(s.nextLine());
		s.close();
		Vector<String> problems = new Vector<String>();
		for(String pmcid : pmcid_list) {
			File file = new File("data/papers/" + pmcid + "/");
			file.mkdirs();
			System.out.println(pmcid);
			String url;
			Document doc;
			while(true) {
				try {
					url = "http://www.ncbi.nlm.nih.gov/pmc/articles/" + pmcid + "/";
					doc = Jsoup.connect(url)
							.timeout(0)
							.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
						    .get();
				}
				catch(Exception e) {
					e.printStackTrace();
					continue;
				}
				break;
			}
			PrintWriter printer;
			Elements authors;
			try {
				authors = doc.getElementsByClass("fm-author").get(0).
								getElementsByTag("a");
			}
			catch(Exception e) {
				problems.add(pmcid);
				continue;
			}
			
			printer = new PrintWriter(new FileWriter(file.getPath() + "/authors.txt"));	
			for(Element author : authors) {
				String str = author.text();
				if(str.contains(",")) {
					String first = str.split(",")[1].trim();
					String last = str.split(",")[0].trim();
					first = first.replaceAll("[^A-Z]", "");
					printer.println(first + " " + last.toUpperCase());
				}
				else {
					String[] split = str.split(" ");
					String last = split[split.length-1];
					String first = "";
					for(int i=0; i<split.length-1; i++)
						first += split[i].replaceAll("[^A-Z]", "");
					printer.println(first + " " + last.toUpperCase());
				}
			}
			printer.close();
			
			Elements sections = doc.getElementsByClass("tsec");
			if(sections.size() == 0) {
				problems.add(pmcid);
				continue;
			}
			printer = new PrintWriter(new FileWriter(file.getPath() + "/abstract.txt"));
			printer.println(sections.get(0).text());
			printer.close();
			
			Elements ref_list;
			try {
				ref_list = doc.getElementById("reference-list").children();
			}
			catch(Exception e) {
				problems.add(pmcid);
				continue;
			}
			if(ref_list.size() == 1) ref_list = ref_list.get(0).children();
			TreeMap<String, Vector<String>> ref_ids = new TreeMap<String, Vector<String>>();
			printer = new PrintWriter(new FileWriter(file.getPath() + "/ref-authors.txt"));
			for(Element ref : ref_list) {
				ref_ids.put(ref.attr("id"), new Vector<String>());
				printer.println(ref.text());
			}
			printer.close();
			
			String text = "";
			for(int i=1; i<sections.size()-1; i++) {
				Element section = sections.get(i);
				text += section.html() + " ";
			}
			text = text.replaceAll(">", "> ");
			text = text.replaceAll("<", " <");
			String[] tokens = text.split("\\s+");
			int[] indices = new int[tokens.length];
			int start = 0;
			for(int i=0; i<tokens.length; i++) {
				String token = tokens[i];
				indices[i] = text.indexOf(token, start);
				start = indices[i] + token.length();
			}
			int window = 30;
			for(String id : ref_ids.keySet()) {
				String find = "rid=\"" + id + "\"";
				int st = 0;
				Vector<String> list = new Vector<String>();
				while(true) {
					int index = text.indexOf(find, st);
					if(index < 0) break;
					int arr_idx = Arrays.binarySearch(indices, index);
					if(arr_idx < 0) arr_idx = -arr_idx-1;
					st = index + find.length() + 1;
					String context = "";
					int count = 0;
					for(int i=arr_idx-1; i>=0; i--) {
						String token = tokens[i];
						if(token.contains(">") || i == arr_idx-1) {
							while(true) {
								token = tokens[i];
								if(token.contains("<")) break;
								i--;
							}
							continue;
						}
						token = token.trim();
						if(token.isEmpty()) continue;
						context = token + " " + context;
						count++;
						if(count == window) break;
					}
					context = context.trim();
					count = 0;
					for(int i=arr_idx+1; i<tokens.length; i++) {
						String token = tokens[i];
						if(token.contains("<") || i == arr_idx+1) {
							while(true) {
								token = tokens[i];
								if(token.contains(">")) break;
								i++;
							}
							continue;
						}
						token = token.trim();
						if(token.isEmpty()) continue;
						context = context + " " + token;
						count++;
						if(count == window) break;
					}
					list.add(context);
				}
				ref_ids.put(id, list);
			}
			printer = new PrintWriter(new FileWriter(file.getPath() + "/cite-sentences.txt"));
			for(Map.Entry<String, Vector<String>> entry : ref_ids.entrySet()) {
				Vector<String> vals = entry.getValue();
				for(String val : vals)
					printer.println(entry.getKey() + "\t" + val);
			}
			printer.close();
 		}
		System.out.println(problems);
	}

}