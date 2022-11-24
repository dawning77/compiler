package frontend;

import frontend.nodes.*;
import frontend.symbol.FuncSym;
import frontend.symbol.SymbolTable;
import frontend.symbol.VarSym;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.ArrayList;

public class Parser{
	private final ArrayList<Token> tokens;
	private final StringBuilder out;
	private final SymbolTable rootST;
	private final Logger logger;
	private SymbolTable curST;
	private int curLine;
	private int curPos;
	private int loopDepth;
	private boolean isVoid;
	public CompUnit compUnit;

	public static final boolean OUTPUT_NODE = true;
	public static final boolean OUTPUT_TOKEN = true;

	public Parser(ArrayList<Token> tokens, Logger logger){
		this.tokens = tokens;
		this.out = new StringBuilder();
		this.rootST = new SymbolTable(null);
		this.logger = logger;
		this.curST = this.rootST;
		this.curLine = 1;
		this.curPos = 0;
		this.loopDepth = 0;
		this.isVoid = true;
		this.compUnit = null;
	}

	public String getOutput(){ return out.toString(); }

	private void output(String s){ out.append(s).append('\n'); }

	private void check(boolean assertion){ if(!assertion){ output("FALSE!!!!!!!!!!!!!!!!!"); } }

	private void printNode(Class c){ if(OUTPUT_NODE){ output('<' + c.getSimpleName() + '>'); } }

	// only need the type of frontend.token
	private TokenType peek(int offset){
		check(curPos + offset < tokens.size());
		return tokens.get(curPos + offset).type;
	}

	private TokenType peek(){ return peek(0); }

	private Token pass(){
		if(OUTPUT_TOKEN){ output(tokens.get(curPos).type + " " + tokens.get(curPos).val); }
		curLine = tokens.get(curPos).line;
		return tokens.get(curPos++);
	}

	private Token pass(TokenType type){
		if(tokens.get(curPos).type.equals(type)){ return pass(); }
		else{ return null; }
	}

	// CompUnit → {Decl} {FuncDef} MainFuncDef
	public void parseCompUnit(){
		ArrayList<Decl> decls = new ArrayList<>();
		ArrayList<FuncDef> funcDefs = new ArrayList<>();
		MainFuncDef mainFuncDef;
		while(!peek(2).equals(TokenType.LPARENT)){
			decls.add(parseDecl());
		}
		while(!peek(1).equals(TokenType.MAINTK)){
			funcDefs.add(parseFuncDef());
		}
		mainFuncDef = parseMainDef();
		printNode(CompUnit.class);
		this.compUnit = new CompUnit(decls, funcDefs, mainFuncDef);
	}

	// Decl → ConstDecl | VarDecl
	private Decl parseDecl(){
		//  printNode(Decl.class);
		if(peek().equals(TokenType.CONSTTK)){ return parseConstDecl(); }
		else{ return parseVarDecl(); }
	}

	// ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
	private ConstDecl parseConstDecl(){
		ArrayList<ConstDef> constDefs = new ArrayList<>();
		pass(TokenType.CONSTTK); // const
		pass(TokenType.INTTK); // int
		constDefs.add(parseConstDef());
		while(peek().equals(TokenType.COMMA)){
			pass(TokenType.COMMA); // ,
			constDefs.add(parseConstDef());
		}
		if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		printNode(ConstDecl.class);
		return new ConstDecl(constDefs);
	}

	// ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
	private ConstDef parseConstDef(){
		Token ident = pass(TokenType.IDENFR);
		if(ident != null && curST.localContains(ident.val)){ logger.log('b', curLine); }
		ArrayList<ConstExp> constExps = new ArrayList<>();
		ConstInitVal constInitVal;
		int dim = 0;
		while(peek().equals(TokenType.LBRACK)){
			dim++;
			pass(TokenType.LBRACK); // [
			constExps.add(parseConstExp());
			if(pass(TokenType.RBRACK) == null){
				logger.log('k', curLine); // ]
			}
		}
		check(dim < 3);
		if(ident != null){ curST.put(new VarSym(ident.val, "const", curLine, dim)); }
		pass(TokenType.ASSIGN); // =
		constInitVal = parseConstInitVal();
		printNode(ConstDef.class);
		return new ConstDef(ident, constExps, constInitVal);
	}

	// ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
	private ConstInitVal parseConstInitVal(){
		ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
		ConstExp constExp = null;
		if(peek().equals(TokenType.LBRACE)){
			pass(TokenType.LBRACE); // {
			if(peek().equals(TokenType.RBRACE)){
				pass(TokenType.RBRACE); // }
				printNode(ConstInitVal.class);
				return new ConstInitVal(constInitVals, constExp);
			}
			constInitVals.add(parseConstInitVal());
			while(!peek().equals(TokenType.RBRACE)){
				pass(TokenType.COMMA);  // ,
				constInitVals.add(parseConstInitVal());
			}
			pass(TokenType.RBRACE); // }
		}
		else{ constExp = parseConstExp(); }
		printNode(ConstInitVal.class);
		return new ConstInitVal(constInitVals, constExp);
	}

	// VarDecl → BType VarDef { ',' VarDef } ';'
	private VarDecl parseVarDecl(){
		ArrayList<VarDef> varDefs = new ArrayList<>();
		pass(TokenType.INTTK); // int
		varDefs.add(parseVarDef());
		while(peek().equals(TokenType.COMMA)){
			pass(TokenType.COMMA); // ,
			varDefs.add(parseVarDef());
		}
		if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		printNode(VarDecl.class);
		return new VarDecl(varDefs);
	}

	// VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
	private VarDef parseVarDef(){
		Token ident = pass(TokenType.IDENFR);
		if(ident != null && curST.localContains(ident.val)){ logger.log('b', curLine); }
		ArrayList<ConstExp> constExps = new ArrayList<>();
		InitVal initVal = null;
		int dim = 0;
		while(peek().equals(TokenType.LBRACK)){
			dim++;
			pass(TokenType.LBRACK); // [
			constExps.add(parseConstExp());
			if(pass(TokenType.RBRACK) == null){
				logger.log('k', curLine); // ]
			}
		}
		check(dim < 3);
		if(ident != null){ curST.put(new VarSym(ident.val, "var", curLine, dim)); }
		if(peek().equals(TokenType.ASSIGN)){
			pass(TokenType.ASSIGN); // =
			initVal = parseInitVal();
		}
		printNode(VarDef.class);
		return new VarDef(ident, constExps, initVal);
	}

	// InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
	private InitVal parseInitVal(){
		ArrayList<InitVal> initVals = new ArrayList<>();
		Exp exp = null;
		if(peek().equals(TokenType.LBRACE)){
			pass(TokenType.LBRACE); // {
			if(peek().equals(TokenType.RBRACE)){
				pass(TokenType.RBRACE); // }
				printNode(InitVal.class);
				return new InitVal(initVals, exp);
			}
			initVals.add(parseInitVal());
			while(!peek().equals(TokenType.RBRACE)){
				pass(TokenType.COMMA);  // ,
				initVals.add(parseInitVal());
			}
			pass(TokenType.RBRACE); // }
		}
		else{ exp = parseExp(); }
		printNode(InitVal.class);
		return new InitVal(initVals, exp);
	}

	// FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
	private FuncDef parseFuncDef(){
		TokenType funcType = pass().type;
		if(OUTPUT_NODE){ output('<' + "FuncType" + '>'); }
		Token ident = pass(TokenType.IDENFR);
		if(ident != null && curST.localContains(ident.val)){ logger.log('b', curLine); }
		FuncFParams funcFParams = null;
		Block block;
		pass(TokenType.LPARENT);    // (
		if(!peek().equals(TokenType.RPARENT) && !peek().equals(TokenType.LBRACE)){
			funcFParams = parseFuncFParams();
		}
		if(pass(TokenType.RPARENT) == null){
			logger.log('j', curLine);    // )
		}
		if(ident != null){
			curST.put(new FuncSym(ident.val, "func", curLine, funcType, funcFParams));
		}
		isVoid = funcType.equals(TokenType.VOIDTK);
		block = parseBlock(funcFParams == null? null: funcFParams.varSyms);
		printNode(FuncDef.class);
		return new FuncDef(funcType, ident, funcFParams, block);
	}

	// MainFuncDef → 'int' 'main' '(' ')' Block
	private MainFuncDef parseMainDef(){
		pass(TokenType.INTTK);  // int
		pass(TokenType.MAINTK); // main
		pass(TokenType.LPARENT);    // (
		if(pass(TokenType.RPARENT) == null){
			logger.log('j', curLine);    // )
		}
		isVoid = false;
		Block block = parseBlock(null);
		printNode(MainFuncDef.class);
		return new MainFuncDef(block);
	}

	// FuncFParams → FuncFParam { ',' FuncFParam }
	private FuncFParams parseFuncFParams(){
		ArrayList<FuncFParam> funcFParams = new ArrayList<>();
		funcFParams.add(parseFuncFParam());
		while(peek().equals(TokenType.COMMA)){
			pass(TokenType.COMMA); // ,
			funcFParams.add(parseFuncFParam());
		}
		printNode(FuncFParams.class);
		return new FuncFParams(funcFParams);
	}

	// FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
	private FuncFParam parseFuncFParam(){
		int dim = 0;
		pass(TokenType.INTTK);  // int
		Token ident = pass(TokenType.IDENFR);
		ConstExp constExp = null;
		VarSym varSym = null;
		if(peek().equals(TokenType.LBRACK)){
			dim++;
			pass(TokenType.LBRACK); // [
			if(pass(TokenType.RBRACK) == null){
				logger.log('k', curLine); // ]
			}
			if(peek().equals(TokenType.LBRACK)){
				dim++;
				pass(TokenType.LBRACK); // [
				constExp = parseConstExp();
				if(pass(TokenType.RBRACK) == null){
					logger.log('k', curLine); // ]
				}
			}
		}
		if(ident != null){ varSym = new VarSym(ident.val, "var", curLine, dim); }
		printNode(FuncFParam.class);
		return new FuncFParam(dim, ident, constExp, varSym);
	}

	// Block → '{' { BlockItem } '}'
	private Block parseBlock(ArrayList<VarSym> varSyms){
		ArrayList<BlockItem> blockItems = new ArrayList<>();
		BlockItem lastItem = null;
		pass(TokenType.LBRACE); // {
		curST = new SymbolTable(curST);
		if(varSyms != null){
			for(VarSym varSym: varSyms){
				if(curST.localContains(varSym.name)){ logger.log('b', varSym.line); }
				curST.put(varSym);
			}
		}
		while(!peek().equals(TokenType.RBRACE)){
			lastItem = parseBlockItem();
			blockItems.add(lastItem);
		}
		pass(TokenType.RBRACE); // }
		curST = curST.parent;
		if(!isVoid && curST.parent == null){
			if(!(lastItem instanceof Stmt) ||
			   !((Stmt)lastItem).type.equals("return") || ((Stmt)lastItem).exp == null){
				logger.log('g', curLine);
			}
		}
		printNode(Block.class);
		return new Block(blockItems);
	}

	// BlockItem → Decl | Stmt
	private BlockItem parseBlockItem(){
		//  printNode(BlockItem.class);
		if(peek().equals(TokenType.CONSTTK) || peek().equals(TokenType.INTTK)){
			return parseDecl();
		}
		else{ return parseStmt(); }
	}

	/*
	Stmt → LVal '=' Exp ';' | [Exp] ';'
	| Block | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
	| 'while' '(' Cond ')' Stmt | 'break' ';' | 'continue' ';'
	| 'return' [Exp] ';' | LVal '=' 'getint''('')'';'
	| 'printf''('FormatString{','Exp}')'';'
	*/
	private Stmt parseStmt(){
		String type = null;
		LVal lVal = null;
		Exp exp = null;
		ArrayList<Exp> exps = new ArrayList<>();
		Block block = null;
		Cond cond = null;
		Stmt stmt1 = null, stmt2 = null;
		FormatString formatString = null;
		String tag = null;
		if(peek().equals(TokenType.LBRACE)){
			type = "block";
			block = parseBlock(null);
		}
		else if(peek().equals(TokenType.IFTK)){
			pass(TokenType.IFTK); // if
			pass(TokenType.LPARENT);    // (
			cond = parseCond();
			if(pass(TokenType.RPARENT) == null){
				logger.log('j', curLine);    // )
			}
			stmt1 = parseStmt();
			if(peek().equals(TokenType.ELSETK)){
				type = "ifelse";
				pass(TokenType.ELSETK); // else
				stmt2 = parseStmt();
			}
			else{ type = "if"; }
		}
		else if(peek().equals(TokenType.WHILETK)){
			type = "while";
			pass(TokenType.WHILETK); // while
			pass(TokenType.LPARENT);    // (
			cond = parseCond();
			if(pass(TokenType.RPARENT) == null){
				logger.log('j', curLine);    // )
			}
			loopDepth++;
			stmt1 = parseStmt();
			loopDepth--;
		}
		else if(peek().equals(TokenType.BREAKTK)){
			type = "break";
			pass(TokenType.BREAKTK); // break
			if(loopDepth == 0){ logger.log('m', curLine); }
			if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		}
		else if(peek().equals(TokenType.CONTINUETK)){
			type = "continue";
			pass(TokenType.CONTINUETK); // continue
			if(loopDepth == 0){ logger.log('m', curLine); }
			if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		}
		else if(peek().equals(TokenType.RETURNTK)){
			type = "return";
			pass(TokenType.RETURNTK); // return
			if(!peek().equals(TokenType.SEMICN)){
				if(isVoid){ logger.log('f', curLine); }
				exp = parseExp();
			}
			if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		}
		else if(peek().equals(TokenType.PRINTFTK)){
			type = "printf";
			Token printTk = pass(TokenType.PRINTFTK); // printf
			pass(TokenType.LPARENT);    // (
			Token formString = pass(TokenType.STRCON);
			if(formString != null){ formatString = new FormatString(formString.val); }
			if(formatString != null && !formatString.legal){ logger.log('a', curLine); }
			while(peek().equals(TokenType.COMMA)){
				pass(TokenType.COMMA); // ,
				exps.add(parseExp());
			}
			if(printTk != null && exps.size() != formatString.formatCharCnt){
				logger.log('l', printTk.line);
			}
			if(pass(TokenType.RPARENT) == null){ logger.log('j', curLine); }  // )
			if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		}
		else if(peek().equals(TokenType.SEMICN)){
			type = "empty";
			pass(TokenType.SEMICN); // ;
		}
		else if(peek().equals(TokenType.TAG)){      // for debugging
			type = "tag";
			tag = pass().val;
			pass(TokenType.TAG);    // #
		}
		else if(peek().equals(TokenType.IDENFR)){
			int offset = 0;
			while(!(peek(offset).equals(TokenType.ASSIGN) ||
			        peek(offset).equals(TokenType.SEMICN))){ offset++; }
			if(peek(offset).equals(TokenType.SEMICN)){
				type = "exp";
				exp = parseExp();
				if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
			}
			else if(peek(offset).equals(TokenType.ASSIGN)){
				if(peek(offset + 1).equals(TokenType.GETINTTK)){
					type = "getint";
					lVal = parseLVal();
					if(curST.get(lVal.ident.val).type.equals("const")){
						logger.log('h', curLine);
					}
					pass(TokenType.ASSIGN); // =
					pass(TokenType.GETINTTK);   // getint
					pass(TokenType.LPARENT);    // (
					if(pass(TokenType.RPARENT) == null){
						logger.log('j', curLine);    // )
					}
					if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
				}
				else{
					type = "assign";
					lVal = parseLVal();
					if(curST.contains(lVal.ident.val) &&
					   curST.get(lVal.ident.val).type.equals("const")){
						logger.log('h', curLine);
					}
					pass(TokenType.ASSIGN); // =
					exp = parseExp();
					if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
				}
			}
		}
		else{
			type = "exp";
			exp = parseExp();
			if(pass(TokenType.SEMICN) == null){ logger.log('i', curLine); } // ;
		}
		printNode(Stmt.class);
		return new Stmt(type, lVal, exp, exps, block, cond, stmt1, stmt2, formatString, tag);
	}

	// Exp → AddExp
	private Exp parseExp(){
		Exp exp = parseAddExp();
		printNode(Exp.class);
		return exp;
	}

	// Cond → LOrExp
	private Cond parseCond(){
		Cond cond = parseLOrExp();
		printNode(Cond.class);
		return cond;
	}

	// LVal → Ident {'[' Exp ']'}
	private LVal parseLVal(){
		Token ident = pass(TokenType.IDENFR);
		if(ident != null && !curST.contains(ident.val)){ logger.log('c', curLine); }
		int identDim = curST.contains(ident.val)? ((VarSym)curST.get(ident.val)).dim: 0;
		ArrayList<Exp> exps = new ArrayList<>();
		int dim = 0;
		while(peek().equals(TokenType.LBRACK)){
			dim++;
			pass(TokenType.LBRACK); // [
			exps.add(parseExp());
			if(pass(TokenType.RBRACK) == null){
				logger.log('k', curLine); // ]
			}
		}
		check(dim < 3);
		printNode(LVal.class);
		return new LVal(ident, exps, identDim - dim);
	}

	// PrimaryExp → '(' Exp ')' | LVal | Number
	private PrimaryExp parsePrimaryExp(){
		Exp exp = null;
		LVal lVal = null;
		Token number = null;
		if(peek().equals(TokenType.LPARENT)){
			pass(TokenType.LPARENT); // (
			exp = parseExp();
			if(pass(TokenType.RPARENT) == null){
				logger.log('j', curLine);    // )
			}
		}
		else if(peek().equals(TokenType.IDENFR)){ lVal = parseLVal(); }
		else if(peek().equals(TokenType.INTCON)){
			number = pass(TokenType.INTCON);
			if(OUTPUT_NODE){ output("<Number>"); }
		}
		printNode(PrimaryExp.class);
		return new PrimaryExp(exp, lVal, number);
	}

	// UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
	private UnaryExp parseUnaryExp(){
		PrimaryExp primaryExp = null;
		Token ident = null;
		FuncRParams funcRParams = null;
		TokenType unaryOp = null;
		UnaryExp unaryExp = null;
		int dim = 0;
		if(peek().equals(TokenType.IDENFR) && peek(1).equals(TokenType.LPARENT)){
			ident = pass(TokenType.IDENFR);
			if(ident != null && !curST.contains(ident.val)){ logger.log('c', curLine); }
			else if(ident != null){
				dim = ((FuncSym)curST.get(ident.val)).retType.equals("void")? -1: 0;
			}
			pass(TokenType.LPARENT);    // (
			if(!peek().equals(TokenType.SEMICN) && !peek().equals(TokenType.RPARENT)){
				funcRParams = parseFuncRParams();
				if(ident != null && curST.get(ident.val) instanceof FuncSym){
					ArrayList<VarSym> varSyms = ((FuncSym)curST.get(ident.val)).varSyms;
					if(varSyms.size() != funcRParams.exps.size()){ logger.log('d', ident.line); }
					for(int i = 0; i < funcRParams.exps.size(); i++){
						if(i < varSyms.size() &&
						   ((AddExp)funcRParams.exps.get(i)).dim != varSyms.get(i).dim){
							logger.log('e', ident.line);
						}
					}
				}
			}
			if(pass(TokenType.RPARENT) == null){
				logger.log('j', curLine);    // )
			}
		}
		else if(peek().equals(TokenType.PLUS) || peek().equals(TokenType.MINU) ||
		        peek().equals(TokenType.NOT)){
			unaryOp = pass().type;      // +/-/!
			if(OUTPUT_NODE){ output('<' + "UnaryOp" + '>'); }
			unaryExp = parseUnaryExp();
		}
		else{
			primaryExp = parsePrimaryExp();
			dim = primaryExp.dim;
		}
		printNode(UnaryExp.class);
		return new UnaryExp(primaryExp, ident, funcRParams, unaryOp, unaryExp, dim);
	}

	// FuncRParams → Exp { ',' Exp }
	private FuncRParams parseFuncRParams(){
		ArrayList<Exp> exps = new ArrayList<>();
		exps.add(parseExp());
		while(peek().equals(TokenType.COMMA)){
			pass(TokenType.COMMA); // ,
			exps.add(parseExp());
		}
		printNode(FuncRParams.class);
		return new FuncRParams(exps);
	}

	// MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
	private MulExp parseMulExp(){
		ArrayList<UnaryExp> unaryExps = new ArrayList<>();
		ArrayList<TokenType> ops = new ArrayList<>();
		unaryExps.add(parseUnaryExp());
		printNode(MulExp.class);
		while(peek().equals(TokenType.MULT) || peek().equals(TokenType.DIV) ||
		      peek().equals(TokenType.MOD)){
			ops.add(pass().type);   // */%//
			unaryExps.add(parseUnaryExp());
			printNode(MulExp.class);
		}
		return new MulExp(unaryExps, ops);
	}

	// AddExp → MulExp | AddExp ('+' | '−') MulExp
	private AddExp parseAddExp(){
		ArrayList<MulExp> mulExps = new ArrayList<>();
		ArrayList<TokenType> ops = new ArrayList<>();
		mulExps.add(parseMulExp());
		printNode(AddExp.class);
		while(peek().equals(TokenType.PLUS) || peek().equals(TokenType.MINU)){
			ops.add(pass().type);    // +/-
			mulExps.add(parseMulExp());
			printNode(AddExp.class);
		}
		return new AddExp(mulExps, ops);
	}

	// RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
	private RelExp parseRelExp(){
		ArrayList<AddExp> addExps = new ArrayList<>();
		ArrayList<TokenType> ops = new ArrayList<>();
		addExps.add(parseAddExp());
		printNode(RelExp.class);
		while(peek().equals(TokenType.GRE) || peek().equals(TokenType.LSS) ||
		      peek().equals(TokenType.GEQ) || peek().equals(TokenType.LEQ)){
			ops.add(pass().type);    // >/</>=/<=
			addExps.add(parseAddExp());
			printNode(RelExp.class);
		}
		return new RelExp(addExps, ops);
	}

	// EqExp → RelExp | EqExp ('==' | '!=') RelExp
	private EqExp parseEqExp(){
		ArrayList<RelExp> relExps = new ArrayList<>();
		ArrayList<TokenType> ops = new ArrayList<>();
		relExps.add(parseRelExp());
		printNode(EqExp.class);
		while(peek().equals(TokenType.EQL) || peek().equals(TokenType.NEQ)){
			ops.add(pass().type);    // ==/!=
			relExps.add(parseRelExp());
			printNode(EqExp.class);
		}
		return new EqExp(relExps, ops);
	}

	// LAndExp → EqExp | LAndExp '&&' EqExp
	private LAndExp parseLAndExp(){
		ArrayList<EqExp> eqExps = new ArrayList<>();
		eqExps.add(parseEqExp());
		printNode(LAndExp.class);
		while(peek().equals(TokenType.AND)){
			pass(TokenType.AND); // &&
			eqExps.add(parseEqExp());
			printNode(LAndExp.class);
		}
		return new LAndExp(eqExps);
	}

	// LOrExp → LAndExp | LOrExp '||' LAndExp
	private LOrExp parseLOrExp(){
		ArrayList<LAndExp> lAndExps = new ArrayList<>();
		lAndExps.add(parseLAndExp());
		printNode(LOrExp.class);
		while(peek().equals(TokenType.OR)){
			pass(TokenType.OR); // ||
			lAndExps.add(parseLAndExp());
			printNode(LOrExp.class);
		}
		return new LOrExp(lAndExps);
	}

	// ConstExp → AddExp
	private ConstExp parseConstExp(){
		ConstExp constExp = parseAddExp();
		printNode(ConstExp.class);
		return constExp;
	}
}