package middle.ir.calc.binary;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Compare extends Binary{
	public Rel rel;

	public Compare(Operand opd0, Operand opd1, Operand res, Rel rel){
		super(opd0, opd1, res);
		this.rel = rel;
	}

	@Override
	public String toString(){ return res + " = " + opd0 + " " + rel + " " + opd1; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg resReg = regManager.get((Var)res);
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val0 = ((Imm)opd0).val;
			int val1 = ((Imm)opd1).val;
			int boolVal = rel.satisfied(val0, val1)? 1: 0;
			ret.add(new Li(resReg, boolVal));
			return ret;
		}
		Reg reg0;
		Reg reg1;
		if(opd0 instanceof Imm){
			reg0 = Reg.$v1;
			reg1 = regManager.get((Var)opd1);
			ret.add(new Li(Reg.$v1, ((Imm)opd0).val));

		}
		else if(opd1 instanceof Imm){
			reg0 = regManager.get((Var)opd0);
			reg1 = Reg.$v1;
			ret.add(new Li(Reg.$v1, ((Imm)opd1).val));
		}
		else{
			reg0 = regManager.get((Var)opd0);
			reg1 = regManager.get((Var)opd1);
		}
		ret.add(new backend.mips.instr.rtype.Compare(rel, reg0, reg1, resReg));
		return ret;
	}
}
