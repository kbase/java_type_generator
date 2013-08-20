package us.kbase.scripts.tests.test1.gwt;

import java.io.Serializable;

public class SimpleStructGWT implements Serializable {
    private Integer prop1;
    private Double prop2;
    private String prop3;

    public Integer getProp1() {
        return prop1;
    }

    public void setProp1(Integer prop1) {
        this.prop1 = prop1;
    }

    public Double getProp2() {
        return prop2;
    }

    public void setProp2(Double prop2) {
        this.prop2 = prop2;
    }

    public String getProp3() {
        return prop3;
    }

    public void setProp3(String prop3) {
        this.prop3 = prop3;
    }
}
