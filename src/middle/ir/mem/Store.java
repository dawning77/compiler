package middle.ir.mem;

import backend.mips.instr.itype.*;
import backend.mips.instr.itype.Sw;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Store extends AccessMem{
	public Store(Symbol sym, Operand idx, Operand val){
		super(sym, idx, val);
		if(val instanceof Symbol) use.add((Symbol)val);
		if(idx instanceof Symbol) use.add((Symbol)idx);
		if(sym.type.equals(Symbol.Type.param)) use.add(sym);
	}

	@Override
	public String toString(){ return sym + "[" + idx + "]  = " + val; }

	@Override
	public void genInstr(RegManager regManager){
		Reg valReg;
		if(val instanceof Imm){
			instrs.add(new Li(Reg.$a0, ((Imm)val).val));
			valReg = Reg.$a0;
		}
		else valReg = regManager.getUse((Var)val);
		switch(sym.type){
			case local:
				if(idx instanceof Imm)
					instrs.add(new Sw(Reg.$sp, valReg, (sym.loc + ((Imm)idx).val) * 4));
				else if(idx instanceof Var){
					loadOffset((Var)idx, regManager);
					instrs.add(new Addi(Reg.$v0, Reg.$v0, sym.loc * 4));
					instrs.add(new Add(Reg.$v0, Reg.$sp, Reg.$v0));
					instrs.add(new Sw(Reg.$v0, valReg, 0));
				}
				break;
			case global:
				if(idx instanceof Imm)
					instrs.add(new backend.mips.instr.pseudo.Sw(valReg, sym.name, ((Imm)idx).val * 4));
				else if(idx instanceof Var){
					loadOffset((Var)idx, regManager);
					instrs.add(new backend.mips.instr.pseudo.Sw(valReg, sym.name, Reg.$v0));
				}
				break;
			case param:
				Reg addr = regManager.getUse(sym);
				if(idx instanceof Imm)
					instrs.add(new Sw(addr, valReg, ((Imm)idx).val * 4));
				else if(idx instanceof Var){
					loadOffset((Var)idx, regManager);
					instrs.add(new Add(Reg.$v0, addr, Reg.$v0));
					instrs.add(new Sw(Reg.$v0, valReg, 0));
				}
				break;
			default:
				break;
		}
	}

	private void loadOffset(Var idx, RegManager regManager){
		Reg idxReg = regManager.getUse(idx);
		instrs.add(new Move(Reg.$v0, idxReg));
		instrs.add(new Sll(Reg.$v0, Reg.$v0, 2));
	}
}
