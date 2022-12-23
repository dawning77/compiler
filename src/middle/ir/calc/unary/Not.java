package middle.ir.calc.unary;

import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Not extends Unary{
	public Not(Operand opd0, Symbol res){ super(opd0, res); }

	@Override
	public Integer calc(){ return null; }

	@Override
	public String toString(){ return res + " = !" + opd0; }

	@Override
	public void genInstr(RegManager regManager){
		Reg reg0;
		if(opd0 instanceof Imm){
			instrs.add(new Li(Reg.$v0, ((Imm)opd0).val));
			reg0 = Reg.$v0;
		}
		else reg0 = regManager.getUse((Var)opd0);
		Reg resReg = regManager.getDef(res);
		instrs.add(new Sltiu(reg0, resReg, 1));
	}
}
