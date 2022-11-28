package middle;

import java.util.*;

public class Utils{
	public static boolean isPowerOf2(int x){ return (x > 0 && (x & (x - 1)) == 0); }

	public static int log2I(int x){ return Integer.toBinaryString(x).length() - 1; }

	public static double log2D(int x){ return Math.log(x)/Math.log(2);}

	public static <T> HashSet<T> union(HashSet<T> a, HashSet<T> b) {
		HashSet<T> ret = new HashSet<>(a);
		ret.addAll(b);
		return ret;
	}

	public static <T> HashSet<T> except(HashSet<T> a, HashSet<T> b) {
		HashSet<T> ret = new HashSet<>(a);
		ret.removeAll(b);
		return ret;
	}

	public static <T> boolean isSame(HashSet<T> a, HashSet<T> b) {
		if (a.size() != b.size()) return false;
		for (T t : a) {
			if (!b.contains(t)) return false;
		}
		return true;
	}

	public static <T> HashSet<T> intersect(HashSet<T> a, HashSet<T> b) {
		HashSet<T> ret = new HashSet<>(a);
		for (T t : a) {
			if (!b.contains(t)) ret.remove(t);
		}
		return ret;
	}
}