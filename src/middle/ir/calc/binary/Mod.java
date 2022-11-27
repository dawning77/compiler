package middle.ir.calc.binary;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Mod extends Binary{
	public Mod(Operand opd0, Operand opd1, Operand res){
		super(opd0, opd1, res);
	}

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg0;
		Reg reg1;
		Reg resReg = regManager.get((Var)res);
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val = ((Imm)opd0).val % ((Imm)opd1).val;
			ret.add(new Li(resReg, val));
		}
		else if(opd0 instanceof Imm){
			int val0 = ((Imm)opd0).val;
			reg1 = regManager.get((Var)opd1);
			if(val0 == 0) ret.add(new Li(resReg, 0));
			else{
				ret.add(new Li(resReg, ((Imm)opd0).val));
				ret.add(new backend.mips.instr.itype.Div(resReg, reg1));
				ret.add(new Mfhi(resReg));
			}
		}
		else if(opd1 instanceof Imm){
			int val1 = ((Imm)opd1).val;
			reg0 = regManager.get((Var)opd0);
			if(val1 == 1)ret.add(new Li(resReg, 0));
			else{
				ret.add(new Li(resReg, ((Imm)opd1).val));
				ret.add(new backend.mips.instr.itype.Div(reg0, resReg));
				ret.add(new Mfhi(resReg));
			}
		}
		else{
			reg0 = regManager.get((Var)opd0);
			reg1 = regManager.get((Var)opd1);
			ret.add(new backend.mips.instr.itype.Div(reg0, reg1));
			ret.add(new Mfhi(resReg));
		}
		return ret;
	}
}
