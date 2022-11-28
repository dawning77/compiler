package middle.func;

import middle.ir.*;

import java.util.*;

public class BasicBlock{
	public final int id;
	public final ArrayList<ICode> iCodes;

	public HashSet<BasicBlock> next;    // logical next
	public HashSet<BasicBlock> prev;    // logical prev

	public BasicBlock(int id){
		this.id = id;
		this.iCodes = new ArrayList<>();
		this.next = new HashSet<>();
		this.prev = new HashSet<>();
	}

	public void add(ICode iCode){
		iCodes.add(iCode);
	}

	@Override
	public String toString(){ return "b" + id; }

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		BasicBlock that = (BasicBlock)o;
		return id == that.id;
	}

	@Override
	public int hashCode(){ return Objects.hash(id); }
}