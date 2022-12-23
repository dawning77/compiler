package middle;

import middle.func.*;
import middle.ir.*;

public class LoopInfo{
	public BasicBlock loopBefore;
	public ICode lastIR;
	public BasicBlock loopBody;
	public BasicBlock loopCond;
	public BasicBlock loopFollow;

	public LoopInfo(BasicBlock loopBefore, ICode lastIR, BasicBlock loopBody, BasicBlock loopCond,
			BasicBlock loopFollow){
		this.loopBefore = loopBefore;
		this.lastIR = lastIR;
		this.loopBody = loopBody;
		this.loopCond = loopCond;
		this.loopFollow = loopFollow;
	}
}
