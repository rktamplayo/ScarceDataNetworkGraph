package util;

public class TopicProbPair implements Comparable<TopicProbPair> {
	
	private int topic;
	private double prob;
	
	public TopicProbPair(int topic, double prob) {
		this.topic = topic;
		this.prob = prob;
	}
	
	public int compareTo(TopicProbPair p) {
		if(prob != p.prob) return Double.compare(p.prob, prob);
		else return topic-p.topic;
	}
	
	public int getTopic() {
		return topic;
	}
	
	public double getProb() {
		return prob;
	}
}