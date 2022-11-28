package middle.ir.func;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.Operand;
import middle.operand.symbol.*;

import java.util.*;

public class GetRet implements ICode{
	public Operand res;

	public GetRet(Operand res){
		this.res = res;
	}

	@Override
	public String toString(){
		return res + " = " + "GetRet()";
	}

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg = regManager.get((Var)res);
		ret.add(new Move(reg, Reg.$v0));
		return ret;
	}
}
