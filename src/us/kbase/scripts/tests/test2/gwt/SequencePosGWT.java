package us.kbase.scripts.tests.test2.gwt;

import java.io.Serializable;

public class SequencePosGWT implements Serializable {
    private Integer start;
    private Integer stop;
    private Integer sequence_id;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getStop() {
        return stop;
    }

    public void setStop(Integer stop) {
        this.stop = stop;
    }

    public Integer getSequenceId() {
        return sequence_id;
    }

    public void setSequenceId(Integer sequence_id) {
        this.sequence_id = sequence_id;
    }
}
