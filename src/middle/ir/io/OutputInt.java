package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class OutputInt implements ICode{
	public Operand opd0;

	public OutputInt(Operand opd0){
		this.opd0 = opd0;
	}

	@Override
	public String toString(){ return "Output " + opd0; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		if(opd0 instanceof Imm){
			regManager.setSpare(Reg.$a0);   // write back
			ret.add(new Li(Reg.$a0, ((Imm)opd0).val));
		}
		else if(opd0 instanceof Var){
			Reg reg = regManager.get((Var)opd0);
			if(reg != Reg.$a0){
				regManager.setSpare(Reg.$a0);   // write back
				ret.add(new Move(Reg.$a0, reg));
			}
		}
		ret.add(new Li(Reg.$v0, 1));
		ret.add(new Syscall());
		return ret;
	}
}
