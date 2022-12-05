package backend;

import backend.mips.instr.*;
import backend.mips.instr.Label;
import backend.mips.instr.itype.*;
import backend.mips.instr.jtype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.*;
import middle.func.*;
import middle.ir.*;
import middle.operand.symbol.*;

import java.util.*;

public class MipsManager{
	public final HashMap<String, FuncScope> funcNameMap;
	public final ArrayList<FuncScope> funcs;
	public final FuncScope mainFunc;
	public final HashMap<String, Integer> strs;
	public final HashMap<Symbol, GlobalVarInfo> globalVars;

	public final RegManager regManager;

	public FuncScope curFunc;
	public BasicBlock curBB;
	public ICode curIR;

	private final StringBuilder mips;
	public String indent = "";
	public static final boolean DEBUG = false;

	public MipsManager(ICodeManager iCodeManager){
		this.funcNameMap = iCodeManager.funcNameMap;
		this.funcs = iCodeManager.funcs;
		this.mainFunc = iCodeManager.mainFunc;
		this.strs = iCodeManager.strs;
		this.globalVars = iCodeManager.globalVars;
		this.regManager = new RegManager(this);
		this.mips = new StringBuilder();
		this.curFunc = null;
		this.curBB = null;
		this.curIR = null;
	}

	public String getMips(){ return mips.toString(); }

	public void genMips(){
		mips.append(".data\n");
		indent += '\t';
		genGlobalVars();
		genStrs();
		mips.append("\n.text\n")
				.append(new Jal("main")).append('\n')
				.append(new Li(Reg.$v0, 10)).append('\n')
				.append(new Syscall()).append("\n\n");
		indent = "";
		genFunc(mainFunc);
		genFuncs();
	}

	private void genGlobalVars(){
		globalVars.forEach((sym, info)->{
			mips.append(indent).append(sym.name).append(':');
			if(info.initialized){
				mips.append(" .word ");
				for(int i = 0; i < info.vals.size(); i++){
					mips.append(info.vals.get(i));
					if(i != info.size - 1) mips.append(", ");
				}
			}
			else mips.append(" .space ").append(4 * info.size);
			mips.append('\n');
		});
	}

	private void genStrs(){
		strs.forEach((s, i)->mips.append(indent).append('s').append(i).append(':')
				.append(" .asciiz ").append('"').append(s).append('"').append('\n'));
	}

	private void genFuncs(){ funcs.forEach(this::genFunc); }

	private void genFunc(FuncScope func){
		curFunc = func;
		func.liveVarAnalyser.liveVarAnalyse();
		regManager.init();
		globalVars.keySet().forEach(regManager::addToStack);
		func.params.forEach(regManager::addToStack);
		genInstr(new middle.ir.Label(curFunc.name));
		genInstr(new middle.ir.Label("# param begin from " + curFunc.frameSize * 4));
		genInstr(new Addi(Reg.$sp, Reg.$sp,
		                  -(curFunc.frameSize + curFunc.paramSize + 1) * 4));
		for(BasicBlock bb: func.bbs){
			curBB = bb;
			curBB.iCodes.forEach(this::genInstr);
		}
		mips.append('\n');
	}

	private void genInstr(ICode iCode){
		curIR = iCode;
		if(DEBUG) System.out.println('\n' + iCode.toString());
		iCode.genInstr(regManager);
		iCode.instrs.forEach(this::genInstr);
		if(DEBUG) System.out.println(regManager);
		if(DEBUG) System.out.println(curFunc.liveVarAnalyser.getOutput(iCode));
	}

	public void genInstr(Instr instr){
		if(DEBUG) System.out.println(instr);
		if(!(instr instanceof Label)) mips.append('\t');
		mips.append(indent).append(instr).append('\n');
	}
}
