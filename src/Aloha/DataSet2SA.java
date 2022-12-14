package Aloha;

import java.util.ArrayList;

public class DataSet2SA {
    ArrayList<Double> trough_put;
    ArrayList<Integer> TagNums;

    public DataSet2SA(ArrayList<Double> trough_put, ArrayList<Integer> TagNums) {
        this.trough_put = trough_put;
        this.TagNums = TagNums;
    }

    public ArrayList<Double> getTrough_put() {
        return trough_put;
    }

    public void setTrough_put(ArrayList<Double> trough_put) {
        this.trough_put = trough_put;
    }

    public ArrayList<Integer> getTagNums() {
        return TagNums;
    }

    public void setTagNums(ArrayList<Integer> tagNums) {
        this.TagNums = tagNums;
    }
}
