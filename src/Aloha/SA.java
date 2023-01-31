package Aloha;

import Tree.SAProcess;
import Tree.ct;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

public class SA {
    public static int dataSize = 1;  // 数据集大小
    public static int tagSize = 48;  // 标签长度
    public static int frameSize = 64; // 时隙数
    public static int CT = 1000; // 每个值下做多少次实验


    public static void main(String[] args) {
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0 ;
        while (frameSize <=256) {  // 一帧大小
            ArrayList<Double> trough_put = new ArrayList<>();
            ArrayList<Integer> tagNums = new ArrayList<>();
            DataSet2SA dataSet2SA = new DataSet2SA(trough_put, tagNums,null,null);
            while (dataSize <= 500) {
                int i = 0;
                double throughput = 0;
                List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
                while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                    int time = 0; //花费时隙数
                    generateRandom(tags, frameSize);  //给标签写入随机数 用来在不同时隙响应
                    SAProcess saProcess = SAProcess(tags);//  SA处理后的标签集
                    for ( Map.Entry<Integer, List<Tag>> entry : saProcess.getMap().entrySet()) { // 遍历每个时隙的碰撞标签
                        time+= ct.CTProcess(entry.getValue(), saProcess.getSuccess(),tagSize).time;
                    }

                }

                trough_put.add(throughput/CT);
                tagNums.add(dataSize);
                if (dataSize == 1 ){
                    dataSize+=9;
                }else {
                    dataSize+=10;
                }
            }
            size = tagNums.size();
            map.put(frameSize, dataSet2SA);
            frameSize = frameSize *2;
            dataSize = 1;
        }
        CategoryDataset dataset = Utils.createDoubleDataset(map, size,1);

        JFreeChart freeChart = Utils.createChart(dataset, "时隙ALOHA仿真", "标签数", "效率");
        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\pureAloha1.jpg");

    }

    /**
     * 产生随机数写入标签
     *
     * @param tags      标签数目
     * @param frameSize 帧大小
     * */
    public static void generateRandom(List<Tag> tags, int frameSize) {
        for (Tag tag : tags) {
            tag.setNum((int) (Math.ceil(Math.random() * (frameSize -1))));
        }
        tags.sort(Comparator.comparingInt(Tag::getNum));
    }

    public static SAProcess SAProcess(List<Tag> tags) {  // 首先进行SA算法 把所有成功标签移除
        int success = 0;
        Map<Integer, List<Tag>> map = tags.stream().collect(Collectors.groupingBy(Tag::getNum)); // 标签按时隙分组
        Iterator<Map.Entry<Integer, List<Tag>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, List<Tag>> next = iterator.next();
            if (next.getValue().size() == 1){
                success++;
                iterator.remove();
            }
        }
        return new SAProcess(map,success);
    }


}
