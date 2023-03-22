package Tree;

import Aloha.DataSet2SA;
import SAT.Result;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeTest {
    public static int dataSize = 0;  // 数据集大小
    public static int tagSize = 96;  // 标签长度
    public static int CT = 100; // 每个值下做多少次实验
    public static String title = "FourArrays";

    public static void main(String[] args) {
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        ArrayList<Double> trough_put = new ArrayList<>();
        ArrayList<Long> traffic = new ArrayList<>();
        ArrayList<Long> takeTime = new ArrayList<>();
        ArrayList<Integer> tagNums = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();//花费总时隙数
        ArrayList<Integer> IdleTimes = new ArrayList<>();//花费总时隙数
        DataSet2SA dataSet2SA_put = new DataSet2SA(trough_put, tagNums, traffic, takeTime);
        dataSet2SA_put.setIdle(IdleTimes);
        dataSet2SA_put.setTime(times);
        int size = 0; // 图有多少个横坐标
        while (dataSize <= 500) {
            int i = 0;
            long start = System.currentTimeMillis();
            Result AdaptTree = new Result();
            while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                List<String> tags = Utils.createTags(dataSize, tagSize);
                Result result = FourArrays.process(tags);
                AdaptTree.efficiency += result.efficiency;
                AdaptTree.traffic += result.traffic;
                AdaptTree.time += result.time;
                AdaptTree.idle += result.idle;
            }
            trough_put.add(AdaptTree.efficiency / CT);
            traffic.add(AdaptTree.traffic / CT); //  SA寻找每个时隙的时候 要发送当前时隙数
            times.add(AdaptTree.time / CT);
            IdleTimes.add(AdaptTree.idle / CT);
            tagNums.add(dataSize);
            dataSize += 10;
            long end = System.currentTimeMillis();
            takeTime.add((end - start));
        }
        size = tagNums.size();
        map.put(1, dataSet2SA_put);
        CategoryDataset dataset1 = Utils.createDoubleDataset(map, size, 1);
        JFreeChart freeChart1 = Utils.createChart(dataset1, title + "吞吐量", "标签数", "效率");
        Utils.saveAsFile(freeChart1, Utils.jpgFilePath + "\\" + title + "吞吐量吞吐量.jpg");
        CategoryDataset dataset2 = Utils.createDoubleDataset(map, size, 2);
        JFreeChart freeChart2 = Utils.createChart(dataset2, title + "吞吐量通信量", "标签数", "通信量");
        Utils.saveAsFile(freeChart2, Utils.jpgFilePath + "\\" + title + "吞吐量通信量.jpg");
        CategoryDataset dataset3 = Utils.createDoubleDataset(map, size, 3);

    }
}
