package Test;

public class SATResult {
    public int success = 0;  //成功识别的时隙数
    public int time = 0;    //花费的时隙数
    public long traffic = 0;//传输的比特数量
    public double efficiency = 0.0;//传输效率

    @Override
    public String toString() {
        return "SATResult{" +
                "success=" + success +
                ", time=" + time +
                ", traffic=" + traffic +
                ", efficiency=" + efficiency +
                '}';
    }
}
