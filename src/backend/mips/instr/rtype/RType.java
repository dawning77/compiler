package backend.mips.instr.rtype;

import backend.mips.instr.*;
import backend.mips.reg.*;

public abstract class RType implements Instr{
	public Reg rs;
	public Reg rt;
	public Reg rd;
	// public Integer shamt;
	public RType(Reg rs, Reg rt, Reg rd){
		this.rs = rs;
		this.rt = rt;
		this.rd = rd;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName().toLowerCase() +
		       " " + rd + ", " + rs + ", " + rt;
	}
}
