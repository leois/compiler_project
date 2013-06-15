package AST;
import AST.Visitor.Visitor;
import AST.Visitor.VisitorSymbol;

public abstract class ClassDecl extends ASTNode{
  public ClassDecl(int ln) {
    super(ln);
  }
  public abstract void accept(Visitor v);
  public abstract void accept(VisitorSymbol vs);
}
