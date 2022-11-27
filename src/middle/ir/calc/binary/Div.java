package middle.ir.calc.binary;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.instr.rtype.Sub;
import backend.mips.reg.*;
import middle.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Div extends Binary{
	public Div(Operand opd0, Operand opd1, Operand res){
		super(opd0, opd1, res);
	}

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg0;
		Reg reg1;
		Reg resReg = regManager.get((Var)res);
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val = ((Imm)opd0).val / ((Imm)opd1).val;
			ret.add(new Li(resReg, val));
		}
		else if(opd0 instanceof Imm){
			int val0 = ((Imm)opd0).val;
			reg1 = regManager.get((Var)opd1);
			ret.add(new Li(resReg, ((Imm)opd0).val));
			if(val0 == 0) ret.add(new Li(resReg, 0));
			else ret.add(new backend.mips.instr.rtype.Div(resReg, reg1, resReg));
		}
		else if(opd1 instanceof Imm){
			int val1 = ((Imm)opd1).val;
			reg0 = regManager.get((Var)opd0);
			if(val1 == 1) ret.add(new Move(resReg, reg0));
			else if(val1 == -1) ret.add(new Sub(Reg.$zero, reg0, resReg));
			else if(Utils.isPowerOf2(val1)) ret.add(new Sra(reg0, resReg, Utils.log2(val1)));
				// TODO: 2022/11/25 div 2 mult
			else ret.add(new backend.mips.instr.itype.Div(reg0, resReg, ((Imm)opd1).val));
		}
		else{
			reg0 = regManager.get((Var)opd0);
			reg1 = regManager.get((Var)opd1);
			ret.add(new backend.mips.instr.rtype.Div(reg0, reg1, resReg));
		}
		return ret;
	}
}
