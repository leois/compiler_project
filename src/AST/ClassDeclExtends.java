package AST;
import AST.Visitor.Visitor;
import AST.Visitor.VisitorSymbol;

public class ClassDeclExtends extends ClassDecl {
  public Identifier i;
  public Identifier j;
  public VarDeclList vl;  
  public MethodDeclList ml;
 
  public ClassDeclExtends(Identifier ai, Identifier aj, 
                  VarDeclList avl, MethodDeclList aml, int ln) {
    super(ln);
    i=ai; j=aj; vl=avl; ml=aml;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public void accept(VisitorSymbol vs) {
	vs.visit(this);
  }
}
