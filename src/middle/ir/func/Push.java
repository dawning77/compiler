package middle.ir.func;

import backend.mips.instr.itype.*;
import backend.mips.instr.itype.Mul;
import backend.mips.instr.itype.Sw;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Push extends ICode{
	public ArrayList<Operand> params;

	public Push(ArrayList<Operand> params){
		super();
		this.params = params;
		for(Operand p: params){
			if(p instanceof Var) use.add((Symbol)p);
			else if((p instanceof Arr && !(p instanceof SubMat)) ||
			        p instanceof Mat){
				if(((Symbol)p).type.equals(Symbol.Type.param)) use.add((Symbol)p);
			}
			else if(p instanceof SubMat){
				if(((SubMat)p).idx instanceof Var) use.add((Symbol)p);
				if(((SubMat)p).mat.type.equals(Symbol.Type.param)) use.add(((SubMat)p).mat);
			}
		}
	}

	@Override
	public String toString(){ return "Push params"; }

	@Override
	public void genInstr(RegManager regManager){
		instrs.add(new Sw(Reg.$sp, Reg.$ra, -4));
		for(int i = 0; i < params.size(); i++){
			int paramLoc = -(params.size() - i + 1);
			Operand param = params.get(i);
			if(param instanceof Imm){
				instrs.add(new Li(Reg.$v0, ((Imm)param).val));
				instrs.add(new Sw(Reg.$sp, Reg.$v0, paramLoc * 4));
				continue;
			}
			Symbol paramSym = (Symbol)param;
			if(paramSym instanceof Var){
				Reg reg = regManager.getUse(paramSym);
				instrs.add(new Sw(Reg.$sp, reg, paramLoc * 4));
			}
			else if((paramSym instanceof Arr && !(paramSym instanceof SubMat)) ||
			        paramSym instanceof Mat){
				switch(paramSym.type){
					case local:
						instrs.add(new Addi(Reg.$sp, Reg.$v0, paramSym.loc * 4));
						instrs.add(new Sw(Reg.$sp, Reg.$v0, paramLoc * 4));
						break;
					case global:
						instrs.add(new La(Reg.$v0, paramSym.name));
						instrs.add(new Sw(Reg.$sp, Reg.$v0, paramLoc * 4));
						break;
					case param:
						Reg reg = regManager.getUse(paramSym);
						instrs.add(new Sw(Reg.$sp, reg, paramLoc * 4));
					default:
						break;
				}
			}
			else if(paramSym instanceof SubMat){
				SubMat paramSubMat = (SubMat)paramSym;
				Mat mat = paramSubMat.mat;
				if(paramSubMat.idx instanceof Imm){
					int offset = mat.innerLen * ((Imm)(paramSubMat.idx)).val;
					switch(mat.type){
						case local:
							instrs.add(new Addi(Reg.$sp, Reg.$v0, (mat.loc + offset) * 4));
							break;
						case global:
							instrs.add(new La(Reg.$v0, mat.name, offset * 4));
							break;
						case param:
							Reg matReg = regManager.getUse(mat);
							instrs.add(new Addi(matReg, Reg.$v0, offset * 4));
							break;
						default:
							break;
					}
					instrs.add(new Sw(Reg.$sp, Reg.$v0, paramLoc * 4));
				}
				else if(paramSubMat.idx instanceof Var){
					Reg idxReg = regManager.getUse((Symbol)paramSubMat.idx);
					if(Utils.isPowerOf2(mat.innerLen * 4))
						instrs.add(new Sll(idxReg, Reg.$v0, Utils.log2I(mat.innerLen * 4)));
					else instrs.add(new Mul(idxReg, Reg.$v0, mat.innerLen * 4));
					switch(mat.type){
						case local:
							instrs.add(new Addi(Reg.$v0, Reg.$v0, mat.loc * 4));
							instrs.add(new Add(Reg.$v0, Reg.$sp, Reg.$v0));
							break;
						case global:
							instrs.add(new La(Reg.$v0, mat.name, Reg.$v0));
							break;
						case param:
							Reg matReg = regManager.getUse(mat);
							instrs.add(new Add(matReg, Reg.$v0, Reg.$v0));
							break;
						default:
							break;
					}
					instrs.add(new Sw(Reg.$sp, Reg.$v0, paramLoc * 4));
				}
			}
		}
	}
}
