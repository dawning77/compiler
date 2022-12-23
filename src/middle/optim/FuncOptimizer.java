package middle.optim;

import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.ir.calc.*;
import middle.ir.calc.unary.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class FuncOptimizer implements Optimizer{
	private final FuncScope func;

	public FuncOptimizer(FuncScope func){
		this.func = func;
	}

	public void optimize(){
		redundantVarRemove();
		localConstPropagation();
		// globalConstPropogation();
		loopOptimize();
		localConstPropagation();
		func.liveVarAnalyser.liveVarAnalyse();
		func.liveVarAnalyser.deadCodeRemove();
	}

	private void redundantVarRemove(){
		for(BasicBlock bb: func.bbs){
			HashSet<ICode> toRemove = new HashSet<>();
			for(int i = 0; i < bb.iCodes.size() - 1; i++){
				ICode iCode = bb.iCodes.get(i);
				if(bb.iCodes.get(i + 1) instanceof Assign){
					Symbol def = iCode.def;
					Symbol use = null;
					Symbol def2 = bb.iCodes.get(i + 1).def;
					for(Symbol sym: bb.iCodes.get(i + 1).use){ use = sym; }
					if(def != null && def.equals(use)){
						iCode.changeDef(def2);
						toRemove.add(bb.iCodes.get(i + 1));
						i++;
					}
				}
			}
			bb.iCodes.removeAll(toRemove);
		}
	}

	private void loopOptimize(){
		for(LoopInfo loopInfo: func.loopInfos){
			BasicBlock beforeBB = loopInfo.loopBefore;
			BasicBlock condBB = loopInfo.loopCond;
			BasicBlock followBB = loopInfo.loopFollow;
			HashSet<Symbol> defInLoop = new HashSet<>();
			// find def in loop vars
			boolean inLoop = false;
			for(int i = 0; i < func.bbs.size(); i++){
				BasicBlock curBB = func.bbs.get(i);
				if(curBB.equals(loopInfo.loopBody)) inLoop = true;
				else if(curBB.equals(followBB)) break;
				if(inLoop){
					curBB.iCodes.forEach(iCode->{
						if(iCode.def != null) defInLoop.add(iCode.def);
					});
				}
			}
			// get the invariants
			inLoop = false;
			int idx = beforeBB.iCodes.indexOf(loopInfo.lastIR) + 1;
			for(int i = 0; i < func.bbs.size(); i++){
				BasicBlock curBB = func.bbs.get(i);
				if(curBB.equals(loopInfo.loopBody)) inLoop = true;
				else if(curBB.equals(condBB)) break;
				if(inLoop){
					for(int j = 0; j < curBB.iCodes.size(); j++){
						ICode iCode = curBB.iCodes.get(j);
						if(iCode instanceof Calc){
							if(iCode.use.size() == 0) continue;
							boolean isInvariant = iCode.use.stream().noneMatch(defInLoop::contains);
							if(isInvariant){
								curBB.iCodes.remove(iCode);
								beforeBB.iCodes.add(idx, iCode);
								idx++;
								j--;
							}
						}
					}
				}
			}
		}
	}

	private void localConstPropagation(){
		for(BasicBlock bb: func.bbs){
			HashMap<Symbol, Integer> constMap = new HashMap<>();
			for(int i = 0; i < bb.iCodes.size(); i++){
				ICode iCode = bb.iCodes.get(i);
				if(iCode instanceof Calc){
					for(Symbol sym: iCode.use){
						if(constMap.containsKey(sym)){
							iCode.changeUse(sym, new Imm(constMap.get(sym)));
						}
					}
					Integer val = ((Calc)iCode).calc();
					Symbol sym = iCode.def;
					if(val != null && sym != null){
						constMap.put(sym, val);
						continue;
					}
				}
				constMap.remove(iCode.def);
			}
		}
	}

	//	private void globalConstPropogation(){
	//		HashMap<BasicBlock, HashMap<Symbol, Integer>> inBB = new HashMap<>();
	//		HashMap<BasicBlock, HashMap<Symbol, Integer>> outBB = new HashMap<>();
	//		HashMap<ICode, HashMap<Symbol, Integer>> inIR = new HashMap<>();
	//		// init in and out for each bb and IR
	//		for(BasicBlock bb: func.bbs){
	//			inBB.put(bb, new HashMap<>());
	//			outBB.put(bb, new HashMap<>());
	//			HashMap<Symbol, Integer> curIR = new HashMap<>();
	//			for(int i = 0; i < bb.iCodes.size(); i++){
	//				ICode iCode = bb.iCodes.get(i);
	//				inIR.put(iCode, new HashMap<>(curIR));
	//				//				System.out.println(iCode+" "+inIR.get(iCode));
	//				if(iCode instanceof Calc){
	//					Integer val = ((Calc)iCode).calc();
	//					Symbol sym = iCode.def;
	//					if(val != null && sym != null){
	//						curIR.put(sym, val);
	//						continue;
	//					}
	//				}
	//				curIR.remove(iCode.def);
	//			}
	//			outBB.put(bb, curIR);
	//		}
	//		// propagation between blocks
	//		for(BasicBlock bb: func.bbs){
	//			Queue<BasicBlock> q = new LinkedList<>();
	//			q.add(bb);
	//			while(q.size()>0){
	//				BasicBlock curBB = q.poll();
	//				for(BasicBlock next: bb.next){
	//				}
	//			}
	//		}
	//		for(BasicBlock bb: func.bbs){
	//			Queue<BasicBlock> q = new LinkedList<>();
	//			q.add(bb);
	//			while(q.size()>0){
	//				BasicBlock bb =
	//			}
	//		}
	//		for(BasicBlock bb: func.bbs){
	//			for(int i = 0; i < bb.iCodes.size(); i++){
	//				ICode iCode = bb.iCodes.get(i);
	//				if(iCode instanceof Calc){
	//					Integer val = ((Calc)iCode).calc();
	//					Symbol toReplace = iCode.def;
	//					// make sure which is the last def
	//					if(val!=null && toReplace!=null){
	//						boolean flag = false;
	//						for(int j = i + 1; j < bb.iCodes.size(); j++){
	//							ICode iCode1 = bb.iCodes.get(j);
	//							if(iCode1.use.contains(toReplace)){
	//								iCode1.changeUse(toReplace, new Imm(val));
	//							}
	//							if(toReplace.equals(iCode1.def)){
	//								flag = true;
	//								break;
	//							}
	//						}
	//						if(flag) continue;
	//					}
	//					if(val != null && toReplace != null && !iCode.def.type.equals(Symbol.Type.tmp)){
	//						Queue<BasicBlock> q = new LinkedList<>(bb.next);
	//						HashSet<BasicBlock>vis = new HashSet<>();
	//						while(q.size() > 0){
	//							BasicBlock nb = q.poll();
	//							if(vis.contains(nb))continue;
	//							vis.add(nb);
	//							boolean flag = false;
	//							for(ICode iCode1: nb.iCodes){
	//								if(iCode1.use.contains(toReplace)){
	//									iCode1.changeUse(toReplace, new Imm(val));
	//								}
	//								if(toReplace.equals(iCode1.def)){
	//									flag = true;
	//									break;
	//								}
	//							}
	//							if(!flag) q.addAll(nb.next);
	//						}
	//					}
	//				}
	//			}
	//		}
	//	}
}
