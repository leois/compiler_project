package Semantic.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Semantic.E;
import Semantic.ErrorTypes;
import Semantic.SymbolTable.Structure.Method;
import Semantic.SymbolTable.Structure.Symbol;
import Semantic.SymbolTable.Structure.Table;
import AST.ClassDeclExtends;
import AST.ClassDeclSimple;
import AST.Formal;
import AST.IdentifierType;
import AST.MethodDecl;
import AST.Program;
import AST.Type;
import AST.VarDecl;
import AST.Visitor.VisitorSymbol;

public class TypeChecker implements VisitorSymbol{
	
	private Table _table;
	private List<E> _errors;
	
	public TypeChecker(Table table){
		_table = table;
		_errors = new ArrayList<E>();
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
		
		Symbol claseSymbol = new Symbol(n.i.s, n.i.getLine());
		Map<Symbol, Type> variables = _table.getTable().get(claseSymbol).getVariables();
		
		for ( int i = 0; i < n.vl.size(); i++ ) {
			VarDecl vd = n.vl.get(i);
			
			Symbol variable = new Symbol(vd.i.s, vd.i.getLine());
			
			Type type = variables.get(variable);
			boolean isIdentifier = type.getClass().isAssignableFrom(IdentifierType.class);
			if( isIdentifier ){
				IdentifierType id = (IdentifierType) type;
				Symbol symbolClass = new Symbol(id.s, id.getLine());
				boolean defined = symbolClass.isMapped(_table.getTable().keySet());
				if( !defined )
					_errors.add(new E(ErrorTypes.UNDECLARED_TYPE.mss(), id.getLine()));
			}
		}
		
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.get(i).accept(this);
		}
	}
	 
	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) {
		Symbol claseSymbol = new Symbol(n.i.s, n.i.getLine());
		
		//super class checking
		Symbol superClass = new Symbol(n.j.s, n.j.getLine());
		boolean defined = superClass.isMapped(_table.getTable().keySet());
		if( !defined )
			_errors.add(new E(ErrorTypes.UNDECLARED_TYPE, n.j.getLine()));
		
		
		
		//variables checking
		Map<Symbol, Type> variables = _table.getTable().get(claseSymbol).getVariables();
		
		for ( int i = 0; i < n.vl.size(); i++ ) {
			VarDecl vd = n.vl.get(i);
			
			Symbol variable = new Symbol(vd.i.s, vd.i.getLine());
			
			Type type = variables.get(variable);
			boolean isIdentifier = type.getClass().isAssignableFrom(IdentifierType.class);
			if( isIdentifier ){
				IdentifierType id = (IdentifierType) type;
				Symbol symbolClass = new Symbol(id.s, id.getLine());
				defined = symbolClass.isMapped(_table.getTable().keySet());
				if( !defined )
					_errors.add(new E(ErrorTypes.UNDECLARED_TYPE.mss(), id.getLine()));
			}
		}
		
		for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.get(i).accept(this);
		}
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
	  	//method.setClase(_currentClass.getSymbol());
	  	
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
	    //_currentClass.addMethod(symbol, method);
	}


	public List<E> getErrors() {
		return _errors;
	}


	public void setErrors(List<E> errors) {
		this._errors = errors;
	}
}
