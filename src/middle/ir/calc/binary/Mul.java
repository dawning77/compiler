package middle.ir.calc.binary;

import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.instr.rtype.Add;
import backend.mips.instr.rtype.Sub;
import backend.mips.reg.*;
import middle.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Mul extends Binary{
	public Mul(Operand opd0, Operand opd1, Symbol res){
		super(opd0, opd1, res);
	}

	@Override
	public void genInstr(RegManager regManager){
		Reg resReg;
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val = ((Imm)opd0).val * ((Imm)opd1).val;
			resReg = regManager.getDef(res);
			instrs.add(new Li(resReg, val));
		}
		else if(opd0 instanceof Imm || opd1 instanceof Imm){
			int val = opd0 instanceof Imm? ((Imm)opd0).val: ((Imm)opd1).val;
			Reg reg = regManager.getUse(opd0 instanceof Imm? (Var)opd1: (Var)opd0);
			resReg = regManager.getDef(res);
			if(val == 0) instrs.add(new Li(resReg, 0));
			else if(val == 1) instrs.add(new Move(resReg, reg));
			else if(val == -1) instrs.add(new Sub(Reg.$zero, reg, resReg));
			else if(Utils.isPowerOf2(Math.abs(val))){
				instrs.add(new Sll(reg, resReg, Utils.log2I(Math.abs(val))));
				if(val < 0) instrs.add(new Sub(Reg.$zero, resReg, resReg));
			}
			else if(Utils.isPowerOf2(Math.abs(val - 1))){
				instrs.add(new Sll(reg, resReg, Utils.log2I(Math.abs(val - 1))));
				if(val - 1 < 0) instrs.add(new Sub(reg, resReg, resReg));
				else instrs.add(new Add(resReg, reg, resReg));
			}
			else if(Utils.isPowerOf2(Math.abs(val + 1))){
				instrs.add(new Sll(reg, resReg, Utils.log2I(Math.abs(val + 1))));
				if(val + 1 < 0){
					instrs.add(new Sub(Reg.$zero, resReg, resReg));
					instrs.add(new Sub(resReg, reg, resReg));
				}
				else instrs.add(new Sub(resReg, reg, resReg));
			}
			else instrs.add(new backend.mips.instr.itype.Mul(reg, resReg, val));
		}
		else{
			Reg reg0 = regManager.getUse((Var)opd0);
			Reg reg1 = regManager.getUse((Var)opd1);
			resReg = regManager.getDef(res);
			instrs.add(new backend.mips.instr.rtype.Mul(reg0, reg1, resReg));
		}
	}
}
