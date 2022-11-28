package middle.ir.func;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Ret implements ICode{
	public Operand opd0;

	public Ret(Operand opd0){
		this.opd0 = opd0;
	}

	@Override
	public String toString(){ return "Ret " + opd0; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		regManager.setAllGlobalSpare();
		if(opd0 != null){
			if(opd0 instanceof Imm){
				ret.add(new Li(Reg.$v0, ((Imm)opd0).val));
			}
			else if(opd0 instanceof Var){
				Reg reg = regManager.get((Var)opd0);
				ret.add(new Move(Reg.$v0, reg));
			}
		}
		ret.add(new Jr(Reg.$ra));
		return ret;
	}
}