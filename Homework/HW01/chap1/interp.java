// Skeleton file for the Straight-Line Program Interpreter

class interp {
  
  static int maxargs(Stm s) {
    return 0;	 	// replace this with the actual code needed
  }

  static void interp(Stm s) {
    // interpret s with respect to an empty Table
    interpStm(s, null);
  }

  static class Table {
    final String id; final int value; final Table tail;
    Table(String i, int v, Table t) {id=i; value=v; tail=t;}
  }

  // Returns the value of key in Table t.
  static int lookup(Table t, String key) {
    if (t == null)
      throw new Error("unknown identifier: " + key);
    else if (t.id.equals(key))
      return t.value;
    else
      return lookup(t.tail, key);
  }

  // Returns a new Table that is the same as t except that id has value val.
  static Table update(Table t, String id, int val) {
    return new Table(id, val, t);
  }
    
  static class IntAndTable {
    final int i; final Table t;
    IntAndTable(int ii, Table tt) {i=ii; t=tt;}
  }

  static Table interpStm(Stm s, Table t) {
    if (s instanceof CompoundStm) {
      CompoundStm cs = (CompoundStm) s;
      return interpStm(cs.stm2, interpStm(cs.stm1, t));
    }
    // ...
    else
      throw new Error("Bad Statement");
  }

  static Table interpAndPrint(ExpList exps, Table t) {
    return null;	// replace this with the actual code needed
  }

  static IntAndTable interpExp(Exp e, Table t) {
    return null;	// replace this with the actual code needed
  }

  public static void main(String args[]) {
    System.out.println("maxargs result: " + maxargs(prog.prog));
    System.out.print("interpretation result: ");
    interp(prog.prog);
  }
}

