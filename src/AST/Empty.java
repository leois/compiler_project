package AST;
import AST.Visitor.Visitor;

public class Empty extends Statement {

  public Empty(int ln) {
    super(ln);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
