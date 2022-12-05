package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;

public class La implements Instr{
	public Reg res;
	public String label;
	public int offset;
	public Reg regOffset;

	public La(Reg res, String label){
		this.res = res;
		this.label = label;
		this.offset = -1;
		this.regOffset = null;
	}

	public La(Reg res, String label, int offset){
		this.res = res;
		this.label = label;
		this.offset = offset;
		this.regOffset = null;
	}

	public La(Reg res, String label, Reg regOffset){
		this.res = res;
		this.label = label;
		this.offset = -1;
		this.regOffset = regOffset;
	}

	@Override
	public String toString(){
		if(regOffset != null) return "la " + res + ", " + label + "(" + regOffset + ")";
		else if(offset != -1) return "la " + res + ", " + label + "+" + offset;
		else return "la " + res + ", " + label;
	}
}
