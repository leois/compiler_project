package Semantic.SymbolTable.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Semantic.E;
import Semantic.ErrorTypes;


import AST.Type;

public class Method{
	private Symbol _methodName;
	private Type _returnType;
	private Map<Symbol, Type> _arguments;
	private List<Type> _argumentList;
	private Map<Symbol, Type> _variables;
	private Symbol _clase;
	private int _line;
	private List<E> _errors;
	
	public Method(Symbol method, int line){
		_methodName = method;
		_line = line;
		_arguments = new HashMap<Symbol, Type>();
		_variables = new HashMap<Symbol, Type>();
		_argumentList = new ArrayList<Type>();
		_errors = new ArrayList<E>();
	}
	
	public void addArgument(Symbol symbol, Type type){
		if( symbol.isMapped(_arguments.keySet()) ){
			_errors.add(new E(ErrorTypes.ARGUMENT_ALREADY_DEFINED.mss(), type.getLine()));
		}
		_argumentList.add(type);
		_arguments.put(symbol, type);
	}
	
	public void addVariable(Symbol symbol, Type type){
		if( symbol.isMapped(_variables.keySet()) ){
			_errors.add(new E(ErrorTypes.VARIABLE_ALREADY_DEFINED.mss(), type.getLine()));
		}
		_variables.put(symbol, type);
	}
	
	public Type searchByName(String name){
		for(Symbol s : _variables.keySet()){
			if(s.getId().equals(name))
				return _variables.get(s);
		}
		for(Symbol s : _arguments.keySet()){
			if(s.getId().equals(name))
				return _arguments.get(s);
		}
		return null;
	}
	
	public Type getVarType(String name){
		for(Symbol s : _variables.keySet()){
			if(s.getId().equals(name))
				return _variables.get(s);
		}
		return null;
	}
	
	public Symbol getMethodName() {
		return _methodName;
	}
	public void setMethodName(Symbol methodName) {
		this._methodName = methodName;
	}
	public Type getReturnType() {
		return _returnType;
	}
	public void setReturnType(Type returnType) {
		this._returnType = returnType;
	}
	public Map<Symbol, Type> getArguments() {
		return _arguments;
	}
	public void setArguments(Map<Symbol, Type> arguments) {
		this._arguments = arguments;
	}
	public Map<Symbol, Type> getVariables() {
		return _variables;
	}
	public void setVariables(Map<Symbol, Type> variables) {
		this._variables = variables;
	}
	public Symbol getClase() {
		return _clase;
	}
	public void setClase(Symbol clase) {
		this._clase = clase;
	}
	
	public List<E> getErrors(){
		return _errors;
	}

	public int getLine() {
		return _line;
	}

	public void setLine(int line) {
		this._line = line;
	}

	public List<Type> getArgumentList() {
		return _argumentList;
	}

	public void setArgumentList(List<Type> argumentList) {
		this._argumentList = argumentList;
	}
}
