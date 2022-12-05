package middle.ir.mem;

import backend.mips.instr.itype.*;
import backend.mips.instr.itype.Lw;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

public class Load extends AccessMem{
	public Load(Symbol sym, Operand idx, Operand val){
		super(sym, idx, val);
		def.add((Symbol)val);
		if(idx instanceof Symbol) use.add((Symbol)idx);
		if(sym.type.equals(Symbol.Type.param)) use.add(sym);
	}

	@Override
	public String toString(){ return val + " = " + sym + '[' + idx + ']'; }

	@Override
	public void genInstr(RegManager regManager){
		Reg valReg;
		assert val instanceof Var;
		switch(sym.type){
			case local:
				valReg = regManager.getDef((Var)val);
				if(idx instanceof Imm)
					instrs.add(new Lw(Reg.$sp, valReg, (sym.loc + ((Imm)idx).val) * 4));
				else if(idx instanceof Var){
					loadOffset((Var)idx, regManager);
					instrs.add(new Addi(Reg.$v0, Reg.$v0, sym.loc * 4));
					instrs.add(new Add(Reg.$v0, Reg.$sp, Reg.$v0));
					instrs.add(new Lw(Reg.$v0, valReg, 0));
				}
				break;
			case global:
				valReg = regManager.getDef((Var)val);
				if(idx instanceof Imm)
					instrs.add(new backend.mips.instr.pseudo.Lw(valReg, sym.name, ((Imm)idx).val * 4));
				else if(idx instanceof Var){
					loadOffset((Var)idx, regManager);
					instrs.add(new backend.mips.instr.pseudo.Lw(valReg, sym.name, Reg.$v0));
				}
				break;
			case param:
				Reg addr = regManager.getUse(sym);
				valReg = regManager.getDef((Var)val);
				if(idx instanceof Imm)
					instrs.add(new Lw(addr, valReg, ((Imm)idx).val * 4));
				else if(idx instanceof Var){
					loadOffset((Var)idx, regManager);
					instrs.add(new Add(Reg.$v0, addr, Reg.$v0));
					instrs.add(new Lw(Reg.$v0, valReg, 0));
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
