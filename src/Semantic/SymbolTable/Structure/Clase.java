package Semantic.SymbolTable.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Semantic.E;
import Semantic.ErrorTypes;


import AST.IdentifierType;
import AST.Type;

public class Clase{
	private Symbol _symbol;
	private Type _type;
	private Symbol _superClass;
	private Map<Symbol, Type> _variables;
	private Map<Symbol, Method> _methods;
	private List<E> _errors;
	
	public Clase(Symbol symbol, int line){
		initializeInstanceVariables(symbol, line);
	}
	
	public Clase(Symbol symbol, Symbol sSymbol, int line){
		initializeInstanceVariables(symbol, line);
		_superClass = sSymbol;
	}
	
	public void addMethod(Symbol symbol, Method method){
		if( symbol.isMapped(_methods.keySet()) ){
			_errors.add(new E(ErrorTypes.METHOD_ALREADY_DEFINED.mss(), method.getLine()));
		}
		_methods.put(symbol, method);
	}
	
	public void addVariable(Symbol symbol, Type variable){
		if( symbol.isMapped(_variables.keySet()) ){
			_errors.add(new E(ErrorTypes.VARIABLE_ALREADY_DEFINED.mss(), variable.getLine()));
		}
		_variables.put(symbol, variable);
	}
	
	public Type searchVariableByName(String var){
		for(Symbol s : _variables.keySet()){
			if(s.getId().equals(var))
				return _variables.get(s);
		}
		return null;
	}

	public Symbol getSymbol() {
		return _symbol;
	}

	public void setSymbol(Symbol symbol) {
		this._symbol = symbol;
	}

	public Type getType() {
		return _type;
	}

	public void setType(Type type) {
		this._type = type;
	}

	public Symbol getSuperClass() {
		return _superClass;
	}

	public void setSuperClass(Symbol superClass) {
		this._superClass = superClass;
	}

	public Map<Symbol, Type> getVariables() {
		return _variables;
	}

	public void setVariables(Map<Symbol, Type> variables) {
		this._variables = variables;
	}

	public Map<Symbol, Method> getMethods() {
		return _methods;
	}

	public void setMethods(Map<Symbol, Method> methods) {
		this._methods = methods;
	}
	
	private void initializeInstanceVariables(Symbol className, int line){
		_symbol = className;
		_type = new IdentifierType(className.toString(), line);
		_variables = new HashMap<Symbol, Type>();
		_methods = new HashMap<Symbol, Method>();
		_errors = new ArrayList<E>();
	}

	public List<E> getErrors() {
		return _errors;
	}

	public void setErrors(List<E> errors) {
		this._errors = errors;
	}
}
