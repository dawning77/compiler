package backend.mips.instr;

public class Syscall implements Instr{
	public Syscall(){
	}

	@Override
	public String toString(){ return "syscall"; }
}
