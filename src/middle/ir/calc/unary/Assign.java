package middle.ir.calc.unary;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Assign extends Unary{
	public Assign(Operand res, Operand opd0){ super(opd0, res); }

	@Override
	public String toString(){ return res + " = " + opd0; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		assert res instanceof Symbol;
		Reg lValReg = regManager.get((Var)res);
		if(opd0 instanceof Imm){
			ret.add(new Li(lValReg, ((Imm)opd0).val));
		}
		else if(opd0 instanceof Var){
			Reg rValReg = regManager.get((Var)opd0);
			ret.add(new Move(lValReg, rValReg));
		}
		return ret;
	}
}
