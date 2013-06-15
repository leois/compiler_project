package Semantic;

public class E {
	private int _line;
	private String _mss;
	
	public E(String mss, int line){
		_mss = mss;
		_line = line;
	}
	
	public E(ErrorTypes et, int line){
		_mss = et.mss();
		_line = line;
	}
	
	public String mss(){
		return _mss + ". Line: " + _line;
	}
}

