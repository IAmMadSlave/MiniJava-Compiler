class CompoundStm extends Stm {
    final Stm stm1,
              stm2;

    CompoundStm(Stm stm1, Stm stm2) {
        this.stm1 = stm1;
        this.stm2 = stm2;
    }
}
