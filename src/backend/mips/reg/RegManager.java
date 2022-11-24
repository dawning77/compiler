package backend.mips.reg;

import backend.*;
import backend.mips.instr.itype.*;
import middle.*;
import middle.operand.symbol.*;

import java.util.*;
import java.util.stream.*;

public class RegManager{
	public static final LinkedHashSet<Reg> ALLOCATABLE = Stream.of(
					Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
					Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7,
					Reg.$t8, Reg.$t9, Reg.$a1, Reg.$a2, Reg.$a3)
			.collect(Collectors.toCollection(LinkedHashSet::new));

	public RegState curState;
	public HashMap<BasicBlock, RegState> stateMap;  // block name to state
	public final MipsManager mipsManager;

	public RegManager(MipsManager mipsManager){
		this.curState = new RegState(new LinkedHashSet<>(ALLOCATABLE),
		                             new LinkedHashMap<>(),
		                             new LinkedHashMap<>(),
		                             new HashSet<>());
		this.stateMap = new HashMap<>();
		this.mipsManager = mipsManager;
	}

	public void saveRegState(BasicBlock bb){
		stateMap.put(bb, new RegState(new LinkedHashSet<>(curState.spare),
		                              new LinkedHashMap<>(curState.used),
		                              new LinkedHashMap<>(curState.vars),
		                              new HashSet<>(curState.inStack)));
	}

	public void loadRegState(BasicBlock bb){ curState = stateMap.get(bb); }

	public Reg get(Symbol sym){
		if(!curState.vars.containsKey(sym)) alloc(sym);
		return curState.vars.get(sym);
	}

	public Reg getParam(Symbol sym){
		if(!curState.vars.containsKey(sym)) {
			if(sym.type.equals(Symbol.Type.global)) curState.inStack.add(sym);
			if(!curState.spare.isEmpty()){
				for(Reg reg: curState.spare){
					if(reg.equals(Reg.$a0)||reg.equals(Reg.$a1)||reg.equals(Reg.$a2)||reg.equals(Reg.$a3))continue;
					setUsed(reg, sym);
					break;
				}
			}
			else{
				for(Reg reg: curState.used.keySet()){
					if(reg.equals(Reg.$a0)||reg.equals(Reg.$a1)||reg.equals(Reg.$a2)||reg.equals(Reg.$a3))continue;
					setSpare(reg);
					setUsed(reg, sym);
					break;
				}
			}
		}
		return curState.vars.get(sym);
	}

	private void alloc(Symbol sym){
		// regState.inStack need to load before use
		if(sym.type.equals(Symbol.Type.global)) curState.inStack.add(sym);
		if(!curState.spare.isEmpty()){
			for(Reg reg: curState.spare){
				setUsed(reg, sym);
				return;
			}
		}
		else{
			for(Reg reg: curState.used.keySet()){
				setSpare(reg);
				setUsed(reg, sym);
				return;
			}
		}
	}

	public void setAllSpareWithoutWriteBack(){
		curState = new RegState(new LinkedHashSet<>(ALLOCATABLE),
		                        new LinkedHashMap<>(),
		                        new LinkedHashMap<>(),
		                        new HashSet<>());
	}

	public void setAllSpare(){
		HashSet<Reg> used = new HashSet<>(curState.used.keySet());
		used.forEach(this::setSpare);
	}

	public void setAllGlobalSpare(){
		HashSet<Reg> used = new HashSet<>(curState.used.keySet());
		used.stream().filter(r->curState.used.get(r).type.equals(Symbol.Type.global)).forEach(this::setSpare);
	}

	public void setSpare(Reg reg){
		Symbol sym = curState.used.get(reg);
		if(sym == null) return;
		setSpareWithoutWriteBack(reg);
		writeBack(reg, sym);
	}

	public void setSpareWithoutWriteBack(Reg reg){
		if(!ALLOCATABLE.contains(reg) && !reg.equals(Reg.$a0)) return;
		Symbol sym = curState.used.get(reg);
		curState.spare.add(reg);
		curState.used.remove(reg);
		if(!(sym == null)) curState.vars.remove(sym);
	}

	public void writeBack(Reg reg, Symbol sym){
		if(sym instanceof Const) return;
		if(sym.type.equals(Symbol.Type.param) || sym.type.equals(Symbol.Type.local) || sym.type.equals(Symbol.Type.tmp))
			mipsManager.genInstr(new Sw(Reg.$fp, reg, sym.loc * 4));
		else if(sym.type.equals(Symbol.Type.global))
			mipsManager.genInstr(new backend.mips.instr.pseudo.Sw(reg, sym.name, 0));
		curState.inStack.add(sym);
	}

	public void setUsed(Reg reg, Symbol sym){
		if(!ALLOCATABLE.contains(reg) && !reg.equals(Reg.$a0)) return;
		curState.spare.remove(reg);
		curState.used.put(reg, sym);
		curState.vars.put(sym, reg);
		if(curState.inStack.contains(sym)){
			if(sym.type.equals(Symbol.Type.param) || sym.type.equals(Symbol.Type.local) ||
			   sym.type.equals(Symbol.Type.tmp))
				mipsManager.genInstr(new Lw(Reg.$fp, reg, sym.loc * 4));
			else if(sym.type.equals(Symbol.Type.global))
				mipsManager.genInstr(new backend.mips.instr.pseudo.Lw(reg, sym.name, 0));
			curState.inStack.remove(sym);
		}
	}
}
