package middle.ir.reg;

import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class SetAllSpare extends ICode{
	public SetAllSpare(){
		super();
	}

	@Override
	public void changeUse(Symbol oldUse, Operand newUse){ }

	@Override
	public void changeDef(Symbol newDef){ }

	@Override
	public String toString(){ return "SetAllSpare"; }

	@Override
	public void genInstr(RegManager regManager){ regManager.setAllTmpRegSpare(); }
}
