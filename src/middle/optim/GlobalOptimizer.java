package middle.optim;

import middle.*;
import middle.func.*;
import java.util.*;

public class GlobalOptimizer implements Optimizer{
	public final HashMap<String, FuncScope> funcNameMap;
	public final ArrayList<FuncScope> funcs;
	public final FuncScope mainFunc;

	public GlobalOptimizer(ICodeManager iCodeManager){
		this.funcNameMap = iCodeManager.funcNameMap;
		this.funcs = iCodeManager.funcs;
		this.mainFunc = iCodeManager.mainFunc;
	}

	@Override
	public void optimize(){
//		inlineFuncs();
		mainFunc.funcOptimizer.optimize();
		funcs.forEach(f->f.funcOptimizer.optimize());
	}

//	private void inlineFuncs(){
//		funcs.forEach(this::inlineFunc);
//		inlineFunc(mainFunc);
//	}

//	private void inlineFunc(FuncScope func){
//		for(int i = 0; i < func.bbs.size(); i++){
//
//		}
//		for(BasicBlock bb: func.bbs){
//			for(int i = 0; i < bb.iCodes.size(); i++){
//				ICode iCode = bb.iCodes.get(i);
//				if(iCode instanceof Push&& ((Call)bb.iCodes.get(i+1)).funcName.equals(func.name)){
//
//				}
//			}
//		}
//	}
}
