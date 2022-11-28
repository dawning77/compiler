package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Input implements ICode{
	public Operand res;

	public Input(Operand res){
		this.res = res;
	}

	@Override
	public String toString(){ return res + " = Input()"; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg reg = regManager.get((Var)res);
		ret.add(new Li(Reg.$v0, 5));
		ret.add(new Syscall());
		ret.add(new Move(reg, Reg.$v0));
		return ret;
	}
}
