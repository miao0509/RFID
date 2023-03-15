package Tree;

import Aloha.DataSet2SA;
import SAT.Result;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;

public class A4PQT {
    public static int dataSize = 0;  // 数据集大小
    public static int tagSize = 96;  // 标签长度
    public static int CT = 1000; // 每个值下做多少次实验

    public static void main(String[] args) {
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        ArrayList<Double> trough_put = new ArrayList<>();
        ArrayList<Long> traffic = new ArrayList<>();
        ArrayList<Long> takeTime = new ArrayList<>();
        ArrayList<Integer> tagNums = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        DataSet2SA dataSet2SA_put = new DataSet2SA(trough_put, tagNums, traffic, takeTime);
        int size = 0; // 图有多少个横坐标
        while (dataSize <= 500) {
            int i = 0;
            long start = System.currentTimeMillis();
            Result AdaptTree = new Result();
            while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                List<String> tags = Utils.createTags(dataSize, tagSize);
                ; // 每次创建不同的标签值（标签数量  标签长度）
                Result result =BMQT.process(tags);
                AdaptTree.efficiency += result.efficiency;
                AdaptTree.traffic += result.traffic;
                AdaptTree.time += result.time;
            }
            trough_put.add(AdaptTree.efficiency / CT);
            traffic.add(AdaptTree.traffic / CT); //  SA寻找每个时隙的时候 要发送当前时隙数
            times.add(AdaptTree.time / CT);
            tagNums.add(dataSize);
            dataSize += 10;
            long end = System.currentTimeMillis();
            takeTime.add((end - start));
        }
        size = tagNums.size();
        map.put(1, dataSet2SA_put);
        CategoryDataset dataset1 = Utils.createDoubleDataset(map, size, 1);
        JFreeChart freeChart1 = Utils.createChart(dataset1, "AdaptTree吞吐量", "标签数", "效率");
        Utils.saveAsFile(freeChart1, Utils.jpgFilePath + "\\AdaptTree吞吐量吞吐量.jpg");
        CategoryDataset dataset2 = Utils.createDoubleDataset(map, size, 2);
        JFreeChart freeChart2 = Utils.createChart(dataset2, "AdaptTree吞吐量通信量", "标签数", "通信量");
        Utils.saveAsFile(freeChart2, Utils.jpgFilePath + "\\AdaptTree吞吐量通信量.jpg");
        CategoryDataset dataset3 = Utils.createDoubleDataset(map, size, 3);
        JFreeChart freeChart3 = Utils.createChart(dataset3, "AdaptTree吞吐量耗时", "标签数", "耗时");
        Utils.saveAsFile(freeChart3, Utils.jpgFilePath + "\\AdaptTree吞吐量耗时.jpg");
    }

    public static Result process(List<String> list) {
        List<String> signlist = new ArrayList<String>();
        String sign = "";// 二进制前缀
        String[] values = new String[5];// 返回值
        values[0] = "0";
        Result result = new Result();
        signlist.add(sign + "00");
        signlist.add(sign + "01");
        signlist.add(sign + "10");
        signlist.add(sign + "11");
        result.traffic = 8;
        //System.out.println(list);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            result.traffic += sign.length();
            values = seek(sign, list);
            result.time++;
            signlist.remove(sign);
            switch (values[0]) {
                case "0":// 无响应
                    result.idle++;
                    break;
                case "1":// 识别成功
                    result.success++;
                    break;
                case "2":
                    for (int i = 1; i < values.length && values[i] != null; i++) {
                        signlist.add(Math.max(signlist.size() - 1, 0), sign + values[i]);
                    }
                    result.collision++;
                    break;
            }
            //System.out.println("    当前成功识别总数为：" + result.success);
            if (list.size() == 0) {
                break;
            }
        }
        System.out.println("识别次数为" + result.time + "次，标签总数为：" + result.success + "  识别完成！");
        result.efficiency = (double) result.success / result.time;
        return result;
    }


    // 将二进制前缀与列表中的ID进行比较
    public static String[] seek(String sign, List<String> list) {
        Set<String> collisionSet = new HashSet<>();
        // values[0] 放置 count的值， 后面放置冲突的位
        String[] values = new String[5];
        int count = 0;
        int value = 0;
        String first = "";// 记录识别出第一个标签的id;
        for (int i = 0; i < list.size(); i++) {
            if (sign.equals(list.get(i))){
                list.remove(first);
                value = 1;
                values[0] = String.valueOf(value);
                return values;
            }
            if (list.get(i).startsWith(sign)) {
                count++;
                // 统计引起冲突的位
//                if (list.get(i).length() >= sign.length() + 2) { //剩余位数大于1
                collisionSet.add(list.get(i).substring(sign.length(), sign.length() + 2));
//                } else {//剩余位数等于1
//                    collisionSet.add(list.get(i).substring(list.get(i).length() - 1));
//                }
                if (count == 1) {
                    first = list.get(i);
                }
            }
        }

        int len = 1;
        if (count == 0) {
            //System.out.print("发出信号为 " + sign + "  无响应");
        } else if (count == 1) {
            //System.out.print("发出信号为 " + sign + "  识别成功    ");
            list.remove(first);
            value = 1;
        } else {
            value = 2;
            //System.out.print("发出信号为 " + sign + "  冲突个数" + count + "    ");
            int countX0 = 0;
            int countX1 = 0;
            for (String str : collisionSet) { //统计碰撞位能否剪枝
                if (str.charAt(1) == '0') {
                    countX0++;
                } else {
                    countX1++;
                }
            }
            if (countX0 == collisionSet.size()) {
                values[len++] = "00";
                values[len] = "10";
            } else if (countX1 == collisionSet.size()) {
                values[len++] = "01";
                values[len] = "11";
            } else {
                //System.out.print("碰撞位: ");

                    //System.out.print(s + "    ");
                values[len++] = "01";
                values[len++] = "10";
                values[len++] = "11";
                values[len] = "00";

            }
        }
        values[0] = String.valueOf(value);
        return values;
    }


}

