package us.kbase.scripts.tests.test9.gwt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class ComplexStructGWT implements Serializable {
    private List<LinkedHashMap<String,Tuple2GWT<SimpleStructGWT, SimpleStructGWT>>> val1;
    private LinkedHashMap<String,List<Tuple2GWT<SimpleStructGWT, SimpleStructGWT>>> val2;
    private Tuple2GWT<List<SimpleStructGWT>, LinkedHashMap<String,SimpleStructGWT>> val3;

    public List<LinkedHashMap<String,Tuple2GWT<SimpleStructGWT, SimpleStructGWT>>> getVal1() {
        return val1;
    }

    public void setVal1(List<LinkedHashMap<String,Tuple2GWT<SimpleStructGWT, SimpleStructGWT>>> val1) {
        this.val1 = val1;
    }

    public LinkedHashMap<String,List<Tuple2GWT<SimpleStructGWT, SimpleStructGWT>>> getVal2() {
        return val2;
    }

    public void setVal2(LinkedHashMap<String,List<Tuple2GWT<SimpleStructGWT, SimpleStructGWT>>> val2) {
        this.val2 = val2;
    }

    public Tuple2GWT<List<SimpleStructGWT>, LinkedHashMap<String,SimpleStructGWT>> getVal3() {
        return val3;
    }

    public void setVal3(Tuple2GWT<List<SimpleStructGWT>, LinkedHashMap<String,SimpleStructGWT>> val3) {
        this.val3 = val3;
    }
}
