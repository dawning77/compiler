package middle.ir.br;

import backend.mips.instr.*;
import backend.mips.instr.jtype.*;
import backend.mips.reg.*;
import middle.*;
import middle.ir.*;

import java.util.*;

public class Jmp implements ICode{
	public BasicBlock bb;

	public Jmp(BasicBlock bb){
		this.bb = bb;
	}

	@Override
	public String toString(){ return "Jmp " + bb; }

	@Override
	public ArrayList<Instr> toInstr(RegManager regManager){
		ArrayList<Instr> ret = new ArrayList<>();
		regManager.setAllSpare();
		ret.add(new J(bb.toString()));
		return ret;
	}
}
