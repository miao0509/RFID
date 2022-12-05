package Aloha;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    ArrayList<Double> trough_put;
    ArrayList<Integer> tagNos ;

    public DataSet(ArrayList<Double> trough_put, ArrayList<Integer> tagNos) {
        this.trough_put = trough_put;
        this.tagNos = tagNos;
    }

    public ArrayList<Double> getTrough_put() {
        return trough_put;
    }

    public void setTrough_put(ArrayList<Double> trough_put) {
        this.trough_put = trough_put;
    }

    public ArrayList<Integer> getTagNos() {
        return tagNos;
    }

    public void setTagNos(ArrayList<Integer> tagNos) {
        this.tagNos = tagNos;
    }
}
