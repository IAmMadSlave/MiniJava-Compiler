// Skeleton file for the Straight-Line Program Interpreter

class interp {
  
  static int maxargs(Stm s) {
    if(s instanceof CompoundStm) {
      CompoundStm cs = (CompoundStm) s;

      return Math.max(maxargs(cs.stm1), maxargs(cs.stm2));
    }
    else if(s instanceof AssignStm) {
      AssignStm as = (AssignStm) s;

      return maxargs(as.exp);
    }
    else if(s instanceof PrintStm) {
      PrintStm ps = (PrintStm) s;

      if(ps.exps instanceof PairExpList) {
        PairExpList pel = (PairExpList) ps.exps;

        PrintStm ps1 = new PrintStm(pel.tail);

        return Math.max(Math.max(1 + maxargs(pel.tail), maxargs(pel.head)), maxargs(ps1));
      }
      else if(ps.exps instanceof LastExpList) {
        LastExpList lel = (LastExpList) ps.exps;
            
        return Math.max(1, maxargs(lel.head));
      }
    }

    return -999999999;
  }

  static int maxargs(Exp e) {
    if(e instanceof IdExp) {
      return 0;
    }
    else if(e instanceof NumExp) {
      return 0;
    }
    else if(e instanceof OpExp) {
      OpExp oe = (OpExp) e;

      return Math.max(maxargs(oe.left), maxargs(oe.right));
    }
    else if(e instanceof EseqExp) {
      EseqExp ee = (EseqExp) e;

      return Math.max(maxargs(ee.exp), maxargs(ee.stm));
    }

    return -999999999;
  }

  static int maxargs(ExpList l) {
    if(l instanceof PairExpList) {
      return 1 + maxargs(((PairExpList)l).tail);
    }
    else if(l instanceof LastExpList) {
      return 1;
    }

    return -999999999;
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
      
      IntAndTable iT = interpExp(as.exp, t);

      return update(iT.t, as.id, iT.i);
    }
    else if(s instanceof PrintStm) {
      PrintStm ps = (PrintStm) s;

      return interpAndPrint(ps.exps, t);
    }
    else
      throw new Error("Bad Statement");
  }

  static Table interpAndPrint(ExpList exps, Table t) {
    if (exps instanceof PairExpList) {
      PairExpList pel = (PairExpList) exps;

      IntAndTable iT = interpExp(pel.head,t); 
      
      System.out.print(iT.i + " ");

      return interpAndPrint(pel.tail, iT.t);
    }
    else if (exps instanceof LastExpList) {
      LastExpList lel = (LastExpList) exps;

      IntAndTable iT = interpExp(lel.head, t);

      System.out.println(iT.i);

      return iT.t;
    }
    else {
      throw new Error("Bad Print");
    }
  }

  static IntAndTable interpExp(Exp e, Table t) {
    if(e instanceof IdExp) {
      IdExp ie = (IdExp) e;

      return new IntAndTable(lookup(t, ie.id), t);        
    }
    else if(e instanceof NumExp) {
      NumExp ne = (NumExp) e;

      return new IntAndTable(ne.num, t);
    }
    else if(e instanceof OpExp) {
      OpExp op = (OpExp) e;

      IntAndTable leftResultTable = interpExp(op.left, t);

      IntAndTable rightResultTable = interpExp(op.right, leftResultTable.t);

      switch(op.oper) {

          case 1:
            return new IntAndTable(leftResultTable.i + rightResultTable.i, rightResultTable.t);

          case 2:
            return new IntAndTable(leftResultTable.i - rightResultTable.i, rightResultTable.t);

          case 3:
            return new IntAndTable(leftResultTable.i * rightResultTable.i, rightResultTable.t);

          case 4:
            return new IntAndTable(leftResultTable.i / rightResultTable.i, rightResultTable.t);

          default:
            throw new Error("Bad Operator");
      }
    }
    else if(e instanceof EseqExp) {
      EseqExp ee = (EseqExp) e;
      
      return interpExp(ee.exp, interpStm(ee.stm, t));
    }
    else
      throw new Error("Bad Expression");
  }

  public static void main(String args[]) {
    System.out.println("maxargs result: " + maxargs(prog.prog));
    System.out.print("interpretation result: ");
    interp(prog.prog);
  }
}

