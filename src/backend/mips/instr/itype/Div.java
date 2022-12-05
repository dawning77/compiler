package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Div extends IType{
	public Div(Reg rs, Reg rt){
		super(rs, rt, 0);
	}

	@Override
	public String toString(){ return "div " + rs + ", " + rt; }
}
