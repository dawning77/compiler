package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Input extends ICode{
	public Operand res;

	public Input(Operand res){
		super();
		this.res = res;
		def.add((Symbol)res);
	}

	@Override
	public String toString(){ return res + " = Input()"; }

	@Override
	public void genInstr(RegManager regManager){
		Reg reg = regManager.getDef((Var)res);
		instrs.add(new Li(Reg.$v0, 5));
		instrs.add(new Syscall());
		instrs.add(new Move(reg, Reg.$v0));
	}
}
