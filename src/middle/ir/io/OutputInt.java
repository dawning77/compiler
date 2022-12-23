package middle.ir.io;

import backend.mips.instr.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class OutputInt extends ICode{
	public Operand opd0;

	public OutputInt(Operand opd0){
		super();
		this.opd0 = opd0;
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
	}

	@Override
	public void changeUse(Symbol oldUse, Operand newUse){
		if(opd0 instanceof Symbol && opd0.equals(oldUse)) {
			opd0 = newUse;
			use = new HashSet<>();
			if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		}
	}

	@Override
	public void changeDef(Symbol newDef){ }

	@Override
	public String toString(){ return "Output " + opd0; }

	@Override
	public void genInstr(RegManager regManager){
		if(opd0 instanceof Imm) instrs.add(new Li(Reg.$a0, ((Imm)opd0).val));
		else if(opd0 instanceof Var){
			Reg reg = regManager.getUse((Var)opd0);
			instrs.add(new Move(Reg.$a0, reg));
		}
		instrs.add(new Li(Reg.$v0, 1));
		instrs.add(new Syscall());
	}
}
