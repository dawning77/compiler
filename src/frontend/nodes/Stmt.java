package frontend.nodes;

import frontend.token.Token;

import java.util.ArrayList;

public class Stmt implements BlockItem{
	public String type;
	public LVal lVal;
	public Exp exp;
	public ArrayList<Exp> exps;
	public Block block;
	public Cond cond;
	public Stmt stmt1, stmt2;
	public FormatString formatString;
	public String tag;

	public Stmt(String type, LVal lVal, Exp exp, ArrayList<Exp> exps, Block block, Cond cond,
			Stmt stmt1, Stmt stmt2, FormatString formatString, String tag){
		this.type = type;
		this.lVal = lVal;
		this.exp = exp;
		this.exps = exps;
		this.block = block;
		this.cond = cond;
		this.stmt1 = stmt1;
		this.stmt2 = stmt2;
		this.formatString = formatString;
		this.tag = tag;
	}
}
