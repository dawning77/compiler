package middle.optim;

import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.symbol.*;

import java.util.*;


public class LiveVarAnalyser{
	private final FuncScope func;
	private final HashMap<BasicBlock, HashSet<Symbol>> defBB;
	private final HashMap<BasicBlock, HashSet<Symbol>> useBB;
	private final HashMap<BasicBlock, HashSet<Symbol>> inBB;
	private final HashMap<BasicBlock, HashSet<Symbol>> outBB;

	private final HashMap<ICode, HashSet<Symbol>> defIR;
	private final HashMap<ICode, HashSet<Symbol>> useIR;
	private final HashMap<ICode, HashSet<Symbol>> inIR;
	private final HashMap<ICode, HashSet<Symbol>> outIR;

	public LiveVarAnalyser(FuncScope func){
		this.func = func;
		this.defBB = new HashMap<>();
		this.useBB = new HashMap<>();
		this.inBB = new HashMap<>();
		this.outBB = new HashMap<>();
		this.defIR = new HashMap<>();
		this.useIR = new HashMap<>();
		this.inIR = new HashMap<>();
		this.outIR = new HashMap<>();
	}

	// contains all vars and param arr/mat
	public void liveVarAnalyse(){
		// gen use and def for bb and ir
		for(BasicBlock bb: func.bbs){
			defBB.put(bb, new HashSet<>());
			useBB.put(bb, new HashSet<>());
			inBB.put(bb, new HashSet<>());
			outBB.put(bb, new HashSet<>());
			for(ICode iCode: bb.iCodes){
				defIR.put(iCode, new HashSet<>());
				useIR.put(iCode, new HashSet<>());
				inIR.put(iCode, new HashSet<>());
				outIR.put(iCode, new HashSet<>());
				iCode.use.forEach(sym->{
					if(!defBB.get(bb).contains(sym)) useBB.get(bb).add(sym);
					if(!defIR.get(iCode).contains(sym)) useIR.get(iCode).add(sym);
				});
				iCode.def.forEach(sym->{
					if(!useBB.get(bb).contains(sym)) defBB.get(bb).add(sym);
					if(!useIR.get(iCode).contains(sym)) defIR.get(iCode).add(sym);
				});
			}
		}
		// gen out and in for bb
		for(int i = func.bbs.size() - 1; i >= 0; i--){
			Queue<BasicBlock> q = new LinkedList<>();
			q.add(func.bbs.get(i));
			while(q.size() > 0){
				BasicBlock bb = q.poll();
				HashSet<Symbol> newIn = Utils.union(useBB.get(bb), Utils.except(outBB.get(bb), defBB.get(bb)));
				if(!Utils.isSame(inBB.get(bb), newIn)){
					inBB.put(bb, newIn);
					for(BasicBlock prev: bb.prev){
						HashSet<Symbol> newOut = Utils.union(outBB.get(prev), inBB.get(bb));
						if(!Utils.isSame(outBB.get(prev), newOut)){
							q.add(prev);
							outBB.put(prev, newOut);
						}
					}
				}
			}
		}
		// gen out and in for ir
		for(BasicBlock bb: func.bbs){
			for(int i = bb.iCodes.size() - 1; i >= 0; i--){
				ICode iCode = bb.iCodes.get(i);
				if(i == bb.iCodes.size() - 1) outIR.put(iCode, outBB.get(bb));
				else outIR.put(iCode, inIR.get(bb.iCodes.get(i + 1)));
				inIR.put(iCode, Utils.union(useIR.get(iCode), Utils.except(outIR.get(iCode), defIR.get(iCode))));
			}
		}
	}

	public String getOutput(ICode iCode){
		// for debugging
		return "----------LiveVar-----------" +
		       "\nuse: " + useIR.get(iCode) +
		       "\ndef: " + defIR.get(iCode) +
		       "\nin: " + inIR.get(iCode) +
		       "\nout: " + outIR.get(iCode) +
		       "\n----------------------------";
	}

	public boolean isLive(ICode iCode, Symbol sym){
		if(!inIR.containsKey(iCode)) return false;
		return Utils.union(useIR.get(iCode), outIR.get(iCode)).contains(sym);
	}
}