package semant;

public abstract class Exp {
  abstract tree.Exp unEx();
  abstract tree.Stm unNx();
  abstract tree.Stm unCx(temp.Label t, temp.Label f);
}
