package middle.ir.func;

import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.symbol.*;

public class GetRet extends ICode{
	public Symbol res;

	public GetRet(Symbol res){
		super();
		this.res = res;
		def = res;
	}

	@Override
	public String toString(){
		return res + " = " + "GetRet()";
	}

	@Override
	public void genInstr(RegManager regManager){
		Reg reg = regManager.getDef(res);
		instrs.add(new Move(reg, Reg.$v0));
	}
}
