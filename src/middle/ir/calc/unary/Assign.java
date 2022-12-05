package middle.ir.calc.unary;

import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Assign extends Unary{
	public Assign(Operand res, Operand opd0){ super(opd0, res); }

	@Override
	public String toString(){ return res + " = " + opd0; }

	@Override
	public void genInstr(RegManager regManager){
		assert res instanceof Symbol;
		Reg lValReg;
		if(opd0 instanceof Imm){
			lValReg = regManager.getDef((Var)res);
			instrs.add(new Li(lValReg, ((Imm)opd0).val));
		}
		else if(opd0 instanceof Var){
			if(opd0.equals(res)) return;
			Reg rValReg = regManager.getUse((Var)opd0);
			lValReg = regManager.getDef((Var)res);
			instrs.add(new Move(lValReg, rValReg));
		}
	}
}
