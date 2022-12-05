package middle.ir.calc.binary;

import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Add extends Binary{
	public Add(Operand opd0, Operand opd1, Operand res){
		super(opd0, opd1, res);
	}

	@Override
	public void genInstr(RegManager regManager){
		Reg reg0;
		Reg reg1;
		Reg resReg;
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val = ((Imm)opd0).val + ((Imm)opd1).val;
			resReg = regManager.getDef((Var)res);
			instrs.add(new Li(resReg, val));
		}
		else if(opd0 instanceof Imm){
			reg1 = regManager.getUse((Var)opd1);
			resReg = regManager.getDef((Var)res);
			instrs.add(new Addi(reg1, resReg, ((Imm)opd0).val));
		}
		else if(opd1 instanceof Imm){
			reg0 = regManager.getUse((Var)opd0);
			resReg = regManager.getDef((Var)res);
			instrs.add(new Addi(reg0, resReg, ((Imm)opd1).val));
		}
		else{
			reg0 = regManager.getUse((Var)opd0);
			reg1 = regManager.getUse((Var)opd1);
			resReg = regManager.getDef((Var)res);
			instrs.add(new backend.mips.instr.rtype.Add(reg0, reg1, resReg));
		}
	}
}
