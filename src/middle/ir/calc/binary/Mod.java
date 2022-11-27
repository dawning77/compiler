package middle.ir.calc.binary;

import backend.mips.instr.*;
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
			if(val1 == 1) ret.add(new Li(resReg, 0));
			else if(Utils.isPowerOf2(val1)) ret.add(new Andi(reg0, resReg, val1 - 1));
				//			else{
				//				ret.add(new Li(resReg, ((Imm)opd1).val));
				//				ret.add(new backend.mips.instr.itype.Div(reg0, resReg));
				//				ret.add(new Mfhi(resReg));
				//			}
			else{
				int l = Math.max((int)Math.ceil(Utils.log2D(Math.abs(val1))), 1);
				long m = 1 + (1L << (32 + l - 1)) / Math.abs(val1);
				int m2 = (int)(m - (1L << 32));
				int sign = val1 < 0? -1: 0;
				ret.addAll(Arrays.asList(
						new Li(Reg.$v1, m2),
						new Mul(reg0, Reg.$v1),
						new Mfhi(Reg.$v1),
						new backend.mips.instr.rtype.Add(reg0, Reg.$v1, Reg.$v1),
						new Sra(Reg.$v1, Reg.$v1, l - 1),
						new Compare(Rel.lt, reg0, Reg.$zero, Reg.$v0),
						new Add(Reg.$v1, Reg.$v0, Reg.$v1),
						new Xori(Reg.$v1, Reg.$v1, sign),
						new Addi(Reg.$v1, Reg.$v1, -sign),
						new backend.mips.instr.itype.Mul(Reg.$v1, Reg.$v1, val1),
						new Sub(reg0, Reg.$v1, resReg)));
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
