package Aloha;

import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

public class Pure {
    public static int dataSize = 1;  // 数据集大小
    public static int tagSize = 48;  // 标签长度
    public static int frameSize = 64; // 时隙数
    public static int CT = 10; // 每个值下做多少次实验

    public static void main(String[] args) {
        Map<Integer, DataSet> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        while (frameSize <=256) {  // 一帧大小
            ArrayList<Double> trough_put = new ArrayList<>();
            ArrayList<Integer> tagNos = new ArrayList<>();
            DataSet dataSet = new DataSet(trough_put, tagNos);
            while (dataSize <= 500) {
                int i = 0;
                double throughput = 0;
                List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
                while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                    generateRandom(tags, frameSize);  //给标签写入随机数 用来在不同时隙响应
                    throughput += throughput(tags, frameSize);  //统计吞吐量
                }
                trough_put.add(throughput/CT);
                tagNos.add(dataSize);
                dataSize+=10;
            }
            map.put(frameSize, dataSet);
            frameSize = frameSize *2;
            dataSize = 1;
        }
        CategoryDataset dataset = Utils.createDoubleDataset(map, 50);
        JFreeChart freeChart = Utils.createChart(dataset, "纯ALOHA仿真", "标签数", "效率");
        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\pureAloha1.jpg");

    }

    /**
     * 产生随机数写入标签
     *
     * @param tags      标签数目
     * @param frameSize 帧大小
     * @return 写入随机数后的标签 按从小到大时隙排序
     */
    public static List<Tag> generateRandom(List<Tag> tags, int frameSize) {
        for (Tag tag : tags) {
            tag.setNum((int) (Math.ceil(Math.random() * (frameSize -1))));
        }
        tags.sort(Comparator.comparingInt(Tag::getNum));
        return tags;
    }

    public static double throughput(List<Tag> tags, int frameSize) {
        double success = 1;
        Map<Integer, List<Tag>> map = tags.stream().collect(Collectors.groupingBy(Tag::getNum));
//        for (int i = 1; i < tags.size(); i++) {
//            if (tags.get(i).getNum() - tags.get(i - 1).getNum() != 0) {
//                success++;
//            }
//        }
        for (List<Tag> tagInSlot : map.values()) {
            if (tagInSlot.size() == 1){   //如果时隙里只有一个标签 响应成功
                success++;
            }
        }
        return success / frameSize;
    }

}
