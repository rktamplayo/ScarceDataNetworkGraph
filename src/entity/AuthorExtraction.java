package entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import abner.Tagger;

public class AuthorExtraction {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers");
		Tagger tagger = new Tagger("model/authorER.crf");
		tagger.setTokenization(false);
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/ref-authors-divided.txt"));
			}
			catch(Exception e) {
				continue;
			}
			String ref = "";
			PrintWriter printer = new PrintWriter(new FileWriter(file.getPath() + "/author-expanded.txt"));
			while(s.hasNextLine()) {
				String line = s.nextLine();
				if(line.isEmpty()) {
					String tagged = tagger.tagABNER(ref);
					String[] split = tagged.split("\n");
					for(int i=0; i<split.length; i++) {
						String[] split2 = split[i].split(" ");
						if(split2[1].equals("B-N")) {
							String name = "";
							for(int j=i; j<split.length; j++) {
								String[] split3 = split[j].split(" ");
								if(split3[1].equals("O")) {
									i = j;
									break;
								}
								name += split3[0] + " ";
							}
							if(name.contains(",")) {
								String[] split3 = name.split(",");
								name = split3[1].trim() + " " + split3[0].trim();
							}
							name = name.trim();
							name = name.replaceAll("[^a-zA-Z ]", "");
							name = name.replaceAll(" +", " ");
							char last = name.charAt(name.length()-1);
							if(last >= 'A' && last <= 'Z')
								name = name.substring(name.indexOf(" ")+1) + " " + name.substring(0, name.indexOf(" "));
							String firstname = name.substring(0, name.lastIndexOf(" "));
							String lastname = name.substring(name.lastIndexOf(" ") + 1);
							firstname = firstname.replaceAll("[^A-Z]", "");
							name = firstname + " " + lastname;
							name = name.toUpperCase();
							printer.print(name + "\t");
							System.out.print(name + " ");
						}
					}
					printer.println();
					System.out.println();
					ref = "";
				}
				ref += line + "\n";
			}
			s.close();
			printer.close();
		}
	}

}
