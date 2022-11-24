package middle.ir;

import backend.mips.instr.*;
import backend.mips.reg.*;

import java.util.*;

public class Label implements ICode{
	public String val;

	public Label(String val){
		this.val = val;
	}

	@Override
	public String toString(){ return val + ":"; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		ret.add(new backend.mips.instr.Label(val));
		return ret;
	}
}
