package SAT;

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
    public static int dataSize = 14;  // 数据集大小
    public static int tagSize = 6;  // 标签长度
    public static int frameSize = 8; // 时隙数
    public static int CT = 1; // 每个值下做多少次实验
    public static String name = " 帧长256"; // 每个值下做多少次实验

    public static void main(String[] args) {

        test1();

    }
    public static void test1(){
        long start1 = System.currentTimeMillis();
        Map<Integer, DataSet2SA> map= new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0 ; // 图有多少个横坐标
        while (frameSize <=8) {  // 一帧大小
            ArrayList<Double> trough_put = new ArrayList<>();
            ArrayList<Long> traffic = new ArrayList<>();
            ArrayList<Long> takeTime = new ArrayList<>();
            ArrayList<Integer> tagNums = new ArrayList<>();
            ArrayList<Integer> times = new ArrayList<>();
            DataSet2SA dataSet2SA_put = new DataSet2SA(trough_put, tagNums,traffic,takeTime);
            while (dataSize <= 14) {
                int i = 0;
                long start = System.currentTimeMillis();
                Result sat = new Result();
                while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                    List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
                    Result satResult = SAT(dataSize, tagSize, frameSize, tags);
                    sat.efficiency += satResult.efficiency;
                    sat.traffic +=satResult.traffic;
                    sat.time +=satResult.time;
                }
                trough_put.add(sat.efficiency/CT);
                traffic.add((sat.traffic/CT)+8*256); //  SA寻找每个时隙的时候 要发送当前时隙数
                times.add(sat.time/CT);
                tagNums.add(dataSize);
                dataSize+=10;
                long end = System.currentTimeMillis();
                takeTime.add((end-start));
            }
            size = tagNums.size();
            map.put(frameSize, dataSet2SA_put);
            frameSize = frameSize *2;
            dataSize = 0;
        }
        long end = System.currentTimeMillis();
        System.out.println("SAT花费的时间：   " +  (end - start1));
        CategoryDataset dataset1 = Utils.createDoubleDataset(map, size,1);
        JFreeChart freeChart1 = Utils.createChart(dataset1, "SAT吞吐量", "标签数", "效率");
        Utils.saveAsFile(freeChart1, Utils.jpgFilePath + "\\SAT吞吐量"+name+".jpg");
        CategoryDataset dataset2 = Utils.createDoubleDataset(map, size,2);
        JFreeChart freeChart2 = Utils.createChart(dataset2, "SAT通信量", "标签数", "通信量");
        Utils.saveAsFile(freeChart2, Utils.jpgFilePath + "\\SAT通信量"+name+".jpg");
        CategoryDataset dataset3 = Utils.createDoubleDataset(map, size,3);
        JFreeChart freeChart3 = Utils.createChart(dataset3, "SAT耗时", "标签数", "耗时");
        Utils.saveAsFile(freeChart3, Utils.jpgFilePath + "\\SAT耗时"+name+".jpg");
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
        signlist.add(tags.prefix+"0"); // 这里只能是 自己去判断公共前缀 否则都是10开头的  你给了“”  那么就进入了死循环
        signlist.add(tags.prefix+"1"); // 这里只能是 自己去判断公共前缀 否则都是10开头的  你给了“”  那么就进入了死循环
        //String commonPrefix = Utils.commonPrefix(tags.getTags());   // 公共前缀
        //signlist.add(commonPrefix);
        while (tags.getTags().size() > 0 && signlist.size() >0) {
            sign = signlist.get(0);
            result.traffic +=sign.length();
            value = seek(sign, tags.getTags(), tagSize);
            result.time++;
            signlist.remove(sign);
            switch (value.getResult()) {
                case 0:// 无响应
                    break;
                case 1:// 识别成功
                    result.success++;
                    break;
                default:
                    signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                    signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    break;
                    /*if (value.getPrefix0().size() == 1 &&value.getPrefix1().size() == 1){
                        result.success +=2; //改进的CT算法 如果只有一位碰撞 成功识别两个标签
                    }else {
                        signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                        signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    }
                    break;*/
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

    public static CTResult seek(String sign, List<Tag> list, int tagSize) {
        CTResult result;
        int count = 0;
        Tag first = new Tag();
        List<Tag> collection0 = new ArrayList<>();//碰撞标签 有共同的前缀（sign+0）
        List<Tag> collection1 = new ArrayList<>();//碰撞标签 有共同的前缀（sign+1）
        for (int i = 0; i < list.size(); i++) {
            if (sign.length() == tagSize ) {
//                System.out.print("发出信号为 " + sign + "  识别成功");
                list.remove(list.get(i));
                return new CTResult(1);
            }
            if (list.get(i).getTag().startsWith(sign)){
                count++;

            }
            if (list.get(i).getTag().startsWith(sign + 0)) {
                collection0.add(list.get(i));
            } else if (list.get(i).getTag().startsWith(sign + 1)) {
                collection1.add(list.get(i));
            }
        }
        if (count == 1){
            list.remove(first);
            return new CTResult(1);
        }

        result = new CTResult(2, collection0, collection1);
//        System.out.print("发出信号为 " + sign + "  冲突个数" + (collection1.size() + collection0.size()));

        return result;
    }

    public static SATProcess SAProcess1(List<Tag> tags) {  // 首先进行SA算法 把所有成功标签移除
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
                satMap.put(next.getKey(),new PreAndTags("",next.getValue()));
            }
        }
        return new SATProcess(satMap,success);

    }
}