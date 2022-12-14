package Test;

import Aloha.CreateTag;
import Aloha.DataSet2SA;
import Aloha.SA;
import Aloha.Tag;
import Tree.ct;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;

public class test {
    public static void main(String[] args) {
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0;
        ArrayList<Double> trough_put = new ArrayList<>();
        ArrayList<Integer> tagNums = new ArrayList<>();
        DataSet2SA dataSet2SA = new DataSet2SA(trough_put, tagNums);
        int i = 0;
        double throughput = 0;
        List<Tag> tags = CreateTag.createTags(10, 8); // 每次创建不同的标签值（标签数量  标签长度）
        int success = 0;   //成功识别个数
        int time = 0; //花费时隙数
        SA.generateRandom(tags, 8);  //给标签写入随机数 用来在不同时隙响应
        Map<Integer, List<Tag>> SAMap = SA.SAProcess(tags, success);//  SA处理后的标签集
        for (Map.Entry<Integer, List<Tag>> entry : SAMap.entrySet()) { // 遍历每个时隙的碰撞标签
            time += ct.CTProcess(entry.getValue(), success);
        }
        trough_put.add(throughput );
        tagNums.add(500);
        System.out.println("已识别"  + success  +  "效率"  + success/time);
    }

}
