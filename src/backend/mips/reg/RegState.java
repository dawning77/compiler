package backend.mips.reg;

import middle.operand.symbol.*;

import java.util.*;

public class RegState{
	public LinkedHashSet<Reg> spare;        // spare regs
	public LinkedHashMap<Reg, Symbol> used;    // used regs
	public LinkedHashMap<Symbol, Reg> vars;    // vars who occupies reg
	public HashSet<Symbol> inStack;    // symbols which need load before use

	public RegState(LinkedHashSet<Reg> spare, LinkedHashMap<Reg, Symbol> used, LinkedHashMap<Symbol, Reg> vars,
			HashSet<Symbol> inStack){
		this.spare = spare;
		this.used = used;
		this.vars = vars;
		this.inStack = inStack;
	}

	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder("----------RegState----------\n" +
		                                      "spare: ");
		for(Reg reg: spare){
			ret.append(reg.toString()).append(" ");
		}
		ret.append("\nused: ");
		for(Reg reg: used.keySet()){
			ret.append("(").append(reg.toString()).append(", ").append(used.get(reg)).append(") ");
		}
		ret.append("\ninstack: ");
		for(Symbol symbol: inStack){
			ret.append(symbol.toString()).append(" ");
		}
		ret.append("\n----------------------------");
		return ret.toString();
	}
}
