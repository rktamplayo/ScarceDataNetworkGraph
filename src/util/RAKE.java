package util;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Java Port of RAKE (Rapid Automatic Keyword Extraction algorithm)
 * implementation in python at (https://github.com/aneesha/RAKE).
 *
 * Author: AskDrCatcher
 * License: MIT
 */
public class RAKE {

    public boolean isNumber(final String str) {
        return str.matches("[0-9.]");
    }

    public List<String> loadStopWords(String filePath) throws FileNotFoundException, IOException {

        if (filePath == null || filePath.trim().length() == 0) {
            filePath = "FoxStoplist.txt";
        }

        final List<String> stopWords = new ArrayList<String>();
        final BufferedReader br = new BufferedReader(new FileReader(filePath));

        try {

            String line = br.readLine();

            while (line != null) {

                if (!line.startsWith("#")) { //add the line which is not a comment
                    stopWords.add(line);
                }

                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return stopWords;
    }

    public List<String> separateWords(final String text, final int minimumWordReturnSize) {

        final List<String> separateWords = new ArrayList<String>();
        final String[] words = text.split("[^a-zA-Z0-9_\\+\\-/]");

        if (words != null && words.length > 0) {

            for (final String word : words) {

                String wordLowerCase = word.trim().toLowerCase();

                if (wordLowerCase.length() > 0 && wordLowerCase.length() > minimumWordReturnSize &&
                        !isNumber(wordLowerCase)) {

                    separateWords.add(wordLowerCase);
                }
            }
        }

        return separateWords;
    }


    public List<String> splitSentences(final String text) {

        final String[] sentences = text.split("[.!?,;:\\t\\\\-\\\\\"\\\\(\\\\)\\\\\\'\\u2019\\u2013]");
        if (sentences != null) {
            return new ArrayList<String>(Arrays.asList(sentences));
        } else {
            return new ArrayList<String>();
        }
    }

    public Pattern buildStopWordRegex(final String stopWordFilePath) throws IOException {

        final List<String> stopWords = loadStopWords(stopWordFilePath);
        final StringBuilder stopWordPatternBuilder = new StringBuilder();
        int count = 0;
        for(final String stopWord: stopWords) {
            if (count++ != 0) {
                stopWordPatternBuilder.append("|");
            }
            stopWordPatternBuilder.append("\\b").append(stopWord).append("\\b");
        }

        return Pattern.compile(stopWordPatternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    public List<String> generateCandidateKeywords(List<String> sentenceList, Pattern stopWordPattern) {
        final List<String> phraseList = new ArrayList<String>();

        for (final String sentence : sentenceList) {

            final String sentenceWithoutStopWord = stopWordPattern.matcher(sentence).replaceAll("|");
            final String[] phrases = sentenceWithoutStopWord.split("\\|");

            if (null != phrases && phrases.length > 0) {
                for(final String phrase : phrases) {
                    if (phrase.trim().toLowerCase().length() > 0) {
                        phraseList.add(phrase.trim().toLowerCase());
                    }
                }
            }
        }

        return phraseList;
    }

    public Map<String,Double> calculateWordScores(List<String> phraseList) {

        final Map<String, Integer> wordFrequency = new HashMap<String, Integer>();
        final Map<String, Integer> wordDegree = new HashMap<String, Integer>();
        final Map<String, Double> wordScore = new HashMap<String, Double>();

        for (final String phrase : phraseList) {

            final List<String> wordList = separateWords(phrase, 0);
            final int wordListLength = wordList.size();
            final int wordListDegree = wordListLength - 1;

            for (final String word : wordList) {

               if (!wordFrequency.containsKey(word)) {
                   wordFrequency.put(word, 0);
               }

               if (!wordDegree.containsKey(word)) {
                   wordDegree.put(word, 0);
               }

               wordFrequency.put(word, wordFrequency.get(word) + 1);
               wordDegree.put(word, wordDegree.get(word) + wordListDegree);
            }
        }

        final Iterator<String> wordIterator = wordFrequency.keySet().iterator();

        while (wordIterator.hasNext()) {
            final String word = wordIterator.next();

            wordDegree.put(word, wordDegree.get(word) + wordFrequency.get(word));

            if (!wordScore.containsKey(word)) {
                wordScore.put(word, 0.0);
            }

            wordScore.put(word, wordDegree.get(word) / (wordFrequency.get(word) * 1.0));
        }

        return wordScore;
    }

    public Map<String, Double> generateCandidateKeywordScores(List<String> phraseList,
                                                               Map<String, Double> wordScore) {

        final Map<String, Double> keyWordCandidates = new HashMap<String, Double>();

        for (String phrase : phraseList) {

            final List<String> wordList = separateWords(phrase, 0);
            double candidateScore = 0;

            for (final String word : wordList) {
                candidateScore += wordScore.get(word);
            }

            keyWordCandidates.put(phrase, candidateScore);
        }

        return keyWordCandidates;
    }

    public static void main(String[] args) throws IOException {

        //String text = "model in computer science Such applications of physical models nowadays are not unusual, for example Boltzmann machines and other methods came to area of artificial neural networks from statistical physics [1, 2]. It is also useful to draw some analogy with fuzzy sets and logic [3]. Here is used discrete representation of 2D sets on some lattice due to understanding analogy with ";
//        String text = "notation p[x] for the probabilities. Now let us introduce notion of quantum set (it is not standard term, mathematical object discussed further corresponds to 2n-qubit register in quantum information science [6] or quantum mechanical system with N2 states [7]) 4) Q qu set array [dot] of complex; Where complex is q u i w, q 1. Instead of standardization condition here ";
//    	String text = "independent useful application. Let us consider two sets p1[x], p2[x] and look for some likelihood function H(p1, p2) with properties H(p1, p2) 1 for p1 6 p2, H(p, p) 1 [1, 3]. For standardized sets such function can be chosen as H(p1, p2) x (p1[x]p2[x]) 1 2. If we use quantum sets q1, q2 (p1 q 2 1 , p2 q 2 ";
//    	String text = "The name of John von Neumann is common both in quantum mechanics and computer science. Are they really two absolutely unconnected areas? Many works devoted to quantum computations and communications are serious argument to suggest about existence of such a relation, but it is impossible to touch the new and active theme in a short review. In the paper are described the structures and models of linear algebra and just due to their generality it is possible to use universal description of very different areas as quantum mechanics and theory of Bayesian image analysis, associative memory, neural networks, fuzzy logic.";
    	String text = " We present a non-vacuous definition of compositionality. It is based on the idea of combining the minimum description length principle with the original definition of compositionality (that is, that the meaning of the whole is a function of the meaning of the parts).   The new definition is intuitive and allows us to distinguish between compositional and non-compositional semantics, and between idiomatic and non-idiomatic expressions. It is not ad hoc, since it does not make any references to non-intrinsic properties of meaning functions (like being a polynomial). Moreover, it allows us to compare different meaning functions with respect to how compositional they are. It bridges linguistic and corpus-based, statistical approaches to natural language understanding. ";
//    	String text = "The multiplicative Newton-like method developed by the author et al. is extended to the situation where the dynamics is restricted to the orthogonal group. A general framework is constructed without specifying the cost function. Though the restriction to the orthogonal groups makes the problem somewhat complicated, an explicit expression for the amount of individual jumps is obtained. This algorithm is exactly second-order-convergent. The global instability inherent in the Newton method is remedied by a Levenberg-Marquardt-type variation. The method thus constructed can readily be applied to the independent component analysis. Its remarkable performance is illustrated by a numerical simulation."
//    	String text = "We must evaluate Pi s, their derivatives, and so on to determine the optimal solution. A robust estimation of these quantities is possibly not an easy task[B.W.Sliverman,1986, D.Cox,1985]. 4.2 Cumulant of fourth order The kurtosis of a random variable A is defined by (A) E(A4) (E(A2))2 3 . (4.4) The kurtosis is related to the cumulant of ";
//    	String text = " of the fourth order, Cum(4)(A) E(A4) 3(E(A2))2 , (4.5) by (A) Cum(4)(A) (E(A2))2 . (4.6) For prewhitened data the kurtosis equals the cumulant of the fourth order. As is wellknown[A.Hyva rinen,1997, T.Akuzawa & N.Murata,1999], we can grab independent components in many cases by seeking the maximum of the absolute values of the kurtoses. Our method is applicable by setting fi 2 ";
    	
    	final RAKE rakeInstance = new RAKE();

        final List<String> sentenceList = rakeInstance.splitSentences(text.replaceAll("-", ""));
        final String stopPath = "data/SmartStoplist.txt";
        final Pattern stopWordPattern = rakeInstance.buildStopWordRegex(stopPath);
        final List<String> phraseList = rakeInstance.generateCandidateKeywords(sentenceList, stopWordPattern);
        final Map<String, Double> wordScore = rakeInstance.calculateWordScores(phraseList);
        final Map<String, Double> keywordCandidates = rakeInstance.generateCandidateKeywordScores(phraseList, wordScore);

        System.out.println("keyWordCandidates = "+ keywordCandidates);

        System.out.println("sortedKeyWordCandidates = " +
                rakeInstance.sortKeyWordCandidates(keywordCandidates));
    }

    public LinkedHashMap<String, Double> sortKeyWordCandidates
            (Map<String,Double> keywordCandidates) {

        final LinkedHashMap<String, Double> sortedKeyWordCandidates = new LinkedHashMap<String, Double>();
        int totaKeyWordCandidates = keywordCandidates.size();
        final List<Map.Entry<String, Double>> keyWordCandidatesAsList =
                new LinkedList<Map.Entry<String, Double>>(keywordCandidates.entrySet());

        Collections.sort(keyWordCandidatesAsList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Double>)o2).getValue()
                        .compareTo(((Map.Entry<String, Double>)o1).getValue());
            }
        });

        totaKeyWordCandidates = totaKeyWordCandidates / 3;
        for(final Map.Entry<String, Double> entry : keyWordCandidatesAsList) {
            sortedKeyWordCandidates.put(entry.getKey(), entry.getValue());
            if (--totaKeyWordCandidates == 0) {
                break;
            }
        }

        return sortedKeyWordCandidates;
    }
}