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
		n.m.accept(this);
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
	    
	    for(int i=0; i<n.sl.size(); i++){
	    	n.sl.get(i).accept(this);
	    }
	    
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
	
	private void checkParameters(Method method, ExpList expressions, int line){
		//amount
		if(method.getArgumentList().size() == expressions.size()){
			boolean same = false;
			for(int i=0; i<expressions.size(); i++){
				same = false;
				expressions.get(i).accept(this);
				same = typeOfExpression(method.getArgumentList().get(i));	
				if( !same ){
					_errors.add(new E(ErrorTypes.WRONG_TYPE_ARGUMENT.mss(), line));
				}
			}
		}else{
			_errors.add(new E(ErrorTypes.WRONG_NUMBER_ARGUMENTS.mss(), line));
		}
	}
	
	private boolean typeOfExpression(Type type){
		boolean same = false;
		if(type.getClass().isAssignableFrom(IdentifierType.class) && _returnType != null &&
				_returnType.getClass().isAssignableFrom(IdentifierType.class)){
			IdentifierType id1 = (IdentifierType) type;
			IdentifierType id2 = (IdentifierType) _returnType;
			if(id1.s.equals(id2.s)) 
				same = true;
			
		}else if(_returnType != null ){
			same = type.getClass().isAssignableFrom(_returnType.getClass());
		}
		return same;
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
		e.accept(this);
		boolean same = typeOfExpression(type);
		
		if( !same ){
			_errors.add(new E(ErrorTypes.WRONG_RETURN.mss(), e.getLine()));
		}
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
	
	private Method searchMethodByName(Clase c, String met){
		Method m = c.searchMethodByName(met);
		if(m == null && c.getSuperClass() != null){
			c = _table.searchClassByName(c.getSuperClass().getId());
			do{
				m = c.searchMethodByName(met);
				c = (c.getSuperClass() != null)? _table.searchClassByName(c.getSuperClass().getId()) : null;
			}while(m == null && c != null);
		}
		return m;
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
			if( t!= null)
				c = _table.searchClassByName(t.s);
		}else if(_returnExpression.getClass().isAssignableFrom(This.class)){
			c = _table.getTable().get(_currentClass);
		}
		Type rt = null;
		if( c != null){
			Method method = searchMethodByName(c, n.i.s);
			rt =  (method != null)? method.getReturnType() : null;
			checkParameters(method, n.el, n.getLine());
			
		}else{
			rt = null;
		}
		_returnType = rt;
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
		n.e.accept(this);
		boolean isBoolean = typeOfExpression(new BooleanType(n.getLine()));
		if( !isBoolean ){
			_errors.add(new E(ErrorTypes.NOT_BOOLEAN, n.getLine()));
		}
		_returnType = new BooleanType(n.getLine());
	}


	@Override
	public void visit(Display n) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visit(MainClass n) {
		n.s.accept(this);
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
		n.e.accept(this);
		Type type = searchVariableType(n.i.s);
		boolean same = typeOfExpression(type);
		if( !same ){
			_errors.add(new E(ErrorTypes.WRONG_TYPE_ASSIGN, n.getLine()));
		}
	}


	@Override
	public void visit(ArrayAssign n) {
		Type type = searchVariableType(n.i.s);
		if(type.getClass().isAssignableFrom(IntArrayType.class)){
			n.e1.accept(this);
			boolean b1 = typeOfExpression(new IntegerType(n.getLine()));
			n.e2.accept(this);
			boolean b2 = typeOfExpression(new IntegerType(n.getLine()));
			if( !b1)
				_errors.add(new E(ErrorTypes.WRONG_INDEX, n.getLine()));
			if( !b2)
				_errors.add(new E(ErrorTypes.WRONG_TYPE_ASSIGN, n.getLine()));
		}else{
			_errors.add(new E(ErrorTypes.WRONG_TYPE_ASSIGN.mss(), n.getLine()));
		}
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
