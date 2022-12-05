package backend.mips.instr.rtype;

import backend.mips.reg.*;
import middle.*;

public class Compare extends RType{
	public Rel rel;

	public Compare(Rel rel, Reg rs, Reg rt, Reg rd){
		super(rs, rt, rd);
		this.rel = rel;
	}

	@Override
	public String toString(){ return "s" + rel + " " + rd + ", " + rs + ", " + rt; }
}
