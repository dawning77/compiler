package middle.ir.func;

import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Ret extends ICode{
	public Operand opd0;

	public Ret(Operand opd0){
		super();
		this.opd0 = opd0;
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
	}

	@Override
	public String toString(){ return "Ret " + opd0; }

	@Override
	public void genInstr(RegManager regManager){
		regManager.setAllGlobalSpare();
		if(opd0 != null){
			if(opd0 instanceof Imm){
				instrs.add(new Li(Reg.$v0, ((Imm)opd0).val));
			}
			else if(opd0 instanceof Var){
				Reg reg = regManager.getUse((Var)opd0);
				instrs.add(new Move(Reg.$v0, reg));
			}
		}
		instrs.add(new Jr(Reg.$ra));
	}
}