package middle.ir.func;

import backend.mips.instr.itype.*;
import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.func.*;
import middle.ir.*;

public class Call extends ICode{
	public String funcName;

	public Call(String funcName){
		super();
		this.funcName = funcName;
	}

	@Override
	public String toString(){ return "Call " + funcName; }

	@Override
	public void genInstr(RegManager regManager){
		regManager.setAllSpare();
		instrs.add(new Jal(funcName));
		FuncScope func = regManager.mipsManager.funcNameMap.get(funcName);
		instrs.add(new Addi(Reg.$sp, Reg.$sp, (func.frameSize + func.paramSize + 1) * 4));
		instrs.add(new Lw(Reg.$sp, Reg.$ra, -4));
	}
}
