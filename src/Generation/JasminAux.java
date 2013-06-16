package Generation;

import java.io.*;
import java.util.*;

public class JasminAux {
	
  private static String _className;
  private static List<String> _lines;

  public static void init() {
    _lines = new ArrayList<String>(300);
  }

  public static void setClassName(String name) {
    _className = name;
  }

  public static void newline() {
    _lines.add("");
  }

  public static void newline2() {
    _lines.add("\n");
  }

  public static void debug(String s) {
    code("; " + s);
  }

  public static void directive(String s) {
    _lines.add(s);
  }
  
  public static void label(String l) {
    if (!_lines.get(_lines.size()-1).trim().isEmpty())
      newline();
    
    _lines.add(" " + l + ":");
  }

  public static void code(String c) {
    _lines.add("   " + c);
  }

  public static void save() {
    String filename = _className + ".jasmin";
    
    try {
      FileWriter writer = new FileWriter(filename);

      for (String line : _lines) {
        writer.append(line);
        writer.append('\n');
      }

      writer.close();
    } catch (IOException e) { }
  }
}
