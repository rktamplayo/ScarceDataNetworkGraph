package entity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import util.TopicProbPair;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class TopicExtraction {
	
	public static void main(String[] args) throws Exception {
		String data = "data";
		File dir = new File(data + "/papers");
		
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));
		pipeList.add(new TokenSequence2FeatureSequence());
		
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		Vector<String> list = new Vector<String>();
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/abstract.txt"));
			}
			catch(Exception e) {
				continue;
			}
			while(s.hasNext()) {
				String text = s.nextLine();
				String nouns = "";
				Annotation document = new Annotation(text);
				pipeline.annotate(document);
				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				for(CoreMap sentence : sentences) {
					List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
					for(CoreLabel token : tokens) {
						String pos = token.get(PartOfSpeechAnnotation.class);
						String lemma = token.get(LemmaAnnotation.class);
						if(pos.startsWith("NN"))
							nouns += lemma.toLowerCase() + " ";
					}
				}
				
				list.add(nouns);
			}
			s.close();
			
			try {
	        	s = new Scanner(new FileReader(file.getPath() + "/cite-sentences.txt"));
	        }
	        catch(Exception e) {
	        	continue;
	        }
			while(s.hasNext()) {
				String text = s.nextLine().split("\t")[1];
				
				String nouns = "";
				Annotation document = new Annotation(text);
				pipeline.annotate(document);
				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				for(CoreMap sentence : sentences) {
					List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
					for(CoreLabel token : tokens) {
						String pos = token.get(PartOfSpeechAnnotation.class);
						String lemma = token.get(LemmaAnnotation.class);
						if(pos.startsWith("NN"))
							nouns += lemma.toLowerCase() + " ";
					}
				}
				
				list.add(nouns);
			}
			
			s.close();
		}
		
		String[] array = new String[list.size()];
		for(int i=0; i<list.size(); i++) array[i] = list.get(i);
		
		instances.addThruPipe(new StringArrayIterator(array));
		
		ParallelTopicModel model = new ParallelTopicModel(500, 1.0, 0.01);
		
		model.addInstances(instances);
		model.setNumThreads(4);
		model.setNumIterations(5000);
		model.estimate();
		
		TopicInferencer inferencer = model.getInferencer();
		Alphabet dataAlphabet = model.getAlphabet();
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		PrintWriter printer = new PrintWriter(new FileWriter(data + "/topics.txt"));
		for (int topic = 0; topic < 500; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            
            printer.format("%d\t", topic);
            int rank = 0;
            while (iterator.hasNext() && rank < 20) {
                IDSorter idCountPair = iterator.next();
                printer.format("%s ", dataAlphabet.lookupObject(idCountPair.getID()));
                rank++;
            }
            printer.println();
        }
		printer.close();
		
		int doc = 0;
		for(File file : dir.listFiles()) {
			System.out.println(file.getName());
			Scanner s;
			try {
				s = new Scanner(new FileReader(file.getPath() + "/abstract.txt"));
			}
			catch(Exception e) {
				continue;
			}
			printer = new PrintWriter(new FileWriter(file.getPath() + "/abstract-topics.txt"));
			while(s.hasNext()) {
				String text = s.nextLine();
				Instance inst = instances.get(doc++);
				double[] testProbabilities = inferencer.getSampledDistribution(inst, 10, 1, 5);
				TopicProbPair[] pairs = new TopicProbPair[500];
				for(int k=0; k<500; k++)
					pairs[k] = new TopicProbPair(k, testProbabilities[k]);
				Arrays.sort(pairs);
				int topic1 = pairs[0].getTopic();
				int topic2 = pairs[1].getTopic();
				printer.println(topic1 + " " + topic2);
			}
			printer.close();
			s.close();
			
			try {
	        	s = new Scanner(new FileReader(file.getPath() + "/cite-sentences.txt"));
	        }
	        catch(Exception e) {
	        	continue;
	        }
			printer = new PrintWriter(new FileWriter(file.getPath() + "/cite-sentences-topics.txt"));
			while(s.hasNext()) {
				String text = s.nextLine().split("\t")[1];
				Instance inst = instances.get(doc++);
				double[] testProbabilities = inferencer.getSampledDistribution(inst, 10, 1, 5);
				TopicProbPair[] pairs = new TopicProbPair[500];
				for(int k=0; k<500; k++)
					pairs[k] = new TopicProbPair(k, testProbabilities[k]);
				Arrays.sort(pairs);
				int topic1 = pairs[0].getTopic();
				double prob = pairs[0].getProb();
				if(prob > 1.0/500)
					printer.println(topic1);
			}
			printer.close();
			s.close();
		}
	}

}
