package middle;

import middle.ir.*;

import java.util.ArrayList;

public class BasicBlock{
	public final int id;
	public final ArrayList<ICode> iCodes;
	public BasicBlock follow;   // sequentially successive bb

	public BasicBlock(int id){
		this.id = id;
		this.iCodes = new ArrayList<>();
		this.follow = null;
	}

	public void add(ICode iCode){
		iCodes.add(iCode);
	}

	@Override
	public String toString(){ return "b" + id; }
}