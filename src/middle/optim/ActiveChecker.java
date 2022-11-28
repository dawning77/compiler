package middle.optim;

import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.ir.br.*;
import middle.ir.calc.binary.*;
import middle.ir.calc.unary.*;
import middle.ir.func.*;
import middle.ir.io.*;
import middle.ir.mem.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;
import java.util.stream.*;

public class ActiveChecker{
	private final FuncScope func;
	private final HashMap<BasicBlock, HashSet<Var>> defBB;
	private final HashMap<BasicBlock, HashSet<Var>> useBB;
	private final HashMap<BasicBlock, HashSet<Var>> inBB;
	private final HashMap<BasicBlock, HashSet<Var>> outBB;

	private final HashMap<ICode, HashSet<Var>> defIR;
	private final HashMap<ICode, HashSet<Var>> useIR;
	private final HashMap<ICode, HashSet<Var>> inIR;
	private final HashMap<ICode, HashSet<Var>> outIR;

	private BasicBlock curBB;
	private ICode curIR;

	public ActiveChecker(FuncScope func){
		this.func = func;
		this.defBB = new HashMap<>();
		this.useBB = new HashMap<>();
		this.inBB = new HashMap<>();
		this.outBB = new HashMap<>();
		this.defIR = new HashMap<>();
		this.useIR = new HashMap<>();
		this.inIR = new HashMap<>();
		this.outIR = new HashMap<>();
		this.curBB = null;
		this.curIR = null;
	}

	public static final HashSet<Symbol.Type> CHECK =
			Stream.of(Symbol.Type.param, Symbol.Type.tmp, Symbol.Type.local)
					.collect(Collectors.toCollection(HashSet::new));    // global dont need to check

	private void addUse(Operand opd){
		if(opd instanceof Var && !(opd instanceof Const) && CHECK.contains(((Symbol)opd).type)){
			if(!defBB.get(curBB).contains((Var)opd)) useBB.get(curBB).add((Var)opd);
			if(!defIR.get(curIR).contains((Var)opd)) useIR.get(curIR).add((Var)opd);
		}
	}

	private void addDef(Operand opd){
		if(opd instanceof Var && !(opd instanceof Const) && CHECK.contains(((Symbol)opd).type)){
			if(!useBB.get(curBB).contains((Var)opd)) defBB.get(curBB).add((Var)opd);
			if(!useIR.get(curIR).contains((Var)opd)) defIR.get(curIR).add((Var)opd);
		}
	}

	// only contains param/local/tmp vars
	public void activeAnalyse(){
		// gen use and def for bb and ir
		for(BasicBlock bb: func.bbs){
			curBB = bb;
			defBB.put(bb, new HashSet<>());
			useBB.put(bb, new HashSet<>());
			inBB.put(bb, new HashSet<>());
			outBB.put(bb, new HashSet<>());
			for(ICode iCode: bb.iCodes){
				curIR = iCode;
				defIR.put(iCode, new HashSet<>());
				useIR.put(iCode, new HashSet<>());
				inIR.put(iCode, new HashSet<>());
				outIR.put(iCode, new HashSet<>());
				if(iCode instanceof Br) addUse(((Br)iCode).opd0);
				else if(iCode instanceof Binary){
					addUse(((Binary)iCode).opd0);
					addUse(((Binary)iCode).opd1);
					addDef(((Binary)iCode).res);
				}
				else if(iCode instanceof Unary){
					addUse(((Unary)iCode).opd0);
					addDef(((Unary)iCode).res);
				}
				else if(iCode instanceof GetRet || iCode instanceof Input){
					Operand res = iCode instanceof GetRet? ((GetRet)iCode).res: ((Input)iCode).res;
					addDef(res);
				}
				else if(iCode instanceof ParamDecl) ((ParamDecl)iCode).params.forEach(this::addDef);
				else if(iCode instanceof Push) ((Push)iCode).params.forEach(this::addUse);
				else if(iCode instanceof Ret || iCode instanceof OutputInt){
					Operand opd0 = iCode instanceof Ret? ((Ret)iCode).opd0: ((OutputInt)iCode).opd0;
					addUse(opd0);
				}
				else if(iCode instanceof Load){
					addUse(((Load)iCode).idx);
					addDef(((Load)iCode).val);
				}
				else if(iCode instanceof Store){
					addUse(((Store)iCode).idx);
					addUse(((Store)iCode).val);
				}
			}
		}
		// gen out and in for bb
		for(int i = func.bbs.size() - 1; i >= 0; i--){
			Queue<BasicBlock> q = new LinkedList<>();
			q.add(func.bbs.get(i));
			while(q.size() > 0){
				BasicBlock bb = q.poll();
				HashSet<Var> newIn = Utils.union(useBB.get(bb), Utils.except(outBB.get(bb), defBB.get(bb)));
				if(!Utils.isSame(inBB.get(bb), newIn)){
					inBB.put(bb, newIn);
					for(BasicBlock prev: bb.prev){
						HashSet<Var> newOut = Utils.union(outBB.get(prev), inBB.get(bb));
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
		//		// for debugging
		//		for(BasicBlock bb: func.bbs){
		//			for(ICode iCode: bb.iCodes){
		//				System.out.println(iCode);
		//				System.out.println("in: " + inIR.get(iCode));
		//				System.out.println("out: " + outIR.get(iCode) + '\n');
		//			}
		//		}
	}

	public boolean isActive(ICode iCode, Var var){
		return inIR.containsKey(iCode) && inIR.get(iCode).contains(var);
	}
}
