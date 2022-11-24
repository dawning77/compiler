package middle.ir.mem;

import backend.*;
import backend.mips.instr.*;
import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.symbol.Symbol;

import java.util.*;

public class CreatePtr implements ICode{
	public Symbol sym;
	public int size;

	public CreatePtr(Symbol sym, int size){
		this.sym = sym;
		this.size = size;
	}

	@Override
	public String toString(){ return this.getClass().getSimpleName() + " " + sym + " " + size; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		// TODO: 2022/10/27 ???
		return ret;
	}
}
