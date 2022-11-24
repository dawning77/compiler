package middle.ir.func;

import backend.*;
import backend.mips.instr.*;
import backend.mips.instr.itype.*;
import backend.mips.instr.itype.Lw;
import backend.mips.instr.itype.Mul;
import backend.mips.instr.itype.Sw;
import backend.mips.instr.pseudo.*;
import backend.mips.instr.rtype.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public class Push implements ICode{
	public ArrayList<Operand> params;

	public Push(ArrayList<Operand> params){
		this.params = params;
	}

	@Override
	public String toString(){ return "Push params"; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		if(params.size() == 0) return ret;
		ret.add(new Addi(Reg.$sp, Reg.$sp, -params.size() * 4));
		for(int i = 0; i < params.size(); i++){
			Operand param = params.get(i);
			Reg tar = i < 4? Reg.valueOf("$a" + i): Reg.$v1;
			if(param instanceof Imm){
				ret.add(new Li(tar, ((Imm)param).val));
				if(tar.equals(Reg.$v1)) ret.add(new Sw(Reg.$sp, Reg.$v1, i * 4));
				continue;
			}
			assert param instanceof Symbol;
			Symbol paramSym = (Symbol)param;
			if(paramSym instanceof Var){
				Reg reg = regManager.getParam(paramSym);
				ret.add(safeLoad(reg, tar, regManager.mipsManager));
			}
			else if((paramSym instanceof Arr && !(paramSym instanceof SubMat)) ||
			        paramSym instanceof Mat){
				switch(paramSym.type){
					case local:
						ret.add(new Addi(Reg.$fp, tar, paramSym.loc * 4));
						break;
					case global:
						ret.add(new La(tar, paramSym.name));
						break;
					case param:
						Reg reg = regManager.getParam(paramSym);
						ret.add(safeLoad(reg, tar, regManager.mipsManager));
					default:
						break;
				}
			}
			else if(paramSym instanceof SubMat){
				Mat mat = ((SubMat)param).mat;
				SubMat paramSubMat = (SubMat)paramSym;
				if(paramSubMat.idx instanceof Imm){
					int offset = mat.innerLen * ((Imm)(paramSubMat.idx)).val;
					switch(mat.type){
						case local:
							ret.add(new Addi(Reg.$fp, tar, (mat.loc + offset) * 4));
							break;
						case global:
							ret.add(new La(Reg.$v0, mat.name));
							ret.add(new Addi(Reg.$v0, tar, offset * 4));
							break;
						case param:
							Reg matReg = regManager.getParam(mat);
							ret.add(safeLoad(matReg, Reg.$v0, regManager.mipsManager));
							ret.add(new Addi(Reg.$v0, tar, offset * 4));
							break;
						default:
							break;
					}
				}
				else if(paramSubMat.idx instanceof Var){
					Reg idxReg = regManager.getParam((Symbol)paramSubMat.idx);
					ret.add(new Mul(idxReg, Reg.$v0, mat.innerLen * 4));
					switch(mat.type){
						case local:
							ret.add(new Addi(Reg.$v0, Reg.$v0, mat.loc * 4));
							ret.add(new Add(Reg.$v0, Reg.$fp, tar));
							break;
						case global:
							ret.add(new La(Reg.$v1, mat.name));
							ret.add(new Add(Reg.$v0, Reg.$v1, tar));
							break;
						case param:
							Reg matReg = regManager.getParam(mat);
							ret.add(safeLoad(matReg, Reg.$v1, regManager.mipsManager));
							ret.add(new Add(Reg.$v0, Reg.$v1, tar));
							break;
						default:
							break;
					}
				}
			}
			if(tar.equals(Reg.$v1)) ret.add(new Sw(Reg.$sp, Reg.$v1, i * 4));
		}
		return ret;
	}

	private Instr safeLoad(Reg src, Reg tar, MipsManager mipsManager){
		if(mipsManager.savedLoc.containsKey(src) &&
		   (src.equals(Reg.$a0) || src.equals(Reg.$a1) || src.equals(Reg.$a2) || src.equals(Reg.$a3))){
			// maybe changed in the push procedure, need to load from stack
			int offset = mipsManager.regSize - mipsManager.savedLoc.get(src) - 1 + params.size();
			return new Lw(Reg.$sp, tar, offset * 4);
		}
		else return new Move(tar, src);
	}
}
