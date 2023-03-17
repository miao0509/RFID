package Tree;

public class BMQTTag {
    String ID ;
    int and;
    int HMWeight;

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

    public int getHMWeight() {
        return HMWeight;
    }

    public void setHMWeight(int HMWeight) {
        this.HMWeight = HMWeight;
    }

    public BMQTTag(String ID, int and, int HMWeight) {
        this.ID = ID;
        this.and = and;
        this.HMWeight = HMWeight;
    }
}
