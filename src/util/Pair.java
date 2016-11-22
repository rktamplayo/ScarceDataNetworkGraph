package util;

public class Pair implements Comparable<Pair> {
	
	public int a, b;
	
	public Pair(int c, int d) {
		a = c;
		b = d;
	}
	
	public int compareTo(Pair p) {
		if(a != p.a) return a - p.a;
		else return b - p.b;
	}
	
	public String toString() {
		return a + "," + b;
	}
}