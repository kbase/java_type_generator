package us.kbase.scripts.tests.test2.gwt;

import java.io.Serializable;

public class BindingSiteGWT implements Serializable {
    private GeneGWT regulator;
    private SequencePosGWT binding_pos;

    public GeneGWT getRegulator() {
        return regulator;
    }

    public void setRegulator(GeneGWT regulator) {
        this.regulator = regulator;
    }

    public SequencePosGWT getBindingPos() {
        return binding_pos;
    }

    public void setBindingPos(SequencePosGWT binding_pos) {
        this.binding_pos = binding_pos;
    }
}
