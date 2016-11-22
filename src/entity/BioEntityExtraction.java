package entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import abner.Tagger;

public class BioEntityExtraction {
	
	public static void main(String[] args) throws Exception {
		File dir = new File("data/papers");
		Tagger tagger = new Tagger();
		tagger.setTokenization(false);
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/abstract.txt"));
			}
			catch(Exception e) {
				continue;
			}
			PrintWriter printer = new PrintWriter(new FileWriter(file.getPath() + "/abstract-bio-entity.txt"));
			while(s.hasNextLine()) {
				String abs = s.nextLine();
				abs = abs.replaceAll("\\|", "");
				String tagged = tagger.tagABNER(abs);
				tagged = tagged.replaceAll("\\s+", " ");
				String[] split = tagged.split(" ");
				for(int i=0; i<split.length; i++) {
					String[] split2 = split[i].split("\\|");
					if(split2[1].startsWith("B-")) {
						String name = "";
						for(int j=i; j<split.length; j++) {
							String[] split3 = split[j].split("\\|");
							if(split3[1].equals("O")) {
								i = j;
								break;
							}
							if(!split3[1].split("-")[1].equals(split2[1].split("-")[1])) {
								i = j-1;
								break;
							}
							name += split3[0] + " ";
						}
						printer.print(name + "\t");
						System.out.println(name);
					}
				}
			}
			printer.println();
			s.close();
			printer.close();
			
			try {
				s = new Scanner(new FileReader(file.getPath() + "/cite-sentences.txt"));
			}
			catch(Exception e) {
				continue;
			}
			printer = new PrintWriter(new FileWriter(file.getPath() + "/cite-sentences-bio-entity.txt"));
			while(s.hasNext()) {
				String abs = s.nextLine();
				abs = abs.replaceAll("\\|", "");
				String tagged = tagger.tagABNER(abs);
				tagged = tagged.replaceAll("\\s+", " ");
				String[] split = tagged.split(" ");
				for(int i=0; i<split.length; i++) {
					String[] split2 = split[i].split("\\|");
					if(split2[1].startsWith("B-")) {
						String name = "";
						for(int j=i; j<split.length; j++) {
							String[] split3 = split[j].split("\\|");
							if(split3[1].equals("O")) {
								i = j;
								break;
							}
							if(!split3[1].split("-")[1].equals(split2[1].split("-")[1])) {
								i = j-1;
								break;
							}
							name += split3[0] + " ";
						}
						printer.print(name + "\t");
						System.out.println(name);
					}
				}
				printer.println();
			}
			s.close();
			printer.close();
		}
	}

}
