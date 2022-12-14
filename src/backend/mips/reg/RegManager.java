package backend.mips.reg;

import backend.*;
import middle.operand.symbol.*;

import java.util.*;
import java.util.stream.*;

public class RegManager{
	// a0, v0 cannot alloc
	public static final LinkedHashSet<Reg> GLOBAL_REG = Stream.of(
					Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3,
					Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7)
			.collect(Collectors.toCollection(LinkedHashSet::new));

	public static final LinkedHashSet<Reg> TMP_REG = Stream.of(
					Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3,
					Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
					Reg.$t8, Reg.$t9,Reg.$a1, Reg.$a2, Reg.$a3, Reg.$v1, Reg.$fp)
			.collect(Collectors.toCollection(LinkedHashSet::new));

	//	public static final LinkedHashSet<Reg> ALLOCATABLE = Stream.of(
	//					Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
	//					Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7,
	//					Reg.$t8, Reg.$t9, Reg.$a1, Reg.$a2, Reg.$a3, Reg.$v1, Reg.$fp)
	//			.collect(Collectors.toCollection(LinkedHashSet::new));

	public final MipsManager mipsManager;
	public final GlobalRegManager globalRegManager;
	public final TmpRegManager tmpRegManager;

	public RegManager(MipsManager mipsManager){
		this.mipsManager = mipsManager;
		this.globalRegManager = new GlobalRegManager(mipsManager);
		this.tmpRegManager = new TmpRegManager(mipsManager);
	}

	public void addToStack(Symbol sym){
		if(!globalRegManager.allocGlobal(sym)) tmpRegManager.addToStack(sym);
	}

	public Reg getDef(Symbol sym){
		if(globalRegManager.allocGlobal(sym)) return globalRegManager.getDef(sym);
		else return tmpRegManager.getDef(sym);
	}

	public Reg getUse(Symbol sym){
		if(globalRegManager.allocGlobal(sym)) return globalRegManager.getUse(sym);
		else return tmpRegManager.getUse(sym);
	}

	public void setAllSpareExcept(Reg reg){
		if(GLOBAL_REG.contains(reg)) return;
		tmpRegManager.setAllSpareExcept(reg);
	}

	public void setSpareNoStore(Reg reg){
		if(GLOBAL_REG.contains(reg)) return;
		tmpRegManager.setAllSpareExcept(reg);
	}

	public void setAllTmpRegSpare(){ tmpRegManager.setAllSpare(); }

	public void setAllGlobalRegSpare(){ globalRegManager.setAllSpare(); }

	public void setAllGlobalVarSpare(){ tmpRegManager.setAllGlobalSpare(); }

	@Override
	public String toString(){ return tmpRegManager.toString()+'\n'+globalRegManager; }
}
