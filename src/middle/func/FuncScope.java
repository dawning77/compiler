package middle.func;
import middle.ir.*;
import middle.ir.br.*;
import middle.operand.symbol.*;

import java.util.*;

public class FuncScope{
	public final String name;
	public final String retType;
	public final BasicBlock firstBB;

	public final ArrayList<Symbol> params;
	public final ArrayList<Symbol> locals;
	public final ArrayList<Symbol> tmps;
	public boolean hasLastReturn;
	public BasicBlock lastBB; // for active analyse
	public int frameSize;
	public int paramSize;

	public FuncScope(String name, String retType, BasicBlock firstBB){
		this.name = name;
		this.retType = retType;
		this.firstBB = firstBB;
		this.hasLastReturn = false;
		this.frameSize = 0;
		this.paramSize = 0;
		this.params = new ArrayList<>();
		this.locals = new ArrayList<>();
		this.tmps = new ArrayList<>();
	}

	public void formFrame(){
		for(Symbol tmp: tmps){
			tmp.loc = frameSize;
			frameSize += tmp.size;
		}
		for(Symbol local: locals){
			local.loc = frameSize;
			frameSize += local.size;
		}
		for(Symbol param: params){
			param.loc = frameSize + paramSize;
			paramSize += 1;
		}
	}

	public void deleteEmptyBlock(){
		BasicBlock bb1 = firstBB;
		// last block cant be empty
		while(bb1.follow!=null){
			// empty block
			if(bb1.iCodes.size()==1){
				BasicBlock followBB = bb1.follow;
				HashSet<BasicBlock> emptys = new HashSet<>();
				emptys.add(bb1);
				while(followBB.iCodes.size()==0 && followBB.follow!=null){
					emptys.add(followBB);
					followBB = followBB.follow;
				}
				// followBB is not empty or has been the last block
				BasicBlock bb2 = firstBB;
				BasicBlock bb1Prev = null;
				while(bb2.follow!=null){
					for(ICode iCode: bb2.iCodes){
						if(iCode instanceof Br&&emptys.contains(((Br)iCode).bb)){
							((Br)iCode).bb = followBB;
						}
						else if(iCode instanceof Jmp&&emptys.contains(((Jmp)iCode).bb)){
							((Jmp)iCode).bb = followBB;
						}
					}
					if(bb2.follow.equals(bb1))bb1Prev = bb2;
					bb2 = bb2.follow;
				}
				assert bb1Prev != null;
				bb1Prev.follow = followBB;
			}
			bb1 = bb1.follow;
		}
		lastBB = bb1;
	}

	private HashMap<BasicBlock, HashSet<Var>> defSet;
	private HashMap<BasicBlock, HashSet<Var>> useSet;
	private HashMap<BasicBlock, HashSet<Var>> inSet;
	private HashMap<BasicBlock, HashSet<Var>> outSet;

	public void activeAnalyse(){

	}
}
