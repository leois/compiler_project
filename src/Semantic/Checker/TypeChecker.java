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
import AST.*;
import AST.Visitor.Visitor;
public class TypeChecker implements Visitor{
	
	private Table _table;
	private List<E> _errors;
	private Symbol _currentClass;
	private Symbol _currentMethod;
	private Type _returnType;
	private Exp _returnExpression;
	
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
		_currentClass = new Symbol(n.i.s, n.i.getLine());
		
		//super class checking
		Symbol superClass = new Symbol(n.j.s, n.getLine());
		checkType(superClass, _table.getTable().keySet());
		
		//variables checking
		Map<Symbol, Type> variables = _table.getTable().get(_currentClass).getVariables();
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
		_currentMethod = methodSymbol;
		Method method = _table.getTable().get(_currentClass).getMethods().get(methodSymbol);
	  	
		checkIdentifier(method.getReturnType(), _table.getTable().keySet());
		checkReturnType(method.getReturnType(), n.e);
	  	
	  	checkArguments(n.fl, method.getArguments(), _table.getTable().keySet());
	    
	    checkVariables(n.vl, method.getVariables(), _table.getTable().keySet());
	    _currentMethod = null;
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
	
	private void checkReturnType(Type type, Exp e){
		boolean same = false;
		e.accept(this);
		if(type.getClass().isAssignableFrom(IdentifierType.class) && _returnType != null &&
				_returnType.getClass().isAssignableFrom(IdentifierType.class)){
			IdentifierType id1 = (IdentifierType) type;
			IdentifierType id2 = (IdentifierType) _returnType;
			if(id1.s.equals(id2.s)) 
				same = true;
			
		}else if(_returnType != null ){
			same = type.getClass().isAssignableFrom(_returnType.getClass());
		}
		
		if( !same ){
			_errors.add(new E(ErrorTypes.WRONG_RETURN.mss(), e.getLine()));
		}
		
	}

	@Override
	public void visit(IntArrayType n) {
		_returnType = n;
	}

	@Override
	public void visit(BooleanType n) {
		_returnType = n;
	}

	@Override
	public void visit(IntegerType n) {
		_returnType = n;
	}

	@Override
	public void visit(IdentifierType n) {
		_returnType = n;
	}

	@Override
	public void visit(And n) {
		_returnType = new BooleanType(n.getLine());
	}


	@Override
	public void visit(LessThan n) {
		_returnType = new BooleanType(n.getLine());
	}


	@Override
	public void visit(Plus n) {
		_returnType = new IntegerType(n.getLine());
	}


	@Override
	public void visit(Minus n) {
		_returnType = new IntegerType(n.getLine());
	}


	@Override
	public void visit(Times n) {
		_returnType = new IntegerType(n.getLine());
	}


	@Override
	public void visit(ArrayLookup n) {
		_returnType = new IntegerType(n.getLine());
	}


	@Override
	public void visit(ArrayLength n) {
		_returnType = new IntegerType(n.getLine());
	}


	@Override
	public void visit(Call n) {
		n.e.accept(this);
		//finding return type
        Clase c = null;
		if(_returnExpression.getClass().isAssignableFrom(NewObject.class)){
			NewObject no = (NewObject) _returnExpression;
			c = _table.searchClassByName(no.i.s);
		}else if(_returnExpression.getClass().isAssignableFrom(IdentifierExp.class)){
			IdentifierExp ie = (IdentifierExp) _returnExpression;
			IdentifierType t = (IdentifierType) searchVariableType(ie.s);
			c = _table.searchClassByName(t.s);
		}else if(_returnExpression.getClass().isAssignableFrom(This.class)){
			c = _table.getTable().get(_currentClass);
		}
		
		_returnType =  (c != null)? searchMethodTypeByName(c, n.i.s) : null;
				
		_returnExpression = n;
	}
	
	private Type searchMethodTypeByName(Clase c, String met){
		Type t = c.searchMethodTypeByName(met);
		if(t == null && c.getSuperClass() != null){
			c = _table.searchClassByName(c.getSuperClass().getId());
			do{
				t = c.searchMethodTypeByName(met);
				c = _table.getTable().get(c.getSuperClass());
			}while(t == null && c != null);
		}
		return t;
	}


	@Override
	public void visit(IntegerLiteral n) {
		_returnType = new IntegerType(n.getLine());
	}


	@Override
	public void visit(True n) {
		_returnType = new BooleanType(n.getLine());
	}


	@Override
	public void visit(False n) {
		_returnType = new BooleanType(n.getLine());
	}


	@Override
	public void visit(IdentifierExp n) {
		_returnExpression = n;
		Type t = searchVariableType(n.s);
		_returnType = t;
	}
	
	private Type searchVariableType(String var){
		Method method = _table.getTable().get(_currentClass).getMethods().get(_currentMethod);
		Type t = method.searchByName(var);
		if( t == null ){
			Clase clase = _table.getTable().get(_currentClass);
			do{
				t = clase.searchVariableByName(var);
				clase = _table.getTable().get(clase.getSuperClass());
			}while(t == null && clase != null);
		}
		return t;
	}


	@Override
	public void visit(This n) {
		_returnExpression = n;
		_returnType = new IdentifierType(_currentClass.getId(), n.getLine());
	}


	@Override
	public void visit(NewArray n) {
		_returnExpression = n;
		_returnType = new IntArrayType(n.getLine());
	}


	@Override
	public void visit(NewObject n) {
		_returnExpression = n;
		_returnType = new IdentifierType(n.i.s, n.getLine());
	}


	@Override
	public void visit(Not n) {
		_returnType = new BooleanType(n.getLine());
	}


	@Override
	public void visit(Display n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(MainClass n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(VarDecl n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(Formal n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(Block n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(If n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(While n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(Print n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(Assign n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(ArrayAssign n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(Identifier n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(Empty n) {
		// TODO Auto-generated method stub
		
	}
}
