package data;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

public class CombinePMCId {

	public static void main(String[] args) throws Exception {
		File dir = new File("data/search result");
		HashSet<String> set = new HashSet<String>();
		for(File file : dir.listFiles()) {
			Scanner s = new Scanner(file);
			System.out.println(file.getName());
			while(s.hasNext())
				set.add(s.nextLine());
			s.close();
		}
		PrintWriter printer = new PrintWriter(new FileWriter("data/search_result.txt"));
		for(String str : set)
			printer.println(str);
		printer.close();
	}

}
