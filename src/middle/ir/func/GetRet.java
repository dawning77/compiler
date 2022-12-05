package middle.ir.func;

import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.Operand;
import middle.operand.symbol.*;

public class GetRet extends ICode{
	public Operand res;

	public GetRet(Operand res){
		super();
		this.res = res;
		def.add((Symbol)res);
	}

	@Override
	public String toString(){
		return res + " = " + "GetRet()";
	}

	@Override
	public void genInstr(RegManager regManager){
		Reg reg = regManager.getDef((Var)res);
		instrs.add(new Move(reg, Reg.$v0));
	}
}
