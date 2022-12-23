package middle.ir.calc.binary;

import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Compare extends Binary{
	public Rel rel;

	public Compare(Operand opd0, Operand opd1, Symbol res, Rel rel){
		super(opd0, opd1, res);
		this.rel = rel;
	}

	@Override
	public Integer calc(){
		if(opd0 instanceof Imm && opd1 instanceof Imm)
			return rel.satisfied(((Imm)opd0).val, ((Imm)opd1).val)? 1: 0;
		else return null;
	}

	@Override
	public String toString(){ return res + " = " + opd0 + " " + rel + " " + opd1; }

	@Override
	public void genInstr(RegManager regManager){
		Reg resReg;
		if(opd0 instanceof Imm && opd1 instanceof Imm){
			int val0 = ((Imm)opd0).val;
			int val1 = ((Imm)opd1).val;
			int boolVal = rel.satisfied(val0, val1)? 1: 0;
			resReg = regManager.getDef(res);
			instrs.add(new Li(resReg, boolVal));
			return;
		}
		Reg reg0;
		Reg reg1;
		switch(rel){
			case lt:
				if(opd0 instanceof Imm){
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Li(Reg.$v0, ((Imm)opd0).val));
					instrs.add(new backend.mips.instr.rtype.Compare(Rel.lt, Reg.$v0, reg1, resReg));
				}
				else if(opd1 instanceof Imm){
					reg0 = regManager.getUse((Var)opd0);
					resReg = regManager.getDef(res);
					instrs.add(new Slti(reg0, resReg, ((Imm)opd1).val));
				}
				else{
					reg0 = regManager.getUse((Var)opd0);
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new backend.mips.instr.rtype.Compare(rel, reg0, reg1, resReg));
				}
				break;
			case le:
				if(opd0 instanceof Imm){
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Li(Reg.$v0, ((Imm)opd0).val - 1));
					instrs.add(new backend.mips.instr.rtype.Compare(Rel.lt, Reg.$v0, reg1, resReg));
				}
				else if(opd1 instanceof Imm){
					reg0 = regManager.getUse((Var)opd0);
					resReg = regManager.getDef(res);
					instrs.add(new Slti(reg0, resReg, ((Imm)opd1).val + 1));
				}
				else{
					reg0 = regManager.getUse((Var)opd0);
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new backend.mips.instr.rtype.Compare(Rel.gt, reg0, reg1, resReg));
					instrs.add(new Sltiu(resReg, resReg, 1));
				}
				break;
			case gt:
				if(opd0 instanceof Imm){
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Slti(reg1, resReg, ((Imm)opd0).val));
				}
				else if(opd1 instanceof Imm){
					reg0 = regManager.getUse((Var)opd0);
					resReg = regManager.getDef(res);
					instrs.add(new Li(Reg.$v0, ((Imm)opd1).val));
					instrs.add(new backend.mips.instr.rtype.Compare(Rel.gt, reg0, Reg.$v0, resReg));
				}
				else{
					reg0 = regManager.getUse((Var)opd0);
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new backend.mips.instr.rtype.Compare(rel, reg0, reg1, resReg));
				}
				break;
			case ge:
				if(opd0 instanceof Imm){
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Slti(reg1, resReg, ((Imm)opd0).val + 1));
				}
				else if(opd1 instanceof Imm){
					reg0 = regManager.getUse((Var)opd0);
					resReg = regManager.getDef(res);
					instrs.add(new Li(Reg.$v0, ((Imm)opd1).val - 1));
					instrs.add(new backend.mips.instr.rtype.Compare(Rel.gt, reg0, Reg.$v0, resReg));
				}
				else{
					reg0 = regManager.getUse((Var)opd0);
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new backend.mips.instr.rtype.Compare(Rel.lt, reg0, reg1, resReg));
					instrs.add(new Sltiu(resReg, resReg, 1));
				}
				break;
			case ne:
				if(opd0 instanceof Imm){
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Xori(reg1, resReg, ((Imm)opd0).val));
				}
				else if(opd1 instanceof Imm){
					reg0 = regManager.getUse((Var)opd0);
					resReg = regManager.getDef(res);
					instrs.add(new Xori(reg0, resReg, ((Imm)opd1).val));
				}
				else{
					reg0 = regManager.getUse((Var)opd0);
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Xor(reg0, reg1, resReg));
				}
				break;
			case eq:
				if(opd0 instanceof Imm){
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Xori(reg1, resReg, ((Imm)opd0).val));
					instrs.add(new Sltiu(resReg, resReg, 1));
				}
				else if(opd1 instanceof Imm){
					reg0 = regManager.getUse((Var)opd0);
					resReg = regManager.getDef(res);
					instrs.add(new Xori(reg0, resReg, ((Imm)opd1).val));
					instrs.add(new Sltiu(resReg, resReg, 1));
				}
				else{
					reg0 = regManager.getUse((Var)opd0);
					reg1 = regManager.getUse((Var)opd1);
					resReg = regManager.getDef(res);
					instrs.add(new Xor(reg0, reg1, resReg));
					instrs.add(new Sltiu(resReg, resReg, 1));
				}
				break;
		}
	}
}
