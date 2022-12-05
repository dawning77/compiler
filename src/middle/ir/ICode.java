package middle.ir;

import backend.mips.instr.*;
import backend.mips.reg.*;
import middle.operand.symbol.*;

import java.util.*;

public abstract class ICode{
	public HashSet<Symbol> use;
	public HashSet<Symbol> def;
	public ArrayList<Instr> instrs;

	public ICode(){
		this.use = new HashSet<>();
		this.def = new HashSet<>();
		this.instrs = new ArrayList<>();
	}

	@Override
	abstract public String toString();

	abstract public void genInstr(RegManager regManager);
}
