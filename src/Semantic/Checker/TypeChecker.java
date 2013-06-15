package Semantic.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Semantic.E;
import Semantic.ErrorTypes;
import Semantic.SymbolTable.Structure.Clase;
import Semantic.SymbolTable.Structure.Method;
import Semantic.SymbolTable.Structure.Symbol;
import Semantic.SymbolTable.Structure.Table;
import AST.ClassDeclExtends;
import AST.ClassDeclSimple;
import AST.Formal;
import AST.FormalList;
import AST.IdentifierType;
import AST.MethodDecl;
import AST.Program;
import AST.Type;
import AST.VarDecl;
import AST.VarDeclList;
import AST.Visitor.VisitorSymbol;

public class TypeChecker implements VisitorSymbol{
	
	private Table _table;
	private List<E> _errors;
	private Symbol _currentClass;
	
	public TypeChecker(Table table){
		_table = table;
		_errors = new ArrayList<E>();
	}
	
	
	// MainClass m;
	// ClassDeclList0 cl;
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
		_currentClass = claseSymbol;
		
		//variables checking
		Map<Symbol, Type> variables = _table.getTable().get(claseSymbol).getVariables();
		checkVariables(n.vl, variables, _table.getTable().keySet());
		
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
		_currentClass = claseSymbol;
		
		//super class checking
		Symbol superClass = new Symbol(n.j.s, n.getLine());
		checkType(superClass, _table.getTable().keySet());
		
		//variables checking
		Map<Symbol, Type> variables = _table.getTable().get(claseSymbol).getVariables();
		checkVariables(n.vl, variables, _table.getTable().keySet());
		
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
		Symbol methodSymbol = new Symbol(n.i.s, n.i.getLine());
		Method method = _table.getTable().get(_currentClass).getMethods().get(methodSymbol);
	  	
		checkIdentifier(method.getReturnType(), _table.getTable().keySet());
		
	  	method.setReturnType(n.t);
	  	//method.setClase(_currentClass.getSymbol());
	  	
	  	checkArguments(n.fl, method.getArguments(), _table.getTable().keySet());
	    
	    checkVariables(n.vl, method.getVariables(), _table.getTable().keySet());
	}


	public List<E> getErrors() {
		return _errors;
	}


	public void setErrors(List<E> errors) {
		this._errors = errors;
	}
	
	private void checkVariables(VarDeclList declarations, Map<Symbol, Type> declaredList, Set<Symbol> availableTypes){
		for ( int i = 0; i < declarations.size(); i++ ) {
			VarDecl vd = declarations.get(i);
			
			Symbol declared = new Symbol(vd.i.s, vd.i.getLine());
			
			Type type = declaredList.get(declared);
			checkIdentifier(type, availableTypes);
		}
	}
	
	private void checkArguments(FormalList arguments, Map<Symbol, Type> declaredList, Set<Symbol> availableTypes){
		for ( int i = 0; i < arguments.size(); i++ ) {
			Formal f = arguments.get(i);
			
			Symbol declared = new Symbol(f.i.s, f.i.getLine());
			
			Type type = declaredList.get(declared);
			checkIdentifier(type, availableTypes);
		}
	}
	
	private void checkType(Symbol s, Set<Symbol> types){
		boolean defined = s.isMapped(_table.getTable().keySet());
		if( !defined )
			_errors.add(new E(ErrorTypes.UNDECLARED_TYPE.mss() + " " + s.getId(), s.getLine()));
	}
	
	private void checkIdentifier(Type type, Set<Symbol> types){
		boolean isIdentifier = type.getClass().isAssignableFrom(IdentifierType.class);
		if( isIdentifier ){
			IdentifierType id = (IdentifierType) type;
			Symbol symbolClass = new Symbol(id.s, id.getLine());
			boolean defined = symbolClass.isMapped(types);
			if( !defined )
				_errors.add(new E(ErrorTypes.UNDECLARED_TYPE.mss() + " " + id.s, id.getLine()));
		}
	}
}
