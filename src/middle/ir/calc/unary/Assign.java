package middle.ir.calc.unary;

import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Assign extends Unary{
	public Assign(Symbol res, Operand opd0){ super(opd0, res); }

	@Override
	public Integer calc(){
		if(opd0 instanceof Imm) return ((Imm)opd0).val;
		else return null;
	}

	@Override
	public String toString(){ return res + " = " + opd0; }

	@Override
	public void genInstr(RegManager regManager){
		Reg resReg;
		if(opd0 instanceof Imm){
			resReg = regManager.getDef(res);
			instrs.add(new Li(resReg, ((Imm)opd0).val));
		}
		else if(opd0 instanceof Var){
			if(opd0.equals(res)) return;
			Reg reg0 = regManager.getUse((Var)opd0);
			resReg = regManager.getDef(res);
			if(!reg0.equals(resReg)) instrs.add(new Move(resReg, reg0));
		}
	}
}
