class AssignStm extends Stm {
    final String id;
    final Exp exp;

    AssignStm(String id, Exp exp) {
        this.id  = id;
        this.exp = exp;
    }
}
