package semant;

import syntaxtree.*;
import java.util.Vector;

public class TypeCheckVisitor extends visitor.TypeDepthFirstVisitor {
  // By extending TypeDepthFirstVisitor, we only have to override those
  // methods that differ from the generic visitor.

  private errormsg.ErrorMsg errorMsg;
  private SymbolTable classTable;
  private ClassInfo currClass;
  private MethodInfo currMethod;

  // Type constants
  final IntegerType INTTY = new IntegerType();
  final IntArrayType INTARRTY = new IntArrayType();
  final BooleanType BOOLTY = new BooleanType();
 
  public TypeCheckVisitor(errormsg.ErrorMsg e, SymbolTable s){
    errorMsg = e;
    classTable = s;
    currClass = null;
    currMethod = null;
  }

  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) {
    // Mostly you just need to typecheck the body of 'main' here.
    // But as shown in Foo.java, you need care concerning 'this'.
    n.s.accept(this);
    return null;
  }

  // Exp e1,e2;
  public Type visit(Plus n) {
    Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);
    if (!equal(t1, t2, INTTY))
      errorMsg.error(n.pos, eIncompBiop("+", t1.toString(), t2.toString()));
    return INTTY;
  }

  // Check whether t1 == t2 == target, but suppress error messages if
  // either t1 or t2 is null.
  private boolean equal(Type t1, Type t2, Type target) {
    if ( t1 == null || t2 == null )
      return true;

    if (target == null)
      throw new Error("target argument in method equal cannot be null");

    if (target instanceof IdentifierType && t1 instanceof IdentifierType
					 && t2 instanceof IdentifierType)
      return ((IdentifierType) t1).s.equals(((IdentifierType) t2).s );

    if (!(target instanceof IdentifierType) &&
         t1.toString().equals(target.toString()) &&
         t2.toString().equals(target.toString()))
      return true;

    return false;
  }

  // Methods for error reporting:

  private String eIncompTypes(String t1, String t2) {
    return "incompatible types \nfound   : " + t1
			    + "\nrequired: " + t2 ;
  }

  private String eIncompBiop(String op, String t1, String t2) {
    return "operator " + op + " cannot be applied to " + t1 + "," + t2 ;
  }

}
