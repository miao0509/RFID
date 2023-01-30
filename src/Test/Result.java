package Test;

public class Result {
    public int success = 0;  //成功识别的时隙数
    public int time = 0;    //花费的时隙数
    public long traffic = 0;//传输的比特数量
    public double efficiency = 0.0;//传输效率
    public int idle = 0;// 空闲时隙

    public int collision = 0;//碰撞时隙

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", time=" + time +
                ", traffic=" + traffic +
                ", efficiency=" + efficiency +
                ", idle=" + idle +
                ", collision=" + collision +
                '}';
    }
}
