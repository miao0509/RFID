import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Result {
    private int idleNum;
    private int collisionNum;
    private int successNum;
    private List<String> collisionList;

    public Result(int idleNum, int collisionNum, int successNum, List<String> collisionList) {
        this.idleNum = idleNum;
        this.collisionNum = collisionNum;
        this.successNum = successNum;
        this.collisionList = collisionList;
    }

    public Result() {
    }

    public int getIdleNum() {
        return idleNum;
    }

    public void setIdleNum(int idleNum) {
        this.idleNum = idleNum;
    }

    public int getCollisionNum() {
        return collisionNum;
    }

    public void setCollisionNum(int collisionNum) {
        this.collisionNum = collisionNum;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public List<String> getCollisionList() {
        return collisionList;
    }

    public void setCollisionList(List<String> collisionList) {
        this.collisionList = collisionList;
    }

    @Override
    public String toString() {
        return "Result{" +
                "idleNum=" + idleNum +
                ", collisionNum=" + collisionNum +
                ", successNum=" + successNum +
                ", collisionList=" + collisionList +
                '}';
    }

}
