package middle.ir;

import backend.mips.instr.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public abstract class ICode{
	public HashSet<Symbol> use;
	public Symbol def;
	public ArrayList<Instr> instrs;

	public ICode(){
		this.use = new HashSet<>();
		this.def = null;
		this.instrs = new ArrayList<>();
	}

	@Override
	abstract public String toString();

	abstract public void genInstr(RegManager regManager);

	abstract public void changeUse(Symbol oldUse, Operand newUse);

	abstract public void changeDef(Symbol newDef);
}
