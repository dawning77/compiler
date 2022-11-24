package frontend.symbol;

import java.util.HashMap;

public class SymbolTable {
    public final SymbolTable parent;
    private final HashMap<String, Symbol> symbols;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    public void put(Symbol symbol) { symbols.put(symbol.name, symbol); }

    public Symbol get(String id) {
        if (symbols.containsKey(id)) { return symbols.get(id); }
        if (parent == null) { return null; }
        return parent.get(id);
    }

    public boolean contains(String id) { return get(id) != null; }

    public boolean localContains(String id) { return symbols.containsKey(id); }
}
