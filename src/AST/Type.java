package AST;
import AST.Visitor.Visitor;

public abstract class Type extends ASTNode {
    
	private int _offset;
	
	public Type(int ln) {
        super(ln);
    }
    public abstract void accept(Visitor v);
    
    public int getOffset(){
    	return _offset;
    }
    
    public void setOffset(int value){
    	_offset = value;
    }
}
