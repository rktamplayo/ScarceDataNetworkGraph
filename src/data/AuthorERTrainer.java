package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import abner.Trainer;

public class AuthorERTrainer {

	public static void main(String[] args) throws Exception {
		PrintWriter printer = new PrintWriter(new FileWriter("data/agg_train.txt"));
		File dir = new File("data/training");
		for(File file : dir.listFiles()) {
			System.out.println(file.getPath());
			Scanner s = new Scanner(new FileReader(file));
			while(s.hasNext())
				printer.println(s.nextLine());
			printer.println();
			s.close();
		}
		printer.close();
		
		String inputFile = "data/agg_train.txt";
		String modelFile = "model/authorER.crf";
		String[] tags = {"N"};
		
		Trainer trainer = new Trainer();
		trainer.train(inputFile, modelFile, tags);
	}
}
