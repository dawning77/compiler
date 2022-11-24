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
	public Operand res;

	public Ret(Operand res){
		this.res = res;
	}

	@Override
	public String toString(){ return "Ret " + res; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		regManager.setAllGlobalSpare();
		if(res != null){
			if(res instanceof Imm){
				ret.add(new Li(Reg.$v0, ((Imm)res).val));
			}
			else if(res instanceof Var){
				Reg reg = regManager.get((Var)res);
				ret.add(new Move(Reg.$v0, reg));
			}
		}
		ret.add(new Jr(Reg.$ra));
		return ret;
	}
}