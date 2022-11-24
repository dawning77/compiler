package middle.ir.calc.binary;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Mul extends Binary{
	public Mul(Operand opd0, Operand opd1, Operand res){
		super(opd0, opd1, res);
	}

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg0;
		Reg reg1;
		Reg resReg = regManager.get((Var)res);
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val = ((Imm)opd0).val * ((Imm)opd1).val;
			ret.add(new Li(resReg, val));
		}
		else if(opd0 instanceof Imm){
			// TODO: 2022/10/28 2 power
			reg1 = regManager.get((Var)opd1);
			ret.add(new backend.mips.instr.itype.Mul(reg1, resReg, ((Imm)opd0).val));
		}
		else if(opd1 instanceof Imm){
			// TODO: 2022/10/28 2 power
			reg0 = regManager.get((Var)opd0);
			ret.add(new backend.mips.instr.itype.Mul(reg0, resReg, ((Imm)opd1).val));
		}
		else{
			reg0 = regManager.get((Var)opd0);
			reg1 = regManager.get((Var)opd1);
			ret.add(new backend.mips.instr.rtype.Mul(reg0, reg1, resReg));
		}
		return ret;
	}
}
