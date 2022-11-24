package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.pseudo.Lw;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class OutputInt implements ICode{
	public Operand val;

	public OutputInt(Operand val){
		this.val = val;
	}

	@Override
	public String toString(){ return "Output " + val; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		if(val instanceof Imm){
			regManager.setSpare(Reg.$a0);   // write back
			ret.add(new Li(Reg.$a0, ((Imm)val).val));
		}
		else if(val instanceof Var){
			Reg reg = regManager.get((Var)val);
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
