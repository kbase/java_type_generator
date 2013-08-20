package us.kbase.scripts.tests.test4.gwt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class ObjectStructGWT implements Serializable {
    private Object val1;
    private List<Object> val2;
    private LinkedHashMap<String,Object> val3;
    private Tuple2GWT<Object, Object> val4;

    public Object getVal1() {
        return val1;
    }

    public void setVal1(Object val1) {
        this.val1 = val1;
    }

    public List<Object> getVal2() {
        return val2;
    }

    public void setVal2(List<Object> val2) {
        this.val2 = val2;
    }

    public LinkedHashMap<String,Object> getVal3() {
        return val3;
    }

    public void setVal3(LinkedHashMap<String,Object> val3) {
        this.val3 = val3;
    }

    public Tuple2GWT<Object, Object> getVal4() {
        return val4;
    }

    public void setVal4(Tuple2GWT<Object, Object> val4) {
        this.val4 = val4;
    }
}
