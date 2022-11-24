package middle.ir.calc.unary;

import backend.mips.instr.*;
import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.pseudo.Sw;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Assign implements ICode{
	public Operand lVal;
	public Operand rVal;

	public Assign(Operand lVal, Operand rVal){
		this.lVal = lVal;
		this.rVal = rVal;
	}

	@Override
	public String toString(){ return lVal + " = " + rVal; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		assert lVal instanceof Symbol;
		Reg lValReg = regManager.get((Var)lVal);
		if(rVal instanceof Imm){
			ret.add(new Li(lValReg, ((Imm)rVal).val));
		}
		else if(rVal instanceof Var){
			Reg rValReg = regManager.get((Var)rVal);
			ret.add(new Move(lValReg, rValReg));
		}
		return ret;
	}
}
