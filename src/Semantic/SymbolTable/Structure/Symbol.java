package Semantic.SymbolTable.Structure;

import java.util.Set;

public class Symbol {
	
	private String _id;
	private boolean _used = false;
	private int _line;
	private int _hashCode;
	
	public Symbol(String id, int linea){
		_id = id;
		_hashCode = id.hashCode() + linea;
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		this._id = id;
	}
	
	public boolean isMapped(Set<Symbol> set){
		boolean maped = false;
		for( Symbol s : set ){
			if( s.getId().equals(_id) ){
				maped = true;
				break;
			}
		}
		return maped;
	}

	@Override
	public int hashCode() {
		return _hashCode;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    Symbol other = (Symbol) obj;
	    return _hashCode == other.hashCode();
	}

	public int getLine() {
		return _line;
	}

	public void setLine(int line) {
		this._line = line;
	}
	
	public boolean isUsed(){
		return _used;
	}
	
	public void used(){
		_used = true;
	}
}
