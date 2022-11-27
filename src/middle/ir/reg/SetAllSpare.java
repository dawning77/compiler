package middle.ir.reg;

import backend.mips.instr.*;
import backend.mips.reg.*;
import middle.ir.*;

import java.util.*;

public class SetAllSpare implements ICode{
	public SetAllSpare(){ }

	@Override
	public String toString(){ return "SetAllSpare"; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		regManager.setAllSpare();
		return ret;
	}
}
