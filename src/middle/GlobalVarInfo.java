package middle;

import java.util.*;

public class GlobalVarInfo{
	public boolean initialized;
	public int size;
	public ArrayList<Integer> vals;

	public GlobalVarInfo(int size){
		this.initialized = false;
		this.size = size;
	}

	public GlobalVarInfo(int size, ArrayList<Integer> vals){
		this.initialized = true;
		this.size = size;
		this.vals = vals;
	}
}
