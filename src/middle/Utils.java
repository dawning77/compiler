package middle;

public class Utils{
	public static boolean isPowerOf2(int x){ return (x > 0 && (x & (x - 1)) == 0); }

	public static int log2I(int x){ return Integer.toBinaryString(x).length() - 1; }

	public static double log2D(int x){ return Math.log(x)/Math.log(2);}
}