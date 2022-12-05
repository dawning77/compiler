package middle.ir.reg;

import backend.mips.reg.*;
import middle.ir.*;

public class SetAllSpare extends ICode{
	public SetAllSpare(){
		super();
	}

	@Override
	public String toString(){ return "SetAllSpare"; }

	@Override
	public void genInstr(RegManager regManager){ regManager.setAllSpare(); }
}
