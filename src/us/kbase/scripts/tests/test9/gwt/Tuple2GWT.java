package us.kbase.scripts.tests.test9.gwt;

import java.io.Serializable;

public class Tuple2GWT <T1, T2> implements Serializable {
    private T1 e1;
    private T2 e2;

    public T1 getE1() {
        return e1;
    }

    public void setE1(T1 e1) {
        this.e1 = e1;
    }

    public T2 getE2() {
        return e2;
    }

    public void setE2(T2 e2) {
        this.e2 = e2;
    }
}
