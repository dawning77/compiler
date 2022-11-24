package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Input implements ICode{
	public Operand lVal;

	public Input(Operand lVal){
		this.lVal = lVal;
	}

	@Override
	public String toString(){ return lVal + " = Input()"; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg = regManager.get((Var)lVal);
		ret.add(new Li(Reg.$v0, 5));
		ret.add(new Syscall());
		ret.add(new Move(reg, Reg.$v0));
		return ret;
	}
}
