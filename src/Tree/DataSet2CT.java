package Tree;

import java.util.ArrayList;
import java.util.List;

public class DataSet2CT {
    List<Integer> times = new ArrayList<>();
    List<Integer> tagNums = new ArrayList<>();

    public DataSet2CT() {
    }

    public DataSet2CT(List<Integer> times, List<Integer> tagNums) {
        this.times = times;
        this.tagNums = tagNums;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public List<Integer> getTagNums() {
        return tagNums;
    }

    public void setTagNums(List<Integer> tagNums) {
        this.tagNums = tagNums;
    }
}
