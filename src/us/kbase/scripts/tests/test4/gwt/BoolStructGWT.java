package us.kbase.scripts.tests.test4.gwt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class BoolStructGWT implements Serializable {
    private Boolean val1;
    private List<Boolean> val2;
    private LinkedHashMap<String,Boolean> val3;
    private Tuple2GWT<Boolean, Boolean> val4;

    public Boolean getVal1() {
        return val1;
    }

    public void setVal1(Boolean val1) {
        this.val1 = val1;
    }

    public List<Boolean> getVal2() {
        return val2;
    }

    public void setVal2(List<Boolean> val2) {
        this.val2 = val2;
    }

    public LinkedHashMap<String,Boolean> getVal3() {
        return val3;
    }

    public void setVal3(LinkedHashMap<String,Boolean> val3) {
        this.val3 = val3;
    }

    public Tuple2GWT<Boolean, Boolean> getVal4() {
        return val4;
    }

    public void setVal4(Tuple2GWT<Boolean, Boolean> val4) {
        this.val4 = val4;
    }
}
