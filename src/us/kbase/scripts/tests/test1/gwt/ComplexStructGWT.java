package us.kbase.scripts.tests.test1.gwt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class ComplexStructGWT implements Serializable {
    private List<SimpleStructGWT> large_prop1;
    private LinkedHashMap<String,SimpleStructGWT> large_prop2;
    private List<LinkedHashMap<String,List<SimpleStructGWT>>> large_prop3;
    private LinkedHashMap<String,List<LinkedHashMap<String,SimpleStructGWT>>> large_prop4;

    public List<SimpleStructGWT> getLargeProp1() {
        return large_prop1;
    }

    public void setLargeProp1(List<SimpleStructGWT> large_prop1) {
        this.large_prop1 = large_prop1;
    }

    public LinkedHashMap<String,SimpleStructGWT> getLargeProp2() {
        return large_prop2;
    }

    public void setLargeProp2(LinkedHashMap<String,SimpleStructGWT> large_prop2) {
        this.large_prop2 = large_prop2;
    }

    public List<LinkedHashMap<String,List<SimpleStructGWT>>> getLargeProp3() {
        return large_prop3;
    }

    public void setLargeProp3(List<LinkedHashMap<String,List<SimpleStructGWT>>> large_prop3) {
        this.large_prop3 = large_prop3;
    }

    public LinkedHashMap<String,List<LinkedHashMap<String,SimpleStructGWT>>> getLargeProp4() {
        return large_prop4;
    }

    public void setLargeProp4(LinkedHashMap<String,List<LinkedHashMap<String,SimpleStructGWT>>> large_prop4) {
        this.large_prop4 = large_prop4;
    }
}
