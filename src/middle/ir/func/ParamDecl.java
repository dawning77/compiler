package middle.ir.func;

import backend.mips.instr.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.symbol.*;

import java.util.*;

public class ParamDecl implements ICode{
	public ArrayList<Symbol> params;

	public ParamDecl(ArrayList<Symbol> params){ this.params = params; }

	@Override
	public String toString(){ return "Decl params"; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		for(int i = 0; i < params.size(); i++){
			Symbol param = params.get(i);
			if(i < 4){
				Reg src = Reg.valueOf("$a" + i);
				regManager.setUsed(src, param);
			}
			else{
				regManager.curState.inStack.add(param);
			}
		}
		return ret;
	}
}
