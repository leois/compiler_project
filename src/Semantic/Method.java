package Semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AST.Type;

public class Method{
	private Symbol _methodName;
	private Type _returnType;
	private Map<Symbol, Type> _arguments;
	private Map<Symbol, Type> _variables;
	private Symbol _clase;
	private int _line;
	private List<Error> _errors;
	
	public Method(Symbol method, int line){
		_methodName = method;
		_line = line;
		_arguments = new HashMap<Symbol, Type>();
		_variables = new HashMap<Symbol, Type>();
		_errors = new ArrayList<Error>();
	}
	
	public void addArgument(Symbol symbol, Type type){
		if( symbol.isMapped(_arguments.keySet()) ){
			_errors.add(new Error(Types.ARGUMENT_ALREADY_DEFINED.mss(), type.getLine()));
		}
		_arguments.put(symbol, type);
	}
	
	public void addVariable(Symbol symbol, Type type){
		if( symbol.isMapped(_variables.keySet()) ){
			_errors.add(new Error(Types.VARIABLE_ALREADY_DEFINED.mss(), type.getLine()));
		}
		_variables.put(symbol, type);
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
	
	public List<Error> getErrors(){
		return _errors;
	}

	public int getLine() {
		return _line;
	}

	public void setLine(int line) {
		this._line = line;
	}
}
