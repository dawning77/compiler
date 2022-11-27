package middle;

import middle.operand.symbol.*;

import java.util.*;

public class FuncScope{
	public final String name;
	public final String retType;
	public final BasicBlock firstBB;

	public final ArrayList<Symbol> params;
	public final ArrayList<Symbol> locals;
	public final ArrayList<Symbol> tmps;
	public boolean hasLastReturn;
	public int frameSize;
	public int paramSize;

	public FuncScope(String name, String retType, BasicBlock firstBB){
		this.name = name;
		this.retType = retType;
		this.firstBB = firstBB;
		this.hasLastReturn = false;
		this.frameSize = 0;
		this.paramSize = 0;
		this.params = new ArrayList<>();
		this.locals = new ArrayList<>();
		this.tmps = new ArrayList<>();
	}

	public void formFrame(){
		for(Symbol tmp: tmps){
			tmp.loc = frameSize;
			frameSize += tmp.size;
		}
		for(Symbol local: locals){
			local.loc = frameSize;
			frameSize += local.size;
		}
		for(Symbol param: params){
			param.loc = frameSize + paramSize;
			paramSize += 1;
		}
	}
}
