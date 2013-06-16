package AST;
import AST.Visitor.Visitor;

public abstract class Exp extends ASTNode {
	private Type _type;
	
    public Exp(int ln) {
        super(ln);
    }
    public abstract void accept(Visitor v);
    
	public Type getType() {
		return _type;
	}
	public void setType(Type type) {
		this._type = type;
	}
}
