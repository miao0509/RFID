package Test;

import Aloha.CreateTag;
import Aloha.DataSet2SA;
import Aloha.SA;
import Aloha.Tag;
import Tree.CTResult;
import Tree.SAProcess;
import Tree.ct;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

public class SAT {
    public static int dataSize = 1;  // 数据集大小
    public static int tagSize = 48;  // 标签长度
    public static int frameSize = 64; // 时隙数
    public static int CT = 100; // 每个值下做多少次实验
    public static void main(String[] args) {
        test1();

    }
    public static void test1(){
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0 ;
        while (frameSize <=256) {  // 一帧大小
            ArrayList<Double> trough_put = new ArrayList<>();
            ArrayList<Integer> tagNums = new ArrayList<>();
            DataSet2SA dataSet2SA = new DataSet2SA(trough_put, tagNums);
            while (dataSize <= 500) {
                int i = 0;
                List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
                SATResult sat = new SATResult();
                while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                    int time = 0; //花费时隙数
                    sat.efficiency += SAT(dataSize, tagSize, frameSize, tags).efficiency;
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
            dataSize = 1;
        }
        CategoryDataset dataset = Utils.createDoubleDataset(map, size);

        JFreeChart freeChart = Utils.createChart(dataset, "SAT", "标签数", "效率");
        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\SAT.jpg");
    }

    public static SATResult SAT(int dataSize, int tagSize, int frameSize,List<Tag> tags ) {
//        List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
        generateRandom(tags, frameSize);  //给标签写入随机数 用来在不同时隙响应
        SAProcess saProcess = SAProcess(tags);//  SA处理后的标签集
        SATResult satResult = new SATResult();
        satResult.time  = frameSize;
        satResult.success = saProcess.success;
        for (Map.Entry<Integer, List<Tag>> entry : saProcess.getMap().entrySet()) { // 遍历每个时隙的碰撞标签
            CTProcess(entry.getValue(), tagSize,satResult);
        }
        satResult.efficiency = (double) satResult.success  / satResult.time;
        return satResult;
    }

    public static SATResult CTProcess(List<Tag> tags, int tagSize,SATResult satResult) {
        List<String> signlist = new ArrayList<String>();  // 前缀栈
        String sign = "";// 二进制前缀
        CTResult value;//  返回值
        String commonPrefix = Utils.commonPrefix(tags);   // 公共前缀
        signlist.add(commonPrefix);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            value = seek(sign, tags, tagSize);
            satResult.time++;
            signlist.remove(sign);
            switch (value.getResult()) {
                case 0:// 无响应
                    break;
                case 1:// 识别成功
                    satResult.success++;
                    break;
                default:
                    signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                    signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    break;
            }
//            System.out.println("    当前成功识别总数为：" + satResult.success);
        }
        return satResult;
    }

    public static CTResult seek(String sign, List<Tag> list, int tagSize) {
        CTResult result;
        List<Tag> collection0 = new ArrayList<>();//碰撞标签 有共同的前缀（sign+0）
        List<Tag> collection1 = new ArrayList<>();//碰撞标签 有共同的前缀（sign+1）
        for (int i = 0; i < list.size(); i++) {
            if (sign.length() == tagSize) {
//                System.out.print("发出信号为 " + sign + "  识别成功");
                return new CTResult(1);
            }
            if (list.get(i).getTag().startsWith(sign + 0)) {
                collection0.add(list.get(i));
            } else if (list.get(i).getTag().startsWith(sign + 1)) {
                collection1.add(list.get(i));
            }
        }

        result = new CTResult(2, collection0, collection1);
//        System.out.print("发出信号为 " + sign + "  冲突个数" + (collection1.size() + collection0.size()));

        return result;
    }

    public static void generateRandom(List<Tag> tags, int frameSize) {
        for (Tag tag : tags) {
            tag.setNum((int) (Math.ceil(Math.random() * (frameSize - 1))));
        }
        tags.sort(Comparator.comparingInt(Tag::getNum));
    }

    public static SAProcess SAProcess(List<Tag> tags) {  // 首先进行SA算法 把所有成功标签移除
        int success = 0;
        Map<Integer, List<Tag>> map = tags.stream().collect(Collectors.groupingBy(Tag::getNum)); // 标签按时隙分组
        Iterator<Map.Entry<Integer, List<Tag>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Tag>> next = iterator.next();
            if (next.getValue().size() == 1) {
                success++;
                iterator.remove();
            }
        }
        return new SAProcess(map,success);

    }

}
