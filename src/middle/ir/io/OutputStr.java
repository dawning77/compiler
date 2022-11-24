package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;

import java.util.*;

public class OutputStr implements ICode{
	public int strId;

	public OutputStr(int strId){
		this.strId = strId;
	}

	@Override
	public String toString(){ return  "Output str" + strId; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		regManager.setSpare(Reg.$a0);   // write back
		ret.add(new La(Reg.$a0, "s" + strId));
		ret.add(new Li(Reg.$v0,4));
		ret.add(new Syscall());
		return ret;
	}
}
