package backend;

import backend.mips.instr.*;
import backend.mips.instr.Label;
import backend.mips.instr.itype.*;
import backend.mips.instr.itype.Lw;
import backend.mips.instr.itype.Sw;
import backend.mips.instr.jtype.*;
import backend.mips.instr.pseudo.*;
import backend.mips.reg.*;
import middle.*;
import middle.ir.*;
import middle.ir.func.*;
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

	private final StringBuilder iCodes;
	private final StringBuilder mips;
	public String indent = "";

	public MipsManager(ICodeManager iCodeManager){
		this.funcNameMap = iCodeManager.funcNameMap;
		this.funcs = iCodeManager.funcs;
		this.mainFunc = iCodeManager.mainFunc;
		this.strs = iCodeManager.strs;
		this.globalVars = iCodeManager.globalVars;
		this.regManager = new RegManager(this);
		this.iCodes = new StringBuilder();
		this.mips = new StringBuilder();
		this.curFunc = null;
		this.curBB = null;
	}

	public String getICodes(){ return iCodes.toString(); }

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
				regManager.curState.inStack.add(sym);
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
		regManager.setAllSpareWithoutWriteBack();
		genInstr(new middle.ir.Label(curFunc.name));
		if(curFunc.frameSize != 0) genInstr(new Addi(Reg.$sp, Reg.$sp, -curFunc.frameSize * 4));   // genFrame
		genInstr(new Move(Reg.$fp, Reg.$sp));
		curBB = func.firstBB;
		while(curBB != null){
			curBB.iCodes.forEach(this::genInstr);
			curBB = curBB.follow;
		}
		iCodes.append('\n');
		mips.append('\n');
	}

	private void genInstr(ICode iCode){
		iCodes.append(iCode).append('\n');
		System.out.println(iCode);
		int frameSize = iCode instanceof Call? funcNameMap.get(((Call)iCode).funcName).frameSize: -1;
		int paramSize = iCode instanceof Call? funcNameMap.get(((Call)iCode).funcName).paramSize: -1;
		if(iCode instanceof Push) prologue();
		ArrayList<Instr> instrs = iCode.toInstr(this.regManager);
		instrs.forEach(this::genInstr);
		if(iCode instanceof Call) {
			genInstr(new Addi(Reg.$sp, Reg.$sp, (frameSize + paramSize) * 4));
			epilogue();
		}
	}

	public void genInstr(Instr instr){
		if(!(instr instanceof Label)) mips.append('\t');
		mips.append(indent).append(instr).append('\n');
	}

	public HashMap<Reg, Integer> savedLoc;
	public HashMap<Reg, Symbol> savedMap;
	public BasicBlock beforeCallBB;
	public int regSize;

	private void save(Reg reg){
		savedLoc.put(reg, regSize++);
		savedMap.put(reg, regManager.curState.used.get(reg));        // ra, fp map to null
		genInstr(new Sw(Reg.$sp, reg, -regSize * 4));
		// regManager.setSpareWithoutWriteBack(reg);
	}

	private void prologue(){
		// before push parameters, save $ra, $fp and allocatable used reg
		genInstr(new Label("prologue_begin" + curBB.id));
		beforeCallBB = curBB;
		savedLoc = new HashMap<>();
		savedMap = new HashMap<>();
		regSize = 0;
		regManager.setAllGlobalSpare();
		regManager.saveRegState(curBB);
		save(Reg.$ra);
		save(Reg.$fp);
		HashSet<Reg> used = new HashSet<>(regManager.curState.used.keySet());
		used.forEach(this::save);
		genInstr(new Addi(Reg.$sp, Reg.$sp, -regSize * 4));
		genInstr(new Label("prologue_end" + curBB.id));
	}

	private void restore(Reg reg){
		genInstr(new Lw(Reg.$sp, reg, -(savedLoc.get(reg) + 1) * 4));
		// regManager.setUsed(reg, savedMap.get(reg));      // ra, fp map to null
	}

	private void epilogue(){
		// after jr, restore regs
		genInstr(new Label("epilogue_begin" + curBB.id));
		genInstr(new Addi(Reg.$sp, Reg.$sp, regSize * 4));
		savedLoc.keySet().forEach(this::restore);
		regManager.loadRegState(beforeCallBB);
		genInstr(new Label("epilogue_end" + curBB.id));
	}
}
