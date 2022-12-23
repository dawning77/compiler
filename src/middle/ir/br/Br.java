package middle.ir.br;

import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Br extends ICode{
	public Operand opd0;
	public Operand opd1;
	public Rel rel;
	public boolean inv;
	public BasicBlock bb;

	public Br(Operand opd0, boolean inv, BasicBlock bb){
		super();
		this.opd0 = opd0;
		this.opd1 = null;
		this.rel = null;
		this.inv = inv;
		this.bb = bb;
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
	}

	@Override
	public void changeUse(Symbol oldUse, Operand newUse){
		if(opd0 instanceof Symbol && opd0.equals(oldUse)){
			opd0 = newUse;
			use = new HashSet<>();
			if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		}
	}

	@Override
	public void changeDef(Symbol newDef){ }

	@Override
	public String toString(){
		String ret = "If" + (inv? "Not ": " ");
		if(rel == null) ret += opd0;
		else ret += opd0 + " " + rel + " " + opd1;
		ret += " goto " + bb;
		return ret;
	}

	@Override
	public void genInstr(RegManager regManager){
		if(rel == null){
			if(opd0 instanceof Imm){
				regManager.setAllTmpRegSpare();
				if((((Imm)opd0).val == 0) == inv) instrs.add(new J(bb.toString()));
			}
			else if(opd0 instanceof Var){
				Reg reg = regManager.getUse((Var)opd0);
				if(((Var)opd0).type.equals(Symbol.Type.tmp) && !regManager.globalRegManager.allocGlobal((Var)opd0))
					regManager.setAllSpareExcept(reg);
				else regManager.setAllTmpRegSpare();
				rel = inv? Rel.eq: Rel.eq.inverse();
				instrs.add(new backend.mips.instr.pseudo.Br(reg, Reg.$zero, rel, bb.toString()));
				if(((Var)opd0).type.equals(Symbol.Type.tmp) && !regManager.globalRegManager.allocGlobal((Var)opd0)){
					regManager.setSpareNoStore(reg);
					regManager.tmpRegManager.initSpare();
				}
			}
		}
	}
}
