package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Mflo extends RType{
	public Mflo(Reg rd){
		super(Reg.$zero, Reg.$zero, rd);
	}

	@Override
	public String toString(){ return "mflo " + rd; }
}
