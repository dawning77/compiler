package middle.ir.calc.unary;

import backend.mips.instr.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.ir.*;
import middle.operand.Operand;
import middle.operand.symbol.*;

import java.util.*;

public class Not implements ICode{
	public Operand opd0;
	public Operand res;

	public Not(Operand opd0, Operand res){
		this.opd0 = opd0;
		this.res = res;
	}

	@Override
	public String toString(){ return res + " = !" + opd0; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg = regManager.get((Var)opd0);
		Reg resReg = regManager.get((Var)res);
		ret.add(new Compare(Rel.eq,reg,Reg.$zero,resReg));
		return ret;
	}
}
