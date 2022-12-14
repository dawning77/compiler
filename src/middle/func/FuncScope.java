package middle.func;

import middle.ir.*;
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

	public FuncOptimizer funcOptimizer;
	public LiveVarAnalyser liveVarAnalyser;

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
		this.funcOptimizer = new FuncOptimizer(this);
		this.liveVarAnalyser = new LiveVarAnalyser(this);
	}

	public void formFrame(){
		for(Symbol local: locals){
			local.loc = frameSize;
			frameSize += local.size;
		}
		for(Symbol tmp: tmps){
			tmp.loc = frameSize;
			frameSize += tmp.size;
		}
		for(Symbol param: params){
			param.loc = frameSize + paramSize;
			paramSize += 1;
		}
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(BasicBlock bb: bbs){
			sb.append(new Label(name)).append('\n');
			for(ICode iCode: bb.iCodes){ sb.append(iCode).append('\n'); }
		}
		sb.append('\n');
		return sb.toString();
	}
}
