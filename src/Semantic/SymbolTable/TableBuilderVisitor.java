package Semantic.SymbolTable;

import Semantic.SymbolTable.Structure.Clase;
import Semantic.SymbolTable.Structure.Method;
import Semantic.SymbolTable.Structure.Symbol;
import Semantic.SymbolTable.Structure.Table;
import AST.*;
import AST.Visitor.VisitorSymbol;

public class TableBuilderVisitor implements VisitorSymbol {
	private Clase _currentClass;
	private Table _table = new Table();
	
	public Table getTable(){
		return _table;
	}
	
  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    Symbol symbol = new Symbol(n.i.s, n.getLine());
    _currentClass = new Clase(symbol, n.getLine());
    
    for ( int i = 0; i < n.vl.size(); i++ ) {
    	VarDecl vd = n.vl.get(i);
    	
    	Symbol s = new Symbol(vd.i.s, vd.i.getLine());
    	Type t = vd.t;
    	_currentClass.addVariable(s, t);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
    	n.ml.get(i).accept(this);
    }
    _table.add(symbol, _currentClass);
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
	    Symbol symbol = new Symbol(n.i.s, n.i.getLine());
	    Symbol symbol2 = new Symbol(n.j.s, n.j.getLine());
	    _currentClass = new Clase(symbol, symbol2, n.getLine());
	    
	    for ( int i = 0; i < n.vl.size(); i++ ) {
	    	VarDecl vd = n.vl.get(i);
	    	
	    	Symbol s = new Symbol(vd.i.s, vd.i.getLine());
	    	Type t = vd.t;
	    	_currentClass.addVariable(s, t);
	    }
	    for ( int i = 0; i < n.ml.size(); i++ ) {
	    	n.ml.get(i).accept(this);
	    }
	    _table.add(symbol, _currentClass);
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {
  	
  	Symbol symbol = new Symbol(n.i.s, n.i.getLine());
  	Method method = new Method(symbol, n.getLine());
  	
  	method.setReturnType(n.t);
  	method.setClase(_currentClass.getSymbol());
  	
    for ( int i = 0; i < n.fl.size(); i++ ) {
    	Formal f = n.fl.get(i);
    	Symbol s = new Symbol(f.i.s, f.i.getLine());
    	method.addArgument(s, f.t);
    }
    
    for ( int i = 0; i < n.vl.size(); i++ ) {
        VarDecl vd = n.vl.get(i);
        Symbol s = new Symbol(vd.i.s, vd.i.getLine());
        method.addVariable(s, vd.t);
    }
    _currentClass.addMethod(symbol, method);
  }
  
}
