package backend.mips.reg;

import backend.*;
import backend.mips.instr.itype.*;
import middle.ir.*;
import middle.operand.symbol.*;

import java.util.*;
import java.util.stream.*;

public class RegManager{
	// a0, v0 cannot alloc
	public static final LinkedHashSet<Reg> ALLOCATABLE = Stream.of(
					Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
					Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7,
					Reg.$t8, Reg.$t9, Reg.$a1, Reg.$a2, Reg.$a3, Reg.$v1, Reg.$fp)
			.collect(Collectors.toCollection(LinkedHashSet::new));

	//	public static final LinkedHashSet<Reg> ALLOCATABLE = Stream.of(
	//					Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3)
	//			.collect(Collectors.toCollection(LinkedHashSet::new));

	private LinkedHashSet<Reg> spare;
	private LinkedHashMap<Reg, Symbol> used;
	private LinkedHashMap<Symbol, Reg> vars;
	private HashSet<Symbol> inStack;    // symbols which need load before use
	private HashSet<Var> dirty;

	public final MipsManager mipsManager;

	public RegManager(MipsManager mipsManager){
		this.mipsManager = mipsManager;
		init();
	}

	public void init(){
		initSapre();
		this.used = new LinkedHashMap<>();
		this.vars = new LinkedHashMap<>();
		this.inStack = new HashSet<>();
		this.dirty = new HashSet<>();
	}

	public void initSapre(){ this.spare = new LinkedHashSet<>(RegManager.ALLOCATABLE); }

	public void addToStack(Symbol sym){ inStack.add(sym); }

	public Reg getDef(Symbol sym){
		if(sym instanceof Var) dirty.add((Var)sym);
		if(!vars.containsKey(sym)) alloc(sym, false);
		return vars.get(sym);
	}

	public Reg getUse(Symbol sym){
		if(!vars.containsKey(sym)) alloc(sym, true);
		return vars.get(sym);
	}

	private void alloc(Symbol sym, boolean load){
		if(!spare.isEmpty()){
			for(Reg reg: spare){
				setUsed(reg, sym, load);
				return;
			}
		}
		else{
			for(Reg reg: used.keySet()){
				if((used.get(reg) instanceof Var || used.get(reg).type.equals(Symbol.Type.param)) &&
				   mipsManager.curFunc.liveVarAnalyser.isLive(mipsManager.curIR, used.get(reg)))
					continue;
				setSpare(reg);
				setUsed(reg, sym, load);
				return;
			}
			// all is active, use OPT to switch
			Reg reg = getToSwitch();
			setSpare(reg);
			setUsed(reg, sym, load);
		}
	}

	private Reg getToSwitch(){
		int curIdx = mipsManager.curBB.iCodes.indexOf(mipsManager.curIR);
		LinkedHashSet<Reg> used1 = new LinkedHashSet<>(used.keySet());
		for(int i = curIdx; i < mipsManager.curBB.iCodes.size(); i++){
			ICode curIR = mipsManager.curBB.iCodes.get(i);
			used1.remove(vars.get(curIR.def));
			if(used1.size() == 1) for(Reg reg: used1){ return reg; }
			for(Symbol sym: curIR.use){
				used1.remove(vars.get(sym));
				if(used1.size() == 1) for(Reg reg: used1){ return reg; }
			}
		}
		for(Reg reg: used1){ return reg; }
		return Reg.$t0;
	}

	public void setAllSpareExcept(Reg reg){
		LinkedHashSet<Reg> used1 = new LinkedHashSet<>(used.keySet());
		used1.stream().filter(r->!r.equals(reg)).forEach(this::setSpare);
	}

	public void setSpareNoStore(Reg reg){
		Symbol sym = used.get(reg);
		spare.add(reg);
		used.remove(reg);
		vars.remove(sym);
	}

	public void setAllSpare(){
		LinkedHashSet<Reg> used1 = new LinkedHashSet<>(used.keySet());
		used1.forEach(this::setSpare);
		initSapre();
	}

	public void setAllGlobalSpare(){
		LinkedHashSet<Reg> used1 = new LinkedHashSet<>(used.keySet());
		used1.stream().filter(r->used.get(r).type.equals(Symbol.Type.global)).forEach(this::setSpare);
		initSapre();
	}

	private void setSpare(Reg reg){
		Symbol sym = used.get(reg);
		setSpareNoStore(reg);
		store(reg, sym);
	}

	private void store(Reg reg, Symbol sym){
		// sym can only be Var or param
		if(!(sym instanceof Var)){
			inStack.add(sym);
			return;
		}
		if(sym.type.equals(Symbol.Type.global)){
			mipsManager.genInstr(new backend.mips.instr.pseudo.Sw(reg, sym.name, 0));
		}
		else if(mipsManager.curFunc.liveVarAnalyser.isLive(mipsManager.curIR, sym)){
			if(dirty.contains((Var)sym)) mipsManager.genInstr(new Sw(Reg.$sp, reg, sym.loc * 4));
		}
		inStack.add(sym);
		dirty.remove(sym);
	}

	private void load(Reg reg, Symbol sym, boolean load){
		if(inStack.contains(sym)){
			if(load){
				if(!sym.type.equals(Symbol.Type.global))
					mipsManager.genInstr(new Lw(Reg.$sp, reg, sym.loc * 4));
				else
					mipsManager.genInstr(new backend.mips.instr.pseudo.Lw(reg, sym.name, 0));
			}
			inStack.remove(sym);
		}
	}

	private void setUsed(Reg reg, Symbol sym, boolean load){
		spare.remove(reg);
		used.put(reg, sym);
		vars.put(sym, reg);
		load(reg, sym, load);
	}

	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder("----------RegState----------");
		ret.append("\nspare: ");
		spare.forEach(reg->ret.append(reg.toString()).append(" "));
		ret.append("\nused: ");
		used.keySet()
				.forEach(reg->ret.append("(").append(reg.toString()).append(", ").append(used.get(reg)).append(") "));
		ret.append("\ninstack: ");
		inStack.forEach(sym->ret.append(sym.toString()).append(" "));
		ret.append("\ndirty: ");
		dirty.forEach(var->ret.append(var.toString()).append(" "));
		ret.append("\n----------------------------");
		return ret.toString();
	}
}
