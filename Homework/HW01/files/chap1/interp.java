// Skeleton file for the Straight-Line Program Interpreter

class interp {
  
  static int maxargs(Stm s) {
    // CompoundStm
    if(s instanceof CompoundStm) {
        CompoundStm compoundStm = (CompoundStm) s;
        // Math.max(maxargs(compoundStm.stm1), maxargs(compoundStm.stm2));
    }
    // AssignStm
    else if(s instanceof AssignStm) {
        AssignStm assignStm = (AssignStm) s;

        if(assignStm.exp instanceof OpExp) {
        
        }
        else if(assignStm.exp instanceof EseqExp) {

        }
    }
    // PrintStm
    else if(s instanceof PrintStm) {
        PrintStm printStm = (PrintStm) s;
    }

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
    else if(s instanceof AssignStm) {
      AssignStm as = (AssignStm) s;
      
      IntTable iT = interpExp(as.exp, t);

      return update(iT.t, as.id, iT.i);
    }
    else if(s instanceof PrintStm) {
      PrintStm ps = (PrintStm) s;
      return interpAndPrint(ps.expList, t);
    }
    else
      throw new Error("Bad Statement");
  }

  static Table interpAndPrint(ExpList exps, Table t) {
    return null;	// replace this with the actual code needed
  }

  static IntAndTable interpExp(Exp e, Table t) {
    if(e instanceof IdExp) {
        //
    }
    else if(e instanceof NumExp) {
        //
    }
    else if(e instanceof OpExp) {
      OpExp op = (OpExp) e;

      switch(op.oper) {
          case 1:
            
              break;
          case 2:

              break;
          case 3:

              break;
          case 4:

              break;
          default: 

      }
    }
    else if(e instanceof EseqExp) {
        
    }
    return null;	// replace this with the actual code needed
  }

  public static void main(String args[]) {
    System.out.println("maxargs result: " + maxargs(prog.prog));
    System.out.print("interpretation result: ");
    interp(prog.prog);
  }
}

