package Test;

import Aloha.CreateTag;
import Aloha.DataSet2SA;
import Aloha.Tag;
import Tree.*;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

public class SAT {
    public static int dataSize = 1;  // 数据集大小
    public static int tagSize = 96;  // 标签长度
    public static int frameSize = 256; // 时隙数
    public static int CT = 1000; // 每个值下做多少次实验
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        test1();
        long end = System.currentTimeMillis();
        System.out.println("SAT花费的时间：   " +  (end - start)/100);
    }
    public static void test1(){
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0 ; // 图有多少个横坐标
//        while (frameSize <=256) {  // 一帧大小
            ArrayList<Double> trough_put = new ArrayList<>();
            ArrayList<Integer> tagNums = new ArrayList<>();
            DataSet2SA dataSet2SA = new DataSet2SA(trough_put, tagNums);
            while (dataSize <= 500) {
                int i = 0;
                List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
                Result sat = new Result();
                while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                    sat.efficiency = SAT(dataSize, tagSize, frameSize, tags).efficiency;
                }
                trough_put.add(sat.efficiency/CT);
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
//            dataSize = 1;
//        }
//        CategoryDataset dataset = Utils.createDoubleDataset(map, size);
//
//        JFreeChart freeChart = Utils.createChart(dataset, "SAT", "标签数", "效率");
//        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\SAT1.jpg");
    }

    public static Result SAT(int dataSize, int tagSize, int frameSize, List<Tag> tags ) {
//        List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
        generateRandom(tags, frameSize);  //给标签写入随机数 用来在不同时隙响应
        SATProcess satProcess = SAProcess(tags);//  SA处理后的标签集
        Result result = new Result();
        result.time  = frameSize;
        result.success = satProcess.success;
        for (Map.Entry<Integer, PreAndTags> entry : satProcess.getMap().entrySet()) { // 遍历每个时隙的碰撞标签
            CTProcess(entry.getValue(), tagSize, result);
        }
        result.efficiency = (double) result.success  / result.time;
        return result;
    }

    public static Result CTProcess(PreAndTags tags, int tagSize, Result result) {
        List<String> signlist = new ArrayList<String>();  // 前缀栈
        String sign = "";// 二进制前缀
        CTResult value;//  返回值
        signlist.add(tags.prefix);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            value = ct.seek(sign, tags.getTags(), tagSize);
            result.time++;
            signlist.remove(sign);
            switch (value.getResult()) {
                case 0:// 无响应
                    break;
                case 1:// 识别成功
                    result.success++;
                    break;
                default:/*
                    signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                    signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    break;*/
                    if (value.getPrefix0().size() == 1){
                        result.success +=2; //改进的CT算法 如果只有一位碰撞 成功识别两个标签
                    }else {
                        signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                        signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    }
                    break;
            }
//            System.out.println("    当前成功识别总数为：" + satResult.success);
        }
        return result;
    }

    public static void generateRandom(List<Tag> tags, int frameSize) {
        for (Tag tag : tags) {
            tag.setNum((int) (Math.ceil(Math.random() * (frameSize - 1))));
        }
        tags.sort(Comparator.comparingInt(Tag::getNum));
    }

    public static SATProcess SAProcess(List<Tag> tags) {  // 首先进行SA算法 把所有成功标签移除
        int success = 0;
        Map<Integer, List<Tag>> map = tags.stream().collect(Collectors.groupingBy(Tag::getNum)); // 标签按时隙分组
        Iterator<Map.Entry<Integer, List<Tag>>> iterator = map.entrySet().iterator();
        Map<Integer, PreAndTags> satMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Tag>> next = iterator.next();
            if (next.getValue().size() == 1) {
                success++;
                iterator.remove();
            }else {
                String prefix = Utils.commonPrefix(next.getValue());
                satMap.put(next.getKey(),new PreAndTags(prefix,next.getValue()));
            }
        }
        return new SATProcess(satMap,success);

    }

}
