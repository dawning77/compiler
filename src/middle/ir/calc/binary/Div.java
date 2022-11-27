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
			else if(Utils.isPowerOf2(val1)) ret.add(new Sra(reg0, resReg, Utils.log2I(val1)));
				//			else ret.add(new backend.mips.instr.itype.Div(reg0, resReg, ((Imm)opd1).val));
			else{
				int l = Math.max((int)Math.ceil(Utils.log2D(Math.abs(val1))), 1);
				long m = 1 + (1L << (32 + l - 1)) / Math.abs(val1);
				int m2 = (int)(m - (1L << 32));
				int sign = val1 < 0? -1: 0;
				ret.addAll(Arrays.asList(
						new Li(Reg.$v1, m2),
						new Mul(reg0, Reg.$v1),
						new Mfhi(Reg.$v1),
						new Add(reg0, Reg.$v1, Reg.$v1),
						new Sra(Reg.$v1, Reg.$v1, l - 1),
						new Compare(Rel.lt, reg0, Reg.$zero, Reg.$v0),
						new Add(Reg.$v1, Reg.$v0, Reg.$v1),
						new Xori(Reg.$v1, Reg.$v1, sign),
						new Addi(Reg.$v1, resReg, -sign)));
			}
		}
		else{
			reg0 = regManager.get((Var)opd0);
			reg1 = regManager.get((Var)opd1);
			ret.add(new backend.mips.instr.rtype.Div(reg0, reg1, resReg));
		}
		return ret;
	}
}
