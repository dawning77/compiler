package middle.ir.br;

import backend.mips.instr.*;
import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Br implements ICode{
	public Operand opd0;
	public Operand opd1;
	public Rel rel;
	public boolean inv;
	public BasicBlock bb;

	//	public Br(Operand opd0, Operand opd1, Rel rel, boolean inv, BasicBlock bb){
	//		this.opd0 = opd0;
	//		this.opd1 = opd1;
	//		this.rel = rel;
	//		this.inv = inv;
	//		this.bb = bb;
	//	}

	public Br(Operand opd0, boolean inv, BasicBlock bb){
		this.opd0 = opd0;
		this.opd1 = null;
		this.rel = null;
		this.inv = inv;
		this.bb = bb;
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
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		if(rel == null){
			if(opd0 instanceof Imm){
				regManager.setAllSpare();
				if((((Imm)opd0).val == 0) == inv){
					ret.add(new J(bb.toString()));
				}
			}
			else if(opd0 instanceof Var){
				Reg reg = regManager.get((Var)opd0);
				regManager.setAllSpare();
				rel = inv? Rel.eq: Rel.eq.inverse();
				ret.add(new backend.mips.instr.pseudo.Br(reg, Reg.$zero, rel, bb.toString()));
			}
		}
		//		else{
		//			if(opd0 instanceof Imm && opd1 instanceof Imm){
		//				int val0 = ((Imm)opd0).val;
		//				int val1 = ((Imm)opd1).val;
		//				if(rel.satisfied(val0, val1) != inv){
		//					ret.add(new J(bb.toString()));
		//				}
		//				return ret;
		//			}
		//			Reg reg0;
		//			Reg reg1;
		//			if(opd0 instanceof Imm){
		//				reg0 = Reg.$v1;
		//				reg1 = regManager.get((Var)opd1);
		//				ret.add(new Li(Reg.$v1, ((Imm)opd0).val));
		//			}
		//			else if(opd1 instanceof Imm){
		//				reg0 = regManager.get((Var)opd0);
		//				reg1 = Reg.$v1;
		//				ret.add(new Li(Reg.$v1, ((Imm)opd1).val));
		//			}
		//			else{
		//				reg0 = regManager.get((Var)opd0);
		//				reg1 = regManager.get((Var)opd1);
		//			}
		//			ret.add(new backend.mips.instr.pseudo.Br(
		//					reg0, reg1,
		//					inv? rel.inverse(): rel,
		//					bb.toString()));
		//		}
		return ret;
	}
}
