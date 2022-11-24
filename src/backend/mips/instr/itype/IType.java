package backend.mips.instr.itype;

import backend.mips.instr.*;
import backend.mips.reg.*;

public abstract class IType implements Instr{
	public Reg rs;
	public Reg rt;
	public Integer imm;

	public IType(Reg rs, Reg rt, Integer imm){
		this.rs = rs;
		this.rt = rt;
		this.imm = imm;
	}

	@Override
	public String toString(){ return this.getClass().getSimpleName().toLowerCase()+
	                                 " " + rt + ", " + rs + ", " + imm; }
}
