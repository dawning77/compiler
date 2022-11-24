package middle.ir.func;

import backend.mips.instr.*;
import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.ir.*;

import java.util.*;

public class Call implements ICode{
	public String funcName;

	public Call(String funcName){
		this.funcName = funcName;
	}

	@Override
	public String toString(){ return "Call " + funcName; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		ret.add(new Jal(funcName));
		ret.add(new Nop());
		return ret;
	}
}
