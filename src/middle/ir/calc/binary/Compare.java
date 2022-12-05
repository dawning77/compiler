package middle.ir.calc.binary;

import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Compare extends Binary{
	public Rel rel;

	public Compare(Operand opd0, Operand opd1, Operand res, Rel rel){
		super(opd0, opd1, res);
		this.rel = rel;
	}

	@Override
	public String toString(){ return res + " = " + opd0 + " " + rel + " " + opd1; }

	@Override
	public void genInstr(RegManager regManager){
		Reg resReg;
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val0 = ((Imm)opd0).val;
			int val1 = ((Imm)opd1).val;
			int boolVal = rel.satisfied(val0, val1)? 1: 0;
			resReg = regManager.getDef((Var)res);
			instrs.add(new Li(resReg, boolVal));
			return ;
		}
		Reg reg0;
		Reg reg1;
		if(opd0 instanceof Imm){
			reg0 = Reg.$v0;
			reg1 = regManager.getUse((Var)opd1);
			instrs.add(new Li(Reg.$v0, ((Imm)opd0).val));
		}
		else if(opd1 instanceof Imm){
			reg0 = regManager.getUse((Var)opd0);
			reg1 = Reg.$v0;
			instrs.add(new Li(Reg.$v0, ((Imm)opd1).val));
		}
		else{
			reg0 = regManager.getUse((Var)opd0);
			reg1 = regManager.getUse((Var)opd1);
		}
		resReg = regManager.getDef((Var)res);
		instrs.add(new backend.mips.instr.rtype.Compare(rel, reg0, reg1, resReg));
	}
}
