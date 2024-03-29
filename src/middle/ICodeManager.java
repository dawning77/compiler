package middle;

import frontend.nodes.*;
import frontend.token.TokenType;
import middle.func.*;
import middle.ir.*;
import middle.ir.Label;
import middle.ir.br.Br;
import middle.ir.calc.binary.*;
import middle.ir.br.*;
import middle.ir.calc.binary.Add;
import middle.ir.calc.binary.Compare;
import middle.ir.calc.binary.Div;
import middle.ir.calc.binary.Mul;
import middle.ir.calc.binary.Sub;
import middle.ir.calc.unary.*;
import middle.ir.func.*;
import middle.ir.io.*;
import middle.ir.mem.*;
import middle.ir.reg.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

import static frontend.token.TokenType.*;
import static middle.operand.symbol.Symbol.Type.global;
import static middle.operand.symbol.Symbol.Type.local;
import static middle.operand.symbol.Symbol.Type.param;

// semantic analyse and generate intermediate code
public class ICodeManager{
	public final HashMap<String, FuncScope> funcNameMap;
	public final ArrayList<FuncScope> funcs;
	public FuncScope mainFunc;
	public final HashMap<String, Integer> strs;
	public final HashMap<Symbol, GlobalVarInfo> globalVars;
	private StringBuilder iCodes;

	private int blockCnt;
	private int strCnt;
	private int tmpCnt;
	private int tableCnt;
	private boolean isGlobal;
	private BasicBlock curBlock;
	private FuncScope curFunc;
	private SymbolTable curST;

	private final Stack<BasicBlock> loopConds;
	private final Stack<BasicBlock> loopFollows;

	private final Calculator calc;

	public static final boolean CONST_PROP = true;
	private HashMap<Var, Integer> valMap;

	public ICodeManager(){
		iCodes = null;
		funcNameMap = new HashMap<>();
		funcs = new ArrayList<>();
		mainFunc = null;
		tableCnt = 0;
		strs = new HashMap<>();
		globalVars = new HashMap<>();
		isGlobal = true;
		tmpCnt = 0;
		blockCnt = 0;
		strCnt = 0;
		curBlock = null;
		curFunc = null;
		curST = new SymbolTable(tableCnt++, null);
		loopConds = new Stack<>();
		loopFollows = new Stack<>();
		calc = new Calculator();
		valMap = new HashMap<>();
	}

	private void check(boolean assertion){
		if(!assertion) System.out.println("FALSE!!!!!!!!!!!!!!!!!");
	}

	private Var genNewTmp(){
		String name = "_t" + tmpCnt++;
		Var tmp = new Var(name, Symbol.Type.tmp);
		curST.put(name, tmp);
		return tmp;
	}

	private BasicBlock genNewBlock(){
		BasicBlock bb = new BasicBlock(blockCnt++);
		bb.add(new Label("b" + bb.id));
		return bb;
	}

	private void genLink(BasicBlock prev, BasicBlock next){
		prev.next.add(next);
		next.prev.add(prev);
	}

	private void changeCurBlock(BasicBlock follow){
		curFunc.bbs.add(follow);
		// process next and prev
		if(curBlock.iCodes.size() == 0) genLink(curBlock, follow);
		else{
			ICode last = curBlock.iCodes.get(curBlock.iCodes.size() - 1);
			if(last instanceof Jmp) genLink(curBlock, ((Jmp)last).bb);
			else if(last instanceof Br){
				genLink(curBlock, follow);
				genLink(curBlock, ((Br)last).bb);
			}
			else genLink(curBlock, follow);
		}
		curBlock = follow;
		valMap = new HashMap<>();
	}

	private void addICode(ICode icode){ curBlock.add(icode); }

	public String getICodes(){
		if(iCodes == null){
			iCodes = new StringBuilder();
			iCodes.append(mainFunc);
			funcs.forEach(f->iCodes.append(f));
		}
		return iCodes.toString();
	}

	private int addStr(String str){
		if(strs.containsKey(str)) return strs.get(str);
		strs.put(str, strCnt);
		return strCnt++;
	}

	// CompUnit → {Decl} {FuncDef} MainFuncDef
	public void analyseCompUnit(CompUnit compUnit){
		compUnit.decls.forEach(this::analyseDecl);
		compUnit.funcDefs.forEach(this::analyseFuncDef);
		analyseMainFuncDef(compUnit.mainFuncDef);
	}

	// Decl → ConstDecl | VarDecl
	private void analyseDecl(Decl decl){
		if(decl instanceof VarDecl){ analyseVarDecl((VarDecl)decl); }
		else if(decl instanceof ConstDecl){
			analyseConstDecl((ConstDecl)decl);
		}
	}

	// ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
	private void analyseConstDecl(ConstDecl constDecl){
		constDecl.constDefs.forEach(this::analyseConstDef);
	}

	// ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
	private void analyseConstDef(ConstDef constDef){
		String name = constDef.ident.val;
		String newName = name + "_c" + curST.id;
		int dim = constDef.constExps.size();
		Symbol sym = null;
		switch(dim){
			case 0:
				sym = new ConstVar(newName, isGlobal? global: local, calc.calc(constDef.constInitVal.constExp));
				if(isGlobal){
					GlobalVarInfo info;
					ArrayList<Integer> vals = new ArrayList<>();
					int initVal = calc.calc(constDef.constInitVal.constExp);
					vals.add(initVal);
					info = new GlobalVarInfo(1, vals);
					globalVars.put(sym, info);
				}
				break;
			case 1:
				int len = calc.calc(constDef.constExps.get(0));
				ArrayList<Integer> vals = new ArrayList<>();
				for(int i = 0; i < len; i++){
					vals.add(calc.calc(constDef.constInitVal.constInitVals.get(i).constExp));
				}
				sym = new ConstArr(newName, isGlobal? global: local, len, vals);
				if(isGlobal){
					GlobalVarInfo info;
					ArrayList<Integer> valss = new ArrayList<>();
					for(int i = 0; i < len; i++){
						int val = calc.calc(constDef.constInitVal.constInitVals.get(i).constExp);
						valss.add(val);
					}
					info = new GlobalVarInfo(len, valss);
					globalVars.put(sym, info);
				}
				else{
					for(int i = 0; i < constDef.constInitVal.constInitVals.size(); i++){
						int val = calc.calc(constDef.constInitVal.constInitVals.get(i).constExp);
						addICode(new Store(sym, new Imm(i), new Imm(val)));
					}
				}
				break;
			case 2:
				int innerLen = calc.calc(constDef.constExps.get(1));
				int outerLen = calc.calc(constDef.constExps.get(0));
				ArrayList<ArrayList<Integer>> valss = new ArrayList<>();
				for(int i = 0; i < outerLen; i++){
					ArrayList<Integer> innerVals = new ArrayList<>();
					valss.add(innerVals);
					ConstInitVal innerInitVal = i < constDef.constInitVal.constInitVals.size()?
					                            constDef.constInitVal.constInitVals.get(i): null;
					for(int j = 0; j < innerLen; j++){
						if(innerInitVal != null && j < innerInitVal.constInitVals.size()){
							valss.get(i).add(calc.calc(innerInitVal.constInitVals.get(j).constExp));
						}
						else valss.get(i).add(0);   // default value is 0
					}
				}
				sym = new ConstMat(newName, isGlobal? global: local, innerLen, outerLen, valss);
				if(isGlobal){
					GlobalVarInfo info;
					ArrayList<Integer> valsss = new ArrayList<>();
					for(int i = 0; i < outerLen; i++){
						ConstInitVal innerInitVal = constDef.constInitVal.constInitVals.get(i);
						for(int j = 0; j < innerLen; j++){
							int val = calc.calc(innerInitVal.constInitVals.get(j).constExp);
							valsss.add(val);
						}
					}
					info = new GlobalVarInfo(sym.size, valsss);
					globalVars.put(sym, info);
				}
				else{
					for(int i = 0; i < constDef.constInitVal.constInitVals.size(); i++){
						ConstInitVal innerInitVal = constDef.constInitVal.constInitVals.get(i);
						for(int j = 0; j < innerInitVal.constInitVals.size(); j++){
							int val = calc.calc(innerInitVal.constInitVals.get(j).constExp);
							addICode(new Store(sym, new Imm(i * innerLen + j), new Imm(val)));
						}
					}
				}
				break;
			default:
				break;
		}
		curST.put(name, sym);
	}

	// VarDecl → BType VarDef { ',' VarDef } ';'
	private void analyseVarDecl(VarDecl varDecl){ varDecl.varDefs.forEach(this::analyseVarDef); }

	// VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
	private void analyseVarDef(VarDef varDef){
		String name = varDef.ident.val;
		String newName = name + "_v" + curST.id;
		int dim = varDef.constExps.size();
		Symbol sym = null;
		switch(dim){
			case 0:
				sym = new Var(newName, isGlobal? global: local);
				if(isGlobal){
					GlobalVarInfo info;
					if(varDef.initVal != null){
						ArrayList<Integer> vals = new ArrayList<>();
						int initVal = calc.calc(varDef.initVal.exp);
						vals.add(initVal);
						info = new GlobalVarInfo(1, vals);
					}
					else info = new GlobalVarInfo(1);
					globalVars.put(sym, info);
				}
				else{
					if(varDef.initVal != null){
						Operand initVal = analyseExp(varDef.initVal.exp);
						addICode(new Assign(sym, initVal));
						if(initVal instanceof Imm) valMap.put((Var)sym, ((Imm)initVal).val);
					}
				}
				break;
			case 1:
				int len = calc.calc(varDef.constExps.get(0));
				sym = new Arr(newName, isGlobal? global: local, len);
				if(isGlobal){
					GlobalVarInfo info;
					if(varDef.initVal != null){
						ArrayList<Integer> vals = new ArrayList<>();
						for(int i = 0; i < len; i++){
							int val = 0;
							if(i < varDef.initVal.initVals.size())
								val = calc.calc(varDef.initVal.initVals.get(i).exp);
							vals.add(val);
						}
						info = new GlobalVarInfo(len, vals);
					}
					else info = new GlobalVarInfo(len);
					globalVars.put(sym, info);
				}
				else{
					if(varDef.initVal != null){
						for(int i = 0; i < varDef.initVal.initVals.size(); i++){
							Operand val = analyseExp(varDef.initVal.initVals.get(i).exp);
							addICode(new Store(sym, new Imm(i), val));
						}
					}
				}
				break;
			case 2:
				int innerLen = calc.calc(varDef.constExps.get(1));
				int outerLen = calc.calc(varDef.constExps.get(0));
				sym = new Mat(newName, isGlobal? global: local, innerLen, outerLen);
				if(isGlobal){
					GlobalVarInfo info;
					if(varDef.initVal != null){
						ArrayList<Integer> vals = new ArrayList<>();
						for(int i = 0; i < outerLen; i++){
							InitVal innerInitVal = null;
							if(i < varDef.initVal.initVals.size())
								innerInitVal = varDef.initVal.initVals.get(i);
							for(int j = 0; j < innerLen; j++){
								int val = 0;
								if(innerInitVal != null && j < innerInitVal.initVals.size())
									val = calc.calc(innerInitVal.initVals.get(j).exp);
								vals.add(val);
							}
						}
						info = new GlobalVarInfo(sym.size, vals);
					}
					else info = new GlobalVarInfo(sym.size);
					globalVars.put(sym, info);
				}
				else{
					if(varDef.initVal != null){
						for(int i = 0; i < varDef.initVal.initVals.size(); i++){
							InitVal innerInitVal = varDef.initVal.initVals.get(i);
							for(int j = 0; j < innerInitVal.initVals.size(); j++){
								Operand val = analyseExp(innerInitVal.initVals.get(j).exp);
								addICode(new Store(sym, new Imm(i * innerLen + j), val));
							}
						}
					}
				}
				break;
			default:
				break;
		}
		curST.put(name, sym);
	}

	// FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
	private void analyseFuncDef(FuncDef funcDef){
		curBlock = genNewBlock();
		valMap = new HashMap<>();
		genNewFunc(funcDef.ident.val,
		           funcDef.funcType.equals(TokenType.VOIDTK)? "void": "int",
		           analyseFuncFParams(funcDef.funcFParams),
		           funcDef.block);
		funcs.add(curFunc);
	}

	// MainFuncDef → 'int' 'main' '(' ')' Block
	private void analyseMainFuncDef(MainFuncDef mainFuncDef){
		curBlock = genNewBlock();
		valMap = new HashMap<>();
		genNewFunc("main",
		           "int",
		           new LinkedHashMap<>(),
		           mainFuncDef.block);
		mainFunc = curFunc;
	}

	private void genNewFunc(String funcName, String retType, LinkedHashMap<String, Symbol> funcFParams,
			Block funcBody){
		curFunc = new FuncScope(funcName, retType);
		curFunc.bbs.add(curBlock);
		funcNameMap.put(funcName, curFunc);
		isGlobal = false;
		analyseFuncBody(funcBody, funcFParams);
		if(!curFunc.hasLastReturn) addICode(new Ret(null));
		curFunc.formFrame();
	}

	// FuncFParams → FuncFParam { ',' FuncFParam }
	private LinkedHashMap<String, Symbol> analyseFuncFParams(FuncFParams funcFParams){
		LinkedHashMap<String, Symbol> paramNameMap = new LinkedHashMap<>();
		if(funcFParams != null){
			funcFParams.funcFParams.forEach(funcFParam->{
				Symbol sym;
				String name = funcFParam.ident.val;
				String newName = name + "_p" + curST.id;
				switch(funcFParam.dim){
					case 0:
						sym = new Var(newName, param);
						break;
					case 1:
						sym = new Arr(newName, param, -1); break;
					case 2:
						int innerLen = calc.calc(funcFParam.constExp);
						sym = new Mat(newName, param, innerLen, -1); break;
					default:
						sym = null; break;
				}
				paramNameMap.put(name, sym);
			});
		}
		return paramNameMap;
	}

	private void analyseFuncBody(Block block, LinkedHashMap<String, Symbol> params){
		curST = new SymbolTable(tableCnt++, curST, curFunc);
		params.forEach((name, param)->curST.put(name, param));
		block.blockItems.forEach(this::analyseBlockItem);
		BlockItem last = block.blockItems.size() == 0? null: block.blockItems.get(block.blockItems.size() - 1);
		if(last instanceof Stmt && ((Stmt)last).type.equals("return"))
			curFunc.hasLastReturn = true;
		curST = curST.parent;
	}

	// Block → '{' { BlockItem } '}'
	private void analyseBlock(Block block){
		curST = new SymbolTable(tableCnt++, curST);
		block.blockItems.forEach(this::analyseBlockItem);
		curST = curST.parent;
	}

	// BlockItem → Decl | Stmt
	private void analyseBlockItem(BlockItem blockItem){
		if(blockItem instanceof Decl){ analyseDecl((Decl)blockItem); }
		else if(blockItem instanceof Stmt){
			analyseStmt((Stmt)blockItem);
		}
	}

	/*
	Stmt → LVal '=' Exp ';' | [Exp] ';'
	| Block | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
	| 'while' '(' Cond ')' Stmt | 'break' ';' | 'continue' ';'
	| 'return' [Exp] ';' | LVal '=' 'getint''('')'';'
	| 'printf''('FormatString{','Exp}')'';'
	*/
	private void analyseStmt(Stmt stmt){
		switch(stmt.type){
			case "assign":
				analyseAssign(stmt); break;
			case "exp":
				analyseExp(stmt.exp); break;
			case "block":
				analyseBlock(stmt.block); break;
			case "if":
			case "ifelse":
				analyseIf(stmt); break;
			case "while":
				analyseWhile(stmt); break;
			case "break":
				analyseBreak(); break;
			case "continue":
				analyseContinue(); break;
			case "return":
				analyseReturn(stmt); break;
			case "getint":
				analyseGetint(stmt); break;
			case "printf":
				analysePrintf(stmt); break;
			case "tag":
				addICode(new Label("################################" + stmt.tag)); break;
			default:
				break;
		}
	}

	private void analyseReturn(Stmt s){
		Operand res = null;
		if(s.exp != null) res = analyseExp(s.exp);
		addICode(new Ret(res));
	}

	private void analysePrintf(Stmt s){
		int formCharCnt = 0;
		ArrayList<ICode> outputList = new ArrayList<>();
		for(String str: s.formatString.strs){
			if(str.equals("%d")){
				Operand val = analyseExp(s.exps.get(formCharCnt++));
				outputList.add(new OutputInt(val));
			}
			else{
				int strId = addStr(str);
				outputList.add(new OutputStr(strId));
			}
		}
		outputList.forEach(this::addICode);
	}

	private void analyseAssign(Stmt s){
		Operand val = analyseExp(s.exp);
		analyseLVal(s.lVal, val, "store");
	}

	private void analyseGetint(Stmt s){
		Var val = genNewTmp();
		addICode(new Input(val));
		analyseLVal(s.lVal, val, "store");
	}

	// LVal → Ident {'[' Exp ']'}
	private Operand analyseLVal(LVal lVal, Operand val, String mode){
		String name = lVal.ident.val;
		Symbol sym = curST.get(name);
		boolean isConst = sym instanceof Const;
		Operand res = null;
		int dim = sym instanceof Var? 0:
		          sym instanceof Arr? 1:
		          sym instanceof Mat? 2: -1;
		switch(dim){
			case 0:
				if(mode.equals("store")){
					addICode(new Assign(sym, val));
					if(val instanceof Imm) valMap.put((Var)sym, ((Imm)val).val);
					else valMap.remove((Var)sym);
				}
				else if(mode.equals("load")){
					if(isConst) res = new Imm(((ConstVar)sym).val);
					else if(CONST_PROP && valMap.containsKey((Var)sym)) res = new Imm(valMap.get((Var)sym));
					else res = sym;
				}
				break;
			case 1:
				switch(lVal.exps.size()){
					case 0:
						res = sym;  // get addr
						break;
					case 1:
						// get value
						Operand idx = analyseExp(lVal.exps.get(0));
						if(mode.equals("store")) addICode(new Store(sym, idx, val));
						else if(mode.equals("load")){
							if(isConst && idx instanceof Imm)
								res = new Imm(((ConstArr)sym).vals.get(((Imm)idx).val));
							else{
								res = genNewTmp();
								addICode(new Load(sym, idx, (Symbol)res));
							}
						}
						break;
					default:
						break;
				}
				break;
			case 2:
				switch(lVal.exps.size()){
					case 0:
						res = sym;  // get addr
						break;
					case 1:
						// only used in passing param, do not add to symbol table
						Operand idx = analyseExp(lVal.exps.get(0));
						res = new SubMat((Mat)sym, idx);
						break;
					case 2:
						int innerLen = ((Mat)sym).innerLen;
						Operand outerIdx = analyseExp(lVal.exps.get(0));
						Operand innerIdx = analyseExp(lVal.exps.get(1));
						Operand idxx;
						if(outerIdx instanceof Imm && innerIdx instanceof Imm)
							idxx = new Imm(((Imm)outerIdx).val * innerLen + ((Imm)innerIdx).val);
						else if(outerIdx instanceof Imm){
							Var tmp = genNewTmp();
							addICode(new Add(new Imm(((Imm)outerIdx).val * innerLen), innerIdx, tmp));
							idxx = tmp;
						}
						else{
							Var tmp1 = genNewTmp();
							Var tmp2 = genNewTmp();
							addICode(new Mul(outerIdx, new Imm(innerLen), tmp1));
							addICode(new Add(tmp1, innerIdx, tmp2));
							idxx = tmp2;
						}
						if(mode.equals("store")) addICode(new Store(sym, idxx, val));
						else if(mode.equals("load")){
							if(isConst && outerIdx instanceof Imm && innerIdx instanceof Imm)
								res = new Imm(((ConstMat)sym).vals.get(((Imm)outerIdx).val).get(((Imm)innerIdx).val));
							else{
								res = genNewTmp();
								addICode(new Load(sym, idxx, (Symbol)res));
							}
						}
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
		return res;
	}

	// PrimaryExp → '(' Exp ')' | LVal | Number
	private Operand analysePrimaryExp(PrimaryExp primaryExp){
		if(primaryExp.exp != null) return analyseExp(primaryExp.exp);
		else if(primaryExp.lVal != null) return analyseLVal(primaryExp.lVal, null, "load");
		else if(primaryExp.number != null) return new Imm(Integer.parseInt(primaryExp.number.val));
		return null;
	}

	// UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
	// UnaryOp → '+' | '−' | '!'
	// return val is var or imm
	private Operand analyseUnaryExp(UnaryExp unaryExp){
		if(unaryExp.primaryExp != null){ return analysePrimaryExp(unaryExp.primaryExp); }
		else if(unaryExp.unaryOp != null){
			UnaryExp curExp = unaryExp.unaryExp;
			Operand core;
			if(unaryExp.unaryOp.equals(TokenType.NOT)){
				int notCnt = 1;
				while(curExp.unaryOp != null){
					if(curExp.unaryOp.equals(TokenType.NOT)) notCnt++;
					curExp = curExp.unaryExp;
				}
				core = analyseUnaryExp(curExp);
				if(core instanceof Imm){
					if((notCnt % 2 == 1) == (((Imm)core).val == 0)) return new Imm(1);
					else return new Imm(0);
				}
				else{
					if(notCnt % 2 == 1){
						Symbol res = genNewTmp();
						addICode(new Not(core, res));
						return res;
					}
					else return core;
				}
			}
			else{
				// analyse until the first ! observed
				int negCnt = unaryExp.unaryOp.equals(TokenType.MINU)? 1: 0;
				while(curExp.unaryOp != null && !curExp.unaryOp.equals(TokenType.NOT)){
					if(curExp.unaryOp.equals(TokenType.MINU)){ negCnt++; }
					curExp = curExp.unaryExp;
				}
				core = analyseUnaryExp(curExp);
				if(core instanceof Imm){
					if(negCnt % 2 == 1) return new Imm(-((Imm)core).val);
					else return core;
				}
				else{
					if(negCnt % 2 == 1){
						Symbol res = genNewTmp();
						addICode(new Sub(new Imm(0), core, res));
						return res;
					}
					else return core;
				}
			}
		}
		else if(unaryExp.ident != null){
			// do not change block in function call
			//			BasicBlock follow = genNewBlock();
			String name = unaryExp.ident.val;
			analyseFuncRParams(unaryExp.funcRParams);
			addICode(new Call(unaryExp.ident.val));
			//			changeCurBlock(follow);
			if(funcNameMap.containsKey(name) && funcNameMap.get(name).retType.equals("int")){
				Symbol res = genNewTmp();
				addICode(new GetRet(res));
				return res;
			}
			else return null;
		}
		return null;
	}

	// FuncRParams → Exp { ',' Exp }
	private void analyseFuncRParams(FuncRParams funcRParams){
		ArrayList<Operand> params = new ArrayList<>();
		if(funcRParams != null){
			for(int i = 0; i < funcRParams.exps.size(); i++){
				Operand param = analyseExp(funcRParams.exps.get(i));
				params.add(param);
			}
		}
		addICode(new Push(params));
	}

	// MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
	private Operand analyseMulExp(MulExp mulExp){
		if(mulExp.isAllMul()){
			// can reorder, gather numbers
			int val = 1;
			Operand res = null;
			for(int i = 0; i < mulExp.unaryExps.size(); i++){
				UnaryExp unaryExp = mulExp.unaryExps.get(i);
				Operand tmp = analyseUnaryExp(unaryExp);
				if(tmp instanceof Imm) val *= ((Imm)tmp).val;
				else{
					if(res == null) res = tmp;
					else{
						Var newRes = genNewTmp();
						addICode(new Mul(res, tmp, newRes));
						res = newRes;
					}
				}
			}
			if(res != null){
				if(val != 1){
					Var newRes = genNewTmp();
					addICode(new Mul(res, new Imm(val), newRes));
					res = newRes;
				}
				return res;
			}
			else return new Imm(val);
		}
		else{
			Operand res = analyseUnaryExp(mulExp.unaryExps.get(0));
			for(int i = 1; i < mulExp.unaryExps.size(); i++){
				Operand tmp = analyseUnaryExp(mulExp.unaryExps.get(i));
				if(tmp instanceof Imm && res instanceof Imm){
					switch(mulExp.ops.get(i - 1)){
						case MULT:
							res = new Imm(((Imm)res).val * ((Imm)tmp).val); break;
						case DIV:
							res = new Imm(((Imm)res).val / ((Imm)tmp).val); break;
						case MOD:
							res = new Imm(((Imm)res).val % ((Imm)tmp).val); break;
						default:
							res = null;
					}
				}
				else{
					assert res != null;
					Var newRes = genNewTmp();
					ICode iCode;
					switch(mulExp.ops.get(i - 1)){
						case MULT:
							iCode = new Mul(res, tmp, newRes);
							break;
						case DIV:
							iCode = new Div(res, tmp, newRes);
							break;
						case MOD:
							iCode = new Mod(res, tmp, newRes);
							break;
						default:
							iCode = null;
					}
					addICode(iCode);
					res = newRes;
				}
			}
			return res;
		}
	}

	// AddExp → MulExp | AddExp ('+' | '−') MulExp
	private Operand analyseAddExp(AddExp addExp){
		// can reorder, gather numbers
		int val = 0;
		Operand res = null;
		for(int i = 0; i < addExp.mulExps.size(); i++){
			MulExp mulExp = addExp.mulExps.get(i);
			Operand tmp = analyseMulExp(mulExp);
			if(tmp instanceof Imm){
				if(i == 0 || addExp.ops.get(i - 1).equals(PLUS)) val += ((Imm)tmp).val;
				else val -= ((Imm)tmp).val;
			}
			else{
				if(i == 0 || addExp.ops.get(i - 1).equals(PLUS)){
					if(res == null) res = tmp;
					else{
						Symbol newRes = genNewTmp();
						addICode(new Add(res, tmp, newRes));
						res = newRes;
					}
				}
				else{
					if(res == null){
						res = genNewTmp();
						addICode(new Sub(new Imm(val), tmp, (Symbol)res));
						val = 0;
					}
					else{
						Symbol newRes = genNewTmp();
						addICode(new Sub(res, tmp, newRes));
						res = newRes;
					}
				}
			}
		}
		if(res != null){
			if(val != 0){
				Symbol newRes = genNewTmp();
				addICode(new Add(res, new Imm(val), newRes));
				res = newRes;
			}
			return res;
		}
		else return new Imm(val);
	}

	private void analyseIf(Stmt s){
		BasicBlock ifBody = genNewBlock();
		BasicBlock elseBody = s.type.equals("ifelse")? genNewBlock(): null;
		BasicBlock ifFollow = genNewBlock();
		if(s.type.equals("if")){
			analyseCond(s.cond, false, ifBody, ifFollow);
			changeCurBlock(ifBody);
			analyseStmt(s.stmt1);
			addICode(new SetAllSpare());
			changeCurBlock(ifFollow);
		}
		else{
			analyseCond(s.cond, false, ifBody, elseBody);
			changeCurBlock(ifBody);
			analyseStmt(s.stmt1);
			addICode(new Jmp(ifFollow));
			changeCurBlock(elseBody);
			analyseStmt(s.stmt2);
			addICode(new SetAllSpare());
			changeCurBlock(ifFollow);
		}
	}

	private void analyseWhile(Stmt s){
		BasicBlock loopBefore = curBlock;
		BasicBlock loopBody = genNewBlock();
		BasicBlock loopCond = genNewBlock();
		BasicBlock loopFollow = genNewBlock();
		curFunc.loopInfos.add(new LoopInfo(loopBefore, curBlock.iCodes.get(
				curBlock.iCodes.size() - 1), loopBody, loopCond, loopFollow));
		analyseCond(s.cond, false, loopBody, loopFollow);
		changeCurBlock(loopBody);
		loopConds.push(loopCond);
		loopFollows.push(loopFollow);
		analyseStmt(s.stmt1);
		addICode(new SetAllSpare());
		loopConds.pop();
		loopFollows.pop();
		changeCurBlock(loopCond);
		analyseCond(s.cond, true, loopBody, loopFollow);
		changeCurBlock(loopFollow);
	}

	private void analyseBreak(){
		addICode(new Jmp(loopFollows.peek()));
		changeCurBlock(genNewBlock());
	}

	private void analyseContinue(){
		addICode(new Jmp(loopConds.peek()));
		changeCurBlock(genNewBlock());
	}

	// toIf = true : if(cond)  goto ifB;
	// toIf = false: if(!cond) goto elseB;
	private void analyseCond(Cond cond, boolean toIf, BasicBlock ifB, BasicBlock elseB){
		analyseLOrExp((LOrExp)cond, toIf, ifB, elseB);
	}

	// LOrExp → LAndExp | LOrExp '||' LAndExp
	private void analyseLOrExp(LOrExp lOrExp, boolean toIf, BasicBlock ifB, BasicBlock elseB){
		for(int i = 0; i < lOrExp.lAndExps.size(); i++){
			BasicBlock nextB = i != lOrExp.lAndExps.size() - 1? genNewBlock(): elseB;
			boolean innerToIf = toIf || i != lOrExp.lAndExps.size() - 1;
			analyseLAndExp(lOrExp.lAndExps.get(i), innerToIf, ifB, nextB);
			if(i != lOrExp.lAndExps.size() - 1) changeCurBlock(nextB);
		}
	}

	// LAndExp → EqExp | LAndExp '&&' EqExp
	private void analyseLAndExp(LAndExp lAndExp, boolean toIf, BasicBlock ifB, BasicBlock elseB){
		for(int i = 0; i < lAndExp.eqExps.size(); i++){
			Operand cond = analyseEqExp(lAndExp.eqExps.get(i));
			Br br;
			if(toIf && i == lAndExp.eqExps.size() - 1) br = new Br(cond, false, ifB);
			else br = new Br(cond, true, elseB);
			addICode(br);
			if(i != lAndExp.eqExps.size() - 1) changeCurBlock(genNewBlock());
		}
	}

	public static final HashMap<TokenType, Rel> relMap = new HashMap<TokenType, Rel>(){{
		put(LSS, Rel.lt);
		put(LEQ, Rel.le);
		put(GRE, Rel.gt);
		put(GEQ, Rel.ge);
		put(EQL, Rel.eq);
		put(NEQ, Rel.ne);
	}};

	// EqExp → RelExp | EqExp ('==' | '!=') RelExp
	private Operand analyseEqExp(EqExp eqExp){
		Operand opd0 = analyseRelExp(eqExp.relExps.get(0));
		Operand opd1;
		Operand res = opd0;
		for(int i = 1; i < eqExp.relExps.size(); i++){
			opd1 = analyseRelExp(eqExp.relExps.get(i));
			res = genNewTmp();
			Rel rel = relMap.get(eqExp.ops.get(i - 1));
			addICode(new Compare(opd0, opd1, (Symbol)res, rel));
			opd0 = res;
		}
		return res;
	}

	// RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
	private Operand analyseRelExp(RelExp relExp){
		Operand opd0 = analyseAddExp(relExp.addExps.get(0));
		Operand opd1;
		Operand res = opd0;
		for(int i = 1; i < relExp.addExps.size(); i++){
			opd1 = analyseAddExp(relExp.addExps.get(i));
			res = genNewTmp();
			Rel rel = relMap.get(relExp.ops.get(i - 1));
			addICode(new Compare(opd0, opd1, (Symbol)res, rel));
			opd0 = res;
		}
		return res;
	}

	// Exp → AddExp
	private Operand analyseExp(Exp exp){ return analyseAddExp((AddExp)exp); }

	private class Calculator{
		public int calc(ConstExp constExp){ return calc((AddExp)constExp); }

		public int calc(Exp exp){ return calc((AddExp)exp); }

		public int calc(AddExp addExp){
			int cur = 0;
			int val = calc(addExp.mulExps.get(cur++));
			while(cur < addExp.mulExps.size()){
				int sign = addExp.ops.get(cur - 1).equals(TokenType.PLUS)? 1: -1;
				val += sign * calc(addExp.mulExps.get(cur++));
			}
			return val;
		}

		public int calc(MulExp mulExp){
			int cur = 0;
			int val = calc(mulExp.unaryExps.get(cur++));
			while(cur < mulExp.unaryExps.size()){
				switch(mulExp.ops.get(cur - 1)){
					case MULT:
						val *= calc(mulExp.unaryExps.get(cur++)); break;
					case DIV:
						val /= calc(mulExp.unaryExps.get(cur++)); break;
					case MOD:
						val %= calc(mulExp.unaryExps.get(cur++)); break;
					default:
						break;
				}
			}
			return val;
		}

		public int calc(UnaryExp unaryExp){
			check(unaryExp.unaryOp != null || unaryExp.primaryExp != null);
			if(unaryExp.primaryExp != null) return calc(unaryExp.primaryExp);
			else{
				// do not contain NOT
				int sign = unaryExp.unaryOp.equals(TokenType.PLUS)? 1: -1;
				return sign * calc(unaryExp.unaryExp);
			}
		}

		public int calc(PrimaryExp primaryExp){
			if(primaryExp.exp != null) return calc(primaryExp.exp);
			else if(primaryExp.number != null) return Integer.parseInt(primaryExp.number.val);
			else return calc(primaryExp.lVal);
		}

		public int calc(LVal lVal){
			String name = lVal.ident.val;
			check(curST.contains(name) && curST.get(name) instanceof Const);
			Symbol sym = curST.get(name);
			int dim = sym instanceof ConstVar? 0:
			          sym instanceof ConstArr? 1:
			          sym instanceof ConstMat? 2: -1;
			int val = 0;
			switch(dim){
				case 0:
					val = ((ConstVar)sym).val;
					break;
				case 1:
					int idx = calc(lVal.exps.get(0));
					val = ((ConstArr)sym).vals.get(idx);
					break;
				case 2:
					int outerIdx = calc(lVal.exps.get(0));
					int innerIdx = calc(lVal.exps.get(1));
					val = ((ConstMat)sym).vals.get(outerIdx).get(innerIdx);
					break;
				default:
					break;
			}
			return val;
		}
	}
}
