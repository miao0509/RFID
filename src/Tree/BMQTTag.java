package Tree;

public class BMQTTag {
    String ID ;
    int and = -1;
    int or =-1;
    int detect = -1;
    String HMWeight;

    public int getDetect() {
        return detect;
    }

    public void setDetect(int detect) {
        this.detect = detect;
    }

    public BMQTTag(String ID, int and, String HMWeight) {
        this.ID = ID;
        this.and = and;
        this.HMWeight = HMWeight;
    }

    public BMQTTag(String ID, int and, int or, String HMWeight) {
        this.ID = ID;
        this.and = and;
        this.or = or;
        this.HMWeight = HMWeight;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getAnd() {
        return and;
    }

    public void setAnd(int and) {
        this.and = and;
    }

    public int getOr() {
        return or;
    }

    public void setOr(int or) {
        this.or = or;
    }

    public String getHMWeight() {
        return HMWeight;
    }

    public void setHMWeight(String HMWeight) {
        this.HMWeight = HMWeight;
    }
}
