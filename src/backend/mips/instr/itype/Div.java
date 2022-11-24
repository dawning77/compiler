package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Div extends IType{
	public Div(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}

	public Div(Reg rs, Reg rt){
		super(rs, rt, 0);
	}

	@Override
	public String toString(){
		if(imm != 0) return super.toString();
		else return "div " + rs + ", " + rt;
	}
}
