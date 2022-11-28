package middle.func;

import middle.operand.symbol.*;
import middle.optim.*;

import java.util.*;

public class FuncScope{
	public final String name;
	public final String retType;
	public final ArrayList<BasicBlock> bbs;

	public final ArrayList<Symbol> params;
	public final ArrayList<Symbol> locals;
	public final ArrayList<Symbol> tmps;
	public boolean hasLastReturn;
	public int frameSize;
	public int paramSize;

	public ActiveChecker activeChecker;

	public FuncScope(String name, String retType){
		this.name = name;
		this.retType = retType;
		this.hasLastReturn = false;
		this.frameSize = 0;
		this.paramSize = 0;
		this.bbs = new ArrayList<>();
		this.params = new ArrayList<>();
		this.locals = new ArrayList<>();
		this.tmps = new ArrayList<>();
		this.activeChecker = new ActiveChecker(this);
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
