package util;

public class SortedPair extends Pair {

	public SortedPair(int c, int d) {
		super(c, d);
		a = Math.min(c, d);
		b = Math.max(c, d);
	}

}
