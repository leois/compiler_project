package AST.Visitor;

import AST.ClassDeclExtends;
import AST.ClassDeclSimple;
import AST.MethodDecl;
import AST.Program;

public interface VisitorSymbol {
	public void visit(Program n);
	public void visit(ClassDeclSimple n);
	public void visit(ClassDeclExtends n);
	public void visit(MethodDecl n);
}
