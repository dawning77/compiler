package middle.ir;

import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Label extends ICode{
	public String val;

	public Label(String val){
		super();
		this.val = val;
	}

	@Override
	public void changeDef(Symbol newDef){ }

	@Override
	public void changeUse(Symbol oldUse, Operand newUse){ }

	@Override
	public String toString(){ return val + ":"; }

	@Override
	public void genInstr(RegManager regManager){
		instrs.add(new backend.mips.instr.Label(val));
	}
}
