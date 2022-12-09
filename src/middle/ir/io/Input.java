package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.symbol.*;

public class Input extends ICode{
	public Symbol res;

	public Input(Symbol res){
		super();
		this.res = res;
		def = res;
	}

	@Override
	public String toString(){ return res + " = Input()"; }

	@Override
	public void genInstr(RegManager regManager){
		Reg resReg = regManager.getDef(res);
		instrs.add(new Li(Reg.$v0, 5));
		instrs.add(new Syscall());
		instrs.add(new Move(resReg, Reg.$v0));
	}
}
