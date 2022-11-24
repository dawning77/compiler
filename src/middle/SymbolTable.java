package middle;

import middle.operand.symbol.*;

import java.util.HashMap;

public class SymbolTable{
	public int id;
	public final SymbolTable parent;
	public FuncScope scope;
	public final HashMap<String, Symbol> symbols;

	public SymbolTable(int id, SymbolTable parent){
		this.id = id;
		this.parent = parent;
		if(parent != null) this.scope = parent.scope;
		else this.scope = null;
		this.symbols = new HashMap<>();
	}

	public SymbolTable(int id, SymbolTable parent, FuncScope scope){
		this.id = id;
		this.parent = parent;
		this.scope = scope;
		this.symbols = new HashMap<>();
	}

	public void put(String name, Symbol symbol){
		symbols.put(name, symbol);
		if(scope != null){
			switch(symbol.type){
				case tmp:
					scope.tmps.add(symbol); break;
				case local:
					System.out.println(symbol.name);
					scope.locals.add(symbol); break;
				case param:
					scope.params.add(symbol); break;
				default:
					break;
			}
		}
	}

	public Symbol get(String name){
		if(symbols.containsKey(name)){ return symbols.get(name); }
		if(parent == null){ return null; }
		return parent.get(name);
	}

	public boolean contains(String name){ return get(name) != null; }
}
