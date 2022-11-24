package middle.ir.mem;

import backend.mips.instr.*;
import backend.mips.instr.itype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Store extends AccessMem implements ICode{
	public Store(Symbol sym, Operand idx, Operand val){
		super(sym, idx, val);
	}

	@Override
	public String toString(){ return sym + "[" + idx + "]  = " + val; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		Reg valReg;
		if(val instanceof Imm){
			ret.add(new Li(Reg.$v0, ((Imm)val).val));
			valReg = Reg.$v0;
		}
		else valReg = regManager.get((Var)val);
		switch(sym.type){
			case local:
				if(idx instanceof Imm)
					ret.add(new backend.mips.instr.itype.Sw(Reg.$fp, valReg, (sym.loc + ((Imm)idx).val) * 4));
				else if(idx instanceof Var){
					Reg idxReg = regManager.get((Symbol)idx);
					ret.add(new Move(Reg.$v1, idxReg));
					ret.add(new Sll(Reg.$v1, Reg.$v1, 2));
					ret.add(new Addi(Reg.$v1, Reg.$v1, sym.loc * 4));
					ret.add(new Add(Reg.$v1, Reg.$fp, Reg.$v1));
					ret.add(new backend.mips.instr.itype.Sw(Reg.$v1, valReg, 0));
				}
				break;
			case global:
				if(idx instanceof Imm)
					ret.add(new backend.mips.instr.pseudo.Sw(valReg, sym.name, ((Imm)idx).val * 4));
				else if(idx instanceof Var){
					Reg idxReg = regManager.get((Symbol)idx);
					ret.add(new Move(Reg.$v1, idxReg));
					ret.add(new Sll(Reg.$v1, Reg.$v1, 2));
					ret.add(new backend.mips.instr.pseudo.Sw(valReg, sym.name, Reg.$v1));
				}
				break;
			case param:
				Reg addr = regManager.get(sym);
				if(idx instanceof Imm){
					ret.add(new backend.mips.instr.itype.Sw(addr, valReg, ((Imm)idx).val * 4));
				}
				else if(idx instanceof Var){
					Reg idxReg = regManager.get((Symbol)idx);
					ret.add(new Move(Reg.$v1, idxReg));
					ret.add(new Sll(Reg.$v1, Reg.$v1, 2));
					ret.add(new Add(Reg.$v1, addr, Reg.$v1));
					ret.add(new backend.mips.instr.itype.Sw(Reg.$v1, valReg, 0));
				}
				break;
			default:
				break;
		}
		return ret;
	}
}
