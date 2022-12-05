package middle.ir.calc.unary;

import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.operand.Operand;
import middle.operand.symbol.*;

public class Not extends Unary{
	public Not(Operand opd0, Operand res){ super(opd0, res); }

	@Override
	public String toString(){ return res + " = !" + opd0; }

	@Override
	public void genInstr(RegManager regManager){
		Reg reg = regManager.getUse((Var)opd0);
		Reg resReg = regManager.getDef((Var)res);
		instrs.add(new Compare(Rel.eq, reg, Reg.$zero, resReg));
	}
}
