package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Mfhi extends RType{
	public Mfhi(Reg rd){
		super(Reg.$zero, Reg.$zero, rd);
	}

	@Override
	public String toString(){ return "mfhi " + rd; }
}
