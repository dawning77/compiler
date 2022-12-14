package middle.optim;

import middle.func.*;
import middle.ir.*;
import middle.ir.calc.binary.*;
import middle.ir.calc.unary.*;
import middle.ir.func.*;
import middle.ir.io.*;
import middle.ir.mem.*;
import middle.operand.symbol.*;

import java.util.*;

public class FuncOptimizer implements Optimizer{
	private final FuncScope func;

	public FuncOptimizer(FuncScope func){
		this.func = func;
	}

	public void optimize(){
		redundantVarRemove();
		func.liveVarAnalyser.liveVarAnalyse();
		func.liveVarAnalyser.deadCodeRemove();
	}

	private void redundantVarRemove(){
		for(BasicBlock bb: func.bbs){
			HashSet<ICode> toRemove = new HashSet<>();
			for(int i = 0; i < bb.iCodes.size() - 1; i++){
				ICode iCode = bb.iCodes.get(i);
				if(bb.iCodes.get(i + 1) instanceof Assign){
					Symbol def = iCode.def;
					Symbol use = null;
					Symbol def2 = null;
					for(Symbol sym: bb.iCodes.get(i + 1).use){
						use = sym;
						def2 = bb.iCodes.get(i + 1).def;
					}
					if(def != null && def.equals(use)){
						iCode.def = def2;
						if(iCode instanceof Binary) ((Binary)iCode).res = def2;
						else if(iCode instanceof Unary) ((Unary)iCode).res = def2;
						else if(iCode instanceof GetRet) ((GetRet)iCode).res = def2;
						else if(iCode instanceof Input) ((Input)iCode).res = def2;
						else if(iCode instanceof Load) ((Load)iCode).val = def2;
						toRemove.add(bb.iCodes.get(i + 1));
						i++;
					}
				}
			}
			bb.iCodes.removeAll(toRemove);
		}
	}
}
