package middle.ir.calc.binary;

import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.pseudo.Mul;
import backend.mips.instr.rtype.*;
import backend.mips.instr.rtype.Add;
import backend.mips.instr.rtype.Compare;
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
	public void genInstr(RegManager regManager){
		Reg reg0;
		Reg reg1;
		Reg resReg;
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val = ((Imm)opd0).val / ((Imm)opd1).val;
			resReg = regManager.getDef((Var)res);
			instrs.add(new Li(resReg, val));
		}
		else if(opd0 instanceof Imm){
			int val0 = ((Imm)opd0).val;
			if(val0 == 0){
				resReg = regManager.getDef((Var)res);
				instrs.add(new Li(resReg, 0));
			}
			else{
				reg1 = regManager.getUse((Var)opd1);
				resReg = regManager.getDef((Var)res);
				instrs.add(new Li(resReg, ((Imm)opd0).val));
				instrs.add(new backend.mips.instr.itype.Div(resReg, reg1));
				instrs.add(new Mflo(resReg));
			}
		}
		else if(opd1 instanceof Imm){
			int val1 = ((Imm)opd1).val;
			reg0 = regManager.getUse((Var)opd0);
			resReg = regManager.getDef((Var)res);
			if(val1 == 1) instrs.add(new Move(resReg, reg0));
			else if(val1 == -1) instrs.add(new Sub(Reg.$zero, reg0, resReg));
			else if(Utils.isPowerOf2(Math.abs(val1))){
				instrs.add(new Sra(reg0, resReg, Utils.log2I(Math.abs(val1))));
				if(val1 < 0) instrs.add(new Sub(Reg.$zero, resReg, resReg));
			}
			else{
				int l = Math.max((int)Math.ceil(Utils.log2D(Math.abs(val1))), 1);
				long m = 1 + (1L << (32 + l - 1)) / Math.abs(val1);
				int m2 = (int)(m - (1L << 32));
				int sign = val1 < 0? -1: 0;
				instrs.addAll(Arrays.asList(
						new Li(Reg.$v0, m2),
						new Mul(reg0, Reg.$v0),
						new Mfhi(Reg.$v0),
						new Add(reg0, Reg.$v0, Reg.$v0),
						new Sra(Reg.$v0, Reg.$v0, l - 1),
						new Compare(Rel.lt, reg0, Reg.$zero, Reg.$a0),
						new Add(Reg.$v0, Reg.$a0, Reg.$v0),
						new Xori(Reg.$v0, Reg.$v0, sign),
						new Addi(Reg.$v0, resReg, -sign)));
			}
		}
		else{
			reg0 = regManager.getUse((Var)opd0);
			reg1 = regManager.getUse((Var)opd1);
			resReg = regManager.getDef((Var)res);
			instrs.add(new backend.mips.instr.itype.Div(reg0, reg1));
			instrs.add(new Mflo(resReg));
		}
	}
}
