package middle.ir.br;

import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Jmp extends ICode{
	public BasicBlock bb;

	public Jmp(BasicBlock bb){
		super();
		this.bb = bb;
	}

	@Override
	public void changeUse(Symbol oldUse, Operand newUse){ }

	@Override
	public void changeDef(Symbol newDef){ }

	@Override
	public String toString(){ return "Jmp " + bb; }

	@Override
	public void genInstr(RegManager regManager){
		regManager.setAllTmpRegSpare();
		instrs.add(new J(bb.toString()));
	}
}
