package middle.ir.br;

import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

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
				regManager.setAllSpare();
				if((((Imm)opd0).val == 0) == inv) instrs.add(new J(bb.toString()));
			}
			else if(opd0 instanceof Var){
				Reg reg = regManager.getUse((Var)opd0);
				if(((Var)opd0).type.equals(Symbol.Type.tmp)) regManager.setAllSpareExcept(reg);
				else regManager.setAllSpare();
				rel = inv? Rel.eq: Rel.eq.inverse();
				instrs.add(new backend.mips.instr.pseudo.Br(reg, Reg.$zero, rel, bb.toString()));
				if(((Var)opd0).type.equals(Symbol.Type.tmp)){
					regManager.setSpareNoStore(reg);
					regManager.initSapre();
				}
			}
		}
	}
}
