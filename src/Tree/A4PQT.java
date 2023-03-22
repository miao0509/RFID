package Tree;

import Aloha.DataSet2SA;
import SAT.Result;
import Utils.TreeUtil;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ArrayList<Integer> idle = new ArrayList<>();

        DataSet2SA dataSet2SA_put = new DataSet2SA(trough_put, tagNums, traffic, takeTime);
        int size = 0; // 图有多少个横坐标
        while (dataSize <= 500) {
            int i = 0;
            long start = System.currentTimeMillis();
            Result A4PQT_NEW = new Result();
            while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                List<String> tags = Utils.createTags(dataSize, tagSize);
                ; // 每次创建不同的标签值（标签数量  标签长度）
                Result result =process(tags);
                A4PQT_NEW.efficiency += result.efficiency;
                A4PQT_NEW.traffic += result.traffic;
                A4PQT_NEW.time += result.time;
                A4PQT_NEW.idle += result.idle;
            }
            trough_put.add(A4PQT_NEW.efficiency / CT);
            traffic.add(A4PQT_NEW.traffic / CT); //  SA寻找每个时隙的时候 要发送当前时隙数
            times.add(A4PQT_NEW.time / CT);
            tagNums.add(dataSize);
            idle.add(A4PQT_NEW.idle/CT);
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
        List<String> response = new ArrayList<>();
        Result result = new Result();
        signlist.add(sign);
        //System.out.println(list);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            result.traffic += sign.length();
            response = seek(sign, list);
            result.time++;
            signlist.remove(sign);
            if (response.size() == 0) {
                result.idle++;
                continue;
            }
            if (response.size() == 1) {
                result.success++;
                list.remove(response.get(0));
                continue;
            }
            result.collision++;
            // 每次识别两位
            String commonPrefix = TreeUtil.getMergedString(response);
            int xpos = find(commonPrefix);
            int x = 1;
            if (xpos != commonPrefix.length() - 1) {
                x = commonPrefix.charAt(xpos + 1) == 'X' ? 2 : 1;
            }
            int end = xpos == commonPrefix.length() - 1 ? xpos + 1 : xpos + 2;
            char[] chars = commonPrefix.toCharArray();
            if (x == 1){
                chars[xpos] = '0';
                signlist.add( String.valueOf(chars).substring(0,end));
                chars[xpos] = '1';
                signlist.add( String.valueOf(chars).substring(0,end));
            }else {
                chars[xpos] = '1';chars[xpos+1] = '1';
                signlist.add( String.valueOf(chars).substring(0,end));
                chars[xpos] = '0';chars[xpos+1] = '1';
                signlist.add( String.valueOf(chars).substring(0,end));
                chars[xpos] = '1';chars[xpos+1] = '0';
                signlist.add( String.valueOf(chars).substring(0,end));
                chars[xpos] = '0';chars[xpos+1] = '0';
                signlist.add( String.valueOf(chars).substring(0,end));
            }
        }
        System.out.println("识别次数为" + result.time + "次，标签总数为：" + result.success + "  识别完成！");
        result.efficiency = (double) result.success / result.time;
        return result;
    }
    public static List<String> seek(String sign, List<String> list) {
        List<String> response = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String tag = list.get(i);
            if (tag.startsWith(sign)) {
                response.add(tag);
            }
        }
        return response;
    }
    public static int find(String commonPrefix) {
        int count = 0;
        for (int i = 0; i < commonPrefix.length(); i++) {
            if (commonPrefix.charAt(i) == 'X') {
                count = i;
                break;
            }
        }
        return count;

    }
}
