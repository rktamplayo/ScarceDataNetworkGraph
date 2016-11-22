package entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import util.RAKE;

public class KeywordExtraction {
	
	public static void main(String[] args) throws Exception {
		RAKE rakeInstance = new RAKE();
		File dir = new File("data/papers");
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/abstract.txt"));
			}
			catch(Exception e) {
				continue;
			}
			String abs = "";
			while(s.hasNext())
				abs += s.nextLine();
			s.close();
			List<String> sentenceList = rakeInstance.splitSentences(abs.replaceAll("-", ""));
	        String stopPath = "data/SmartStoplist.txt";
	        Pattern stopWordPattern = rakeInstance.buildStopWordRegex(stopPath);
	        List<String> phraseList = rakeInstance.generateCandidateKeywords(sentenceList, stopWordPattern);
	        Map<String, Double> wordScore = rakeInstance.calculateWordScores(phraseList);
	        Map<String, Double> keywordCandidates = rakeInstance.generateCandidateKeywordScores(phraseList, wordScore);
	        Map<String, Double> sorted = rakeInstance.sortKeyWordCandidates(keywordCandidates);
	        
	        PrintWriter printer = new PrintWriter(new FileWriter(file.getPath() + "/abstract-keywords.txt"));
	        for(Map.Entry<String, Double> entry : sorted.entrySet())
	        	printer.print(entry.getKey() + "|" + entry.getValue() + "\t");
	        printer.println();
	        printer.close();
	        
	        try {
	        	s = new Scanner(new FileReader(file.getPath() + "/cite-sentences.txt"));
	        }
	        catch(Exception e) {
	        	continue;
	        }
	        printer = new PrintWriter(new FileWriter(file.getPath() + "/cite-sentences-keywords.txt"));
	        while(s.hasNext()) {
	        	String[] line = s.nextLine().split("\t");
	        	sentenceList = rakeInstance.splitSentences(line[1].replaceAll("-", ""));
		        stopPath = "data/SmartStoplist.txt";
		        stopWordPattern = rakeInstance.buildStopWordRegex(stopPath);
		        phraseList = rakeInstance.generateCandidateKeywords(sentenceList, stopWordPattern);
		        wordScore = rakeInstance.calculateWordScores(phraseList);
		        keywordCandidates = rakeInstance.generateCandidateKeywordScores(phraseList, wordScore);
		        sorted = rakeInstance.sortKeyWordCandidates(keywordCandidates);
		        
		        for(Map.Entry<String, Double> entry : sorted.entrySet())
		        	printer.print(entry.getKey() + "|" + entry.getValue() + "\t");
		        printer.println();   
	        }
	        printer.close();
	        s.close();
		}
	}

}
