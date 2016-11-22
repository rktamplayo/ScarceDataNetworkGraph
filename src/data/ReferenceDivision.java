package data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class ReferenceDivision {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers");
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/ref-authors.txt"));
			}
			catch(Exception e) {
				continue;
			}
			PrintWriter printer = new PrintWriter(new FileWriter(file.getPath() + "/ref-authors-divided.txt"));
			while(s.hasNext()) {
				String line = s.nextLine();
				for(int i=0; i<line.length(); i++) {
					if(Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '-') {
						String word = "";
						for(int j=i; j<line.length(); j++) {
							if(Character.isLetterOrDigit(line.charAt(j)) || line.charAt(j) == '-')
								word += line.charAt(j);
							else {
								i = j-1;
								break;
							}
							if(j == line.length()-1)
								i = j;
						}
						printer.println(word);
					}
					else if(Character.isWhitespace(line.charAt(i))) continue;
					else printer.println(line.charAt(i));
				}
				printer.println();
			}
			printer.close();
			s.close();
		}
	}

}
