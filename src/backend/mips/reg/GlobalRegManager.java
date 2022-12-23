package backend.mips.reg;

import backend.*;
import backend.mips.instr.itype.*;
import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.symbol.*;
import middle.optim.*;

import java.util.*;
import java.util.stream.*;

public class GlobalRegManager{
	private HashMap<Symbol, Reg> regMap;
	private HashSet<Var> dirty;

	public final MipsManager mipsManager;

	public GlobalRegManager(MipsManager mipsManager){
		this.mipsManager = mipsManager;
		init();
	}

	public void init(){
		this.regMap = new HashMap<>();
		this.dirty = new HashSet<>();
	}

	public boolean allocGlobal(Symbol sym){ return regMap.containsKey(sym); }

	public void load(Symbol sym){
		if(regMap.containsKey(sym))
			mipsManager.genInstr(new Lw(Reg.$sp, regMap.get(sym), sym.loc * 4));
	}

	private void store(Symbol sym){
		mipsManager.genInstr(new Sw(Reg.$sp, regMap.get(sym), sym.loc * 4));
	}

	public Reg getDef(Symbol sym){
		if(regMap.containsKey(sym)){
			if(sym instanceof Var) dirty.add((Var)sym);
			return regMap.get(sym);
		}
		return null;
	}

	public Reg getUse(Symbol sym){
		if(regMap.containsKey(sym)){ return regMap.get(sym); }
		return null;
	}

	public void setAllSpare(){
		for(Var var: dirty){
			if(mipsManager.curFunc.liveVarAnalyser.isLive(mipsManager.curIR, var)){
				store(var);
			}
		}
		dirty = new HashSet<>();
	}

	public void allocGlobalReg(LiveVarAnalyser liveVarAnalyser){
		HashMap<Symbol, HashSet<Symbol>> conflicts = new HashMap<>();
		HashSet<Symbol> nodes = new HashSet<>();
		ArrayList<Map.Entry<Symbol, Integer>> refCnt = new ArrayList<>(liveVarAnalyser.refCnt.entrySet());
		refCnt.sort(Comparator.comparingInt(Map.Entry::getValue));
		// build conflict graph
		for(BasicBlock bb: liveVarAnalyser.func.bbs){
			for(ICode iCode: bb.iCodes){
				if(liveVarAnalyser.outIR.get(iCode) == null || liveVarAnalyser.outIR.get(iCode).size() == 0) continue;
				ArrayList<Symbol> vars =
						Utils.union(liveVarAnalyser.useIR.get(iCode), liveVarAnalyser.outIR.get(iCode)).stream()
								.filter(sym->!sym.type.equals(Symbol.Type.global) && !sym.type.equals(Symbol.Type.tmp))
								.collect(Collectors.toCollection(ArrayList::new));
				for(int i = 0; i < vars.size(); i++){
					if(!conflicts.containsKey(vars.get(i))){
						conflicts.put(vars.get(i), new HashSet<>());
						if(conflicts.size() > 800){
							// points count reach the limit, use the refCnt
							int curIdx = refCnt.size() - 1;
							for(Reg reg: RegManager.GLOBAL_REG){
								regMap.put(refCnt.get(curIdx).getKey(), reg);
								curIdx--;
							}
							return;
						}
					}
					nodes.add(vars.get(i));
					for(int j = 0; j < i; j++){
						conflicts.get(vars.get(i)).add(vars.get(j));
						conflicts.get(vars.get(j)).add(vars.get(i));
					}
				}
			}
		}
		if(conflicts.keySet().size() == 0) return;
		// perform graph coloring algorithm
		ArrayList<Reg> regs = new ArrayList<>(RegManager.GLOBAL_REG);
		Stack<Symbol> stack = new Stack<>();
		while(nodes.size() > 0){
			Symbol toRemove = null;
			for(Symbol node: nodes){
				int degree = conflicts.get(node).stream().filter(nodes::contains)
						.collect(Collectors.toSet()).size();
				if(degree < regs.size()){
					toRemove = node;
					break;
				}
			}
			if(toRemove != null){
				stack.add(toRemove);
				nodes.remove(toRemove);
			}
			else{
				// do not alloc global reg
				for(Map.Entry<Symbol, Integer> symRef: refCnt){
					if(nodes.contains(symRef.getKey())){
						nodes.remove(symRef.getKey());
						break;
					}
				}
			}
		}
		Symbol node = stack.pop();
		regMap.put(node, regs.get(0));
		while(stack.size() > 0){
			node = stack.pop();
			HashSet<Reg> colorable = new HashSet<>(regs);
			for(Symbol other: conflicts.get(node)){
				if(regMap.containsKey(other)) colorable.remove(regMap.get(other));
			}
			regMap.put(node, (Reg)colorable.toArray()[0]);
		}
	}

	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder("----------GlobalReg---------");
		ret.append("\nregMap: ").append(regMap);
		ret.append("\ndirty: ");
		dirty.forEach(var->ret.append(var.toString()).append(" "));
		ret.append("\n----------------------------");
		return ret.toString();
	}
}
