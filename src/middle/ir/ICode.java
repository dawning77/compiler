package middle.ir;

import backend.mips.instr.*;
import backend.mips.reg.*;

import java.util.*;

public interface ICode{
	@Override
	String toString();

	ArrayList<Instr> toInstr(RegManager regManager);
}
