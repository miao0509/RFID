package Aloha;

import java.util.ArrayList;

public class DataSet2SA {
    ArrayList<Double> trough_put;
    ArrayList<Integer> TagNums;
    ArrayList<Long> traffic;
    ArrayList<Long> takeTime;

    public DataSet2SA(ArrayList<Double> trough_put, ArrayList<Integer> TagNums,ArrayList<Long> traffic,ArrayList<Long> takeTime) {
        this.trough_put = trough_put;
        this.TagNums = TagNums;
        this.traffic = traffic;
        this.takeTime = takeTime;
    }

    public ArrayList<Long> getTraffic() {
        return traffic;
    }

    public void setTraffic(ArrayList<Long> traffic) {
        this.traffic = traffic;
    }

    public ArrayList<Long> getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(ArrayList<Long> takeTime) {
        this.takeTime = takeTime;
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
