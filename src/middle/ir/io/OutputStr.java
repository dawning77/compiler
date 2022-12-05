package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;

public class OutputStr extends ICode{
	public int strId;

	public OutputStr(int strId){
		super();
		this.strId = strId;
	}

	@Override
	public String toString(){ return "Output str" + strId; }

	@Override
	public void genInstr(RegManager regManager){
		instrs.add(new La(Reg.$a0, "s" + strId));
		instrs.add(new Li(Reg.$v0, 4));
		instrs.add(new Syscall());
	}
}
