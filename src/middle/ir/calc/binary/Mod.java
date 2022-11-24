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
			reg1 = regManager.get((Var)opd1);
			// TODO: 2022/10/28 maybe formFrame a spare
			ret.add(new Li(resReg, ((Imm)opd0).val));
			ret.add(new backend.mips.instr.itype.Div(resReg, reg1));
			ret.add(new Mfhi(resReg));
		}
		else if(opd1 instanceof Imm){
			reg0 = regManager.get((Var)opd0);
			// TODO: 2022/10/28 maybe formFrame a spare
			ret.add(new Li(resReg, ((Imm)opd1).val));
			ret.add(new backend.mips.instr.itype.Div(reg0, resReg));
			ret.add(new Mfhi(resReg));
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
