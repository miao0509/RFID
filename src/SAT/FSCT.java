package SAT;

import Aloha.CreateTag;
import Aloha.DataSet2SA;
import Aloha.Tag;
import Tree.CTResult;
import Tree.PreAndTags;
import Tree.SATProcess;
import Utils.*;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

public class FSCT {
    public static int dataSize = 500;  // 数据集大小
    public static int tagSize = 96;  // 标签长度
    public static int frameSize = 256; // 时隙数
    public static int CT = 1000; // 每个值下做多少次实验
    public static String name = " 帧长256"; // 每个值下做多少次实验

    public static void main(String[] args) {

//        test1();
        List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
        Result satResult = SAT(dataSize, tagSize, frameSize, tags);

    }
    public static void test1(){
        long start1 = System.currentTimeMillis();
        Map<Integer, DataSet2SA> map= new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0 ; // 图有多少个横坐标
        while (frameSize <=256) {  // 一帧大小
            ArrayList<Double> trough_put = new ArrayList<>();
            ArrayList<Long> traffic = new ArrayList<>();
            ArrayList<Long> takeTime = new ArrayList<>();
            ArrayList<Integer> tagNums = new ArrayList<>();
            ArrayList<Integer> times = new ArrayList<>();
            DataSet2SA dataSet2SA_put = new DataSet2SA(trough_put, tagNums,traffic,takeTime);
            while (dataSize <= 500) {
                int i = 0;
                long start = System.currentTimeMillis();
                Result sat = new Result();
                while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                    List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
                    Result satResult = SAT(dataSize, tagSize, frameSize, tags);
                    sat.efficiency += satResult.efficiency;
                    sat.traffic +=satResult.traffic;
                    sat.time +=satResult.time;
                    sat.idle += satResult.idle;
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
        result.idle = satProcess.idle;;
        result.success = satProcess.success;
        for (Map.Entry<Integer, PreAndTag> entry : satProcess.getMap().entrySet()) { // 遍历每个时隙的碰撞标签
            CTProcess(entry.getValue(), result);
        }
        result.efficiency = (double) result.success  / result.time;
        return result;
    }

    public static Result CTProcess(PreAndTag tags,  Result result) {
        List<String> list = tags.getTags().stream().map(Tag::getTag).collect(Collectors.toList());
        List<String> signlist = new ArrayList<String>();  // 前缀栈
        String sign = "";// 二进制前缀
        signlist.add(tags.prefix.get(0));
        signlist.add(tags.prefix.get(1));

        while (tags.getTags().size() > 0 && signlist.size() >0) {
            sign = signlist.get(0);
            result.traffic += sign.length();
            List<String> response = seek(sign, list);
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
            String commonPrefix = TreeUtil.getMergedString(response);
            int x = countCollision(commonPrefix);
            int xpos = find(commonPrefix);
            char[] chars = commonPrefix.toCharArray();
            if (x == 1){
                chars[xpos] = '0';
                list.remove(String.valueOf(chars).substring(0, 96));
                chars[xpos] = '1';
                list.remove(String.valueOf(chars).substring(0, 96));
                result.success += 2;
            }else {
                chars[xpos] = '0';
                signlist.add(String.valueOf(chars).substring(0, xpos+1));
                chars[xpos] = '1';
                signlist.add(String.valueOf(chars).substring(0, xpos+1));
            }
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
        Map<Integer, PreAndTag> satMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<Tag>> next = iterator.next();
            if (next.getValue().size() == 1) {
                success++;
                iterator.remove();
            }else {
                String commonPrefix = Utils.commonPrefix(next.getValue());
                List<String> prefix=  new ArrayList<>();
                prefix.add(commonPrefix+"0");
                prefix.add(commonPrefix+"1");
                satMap.put(next.getKey(),new PreAndTag(prefix,next.getValue()));
            }
        }
        SATProcess process = new SATProcess(satMap, success);
        process.idle = 256-map.size();
        return process;

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

    private static int countCollision(String commonPrefix) {
        int count = 0;
        for (int i = 0; i < commonPrefix.length(); i++) {
            if (commonPrefix.charAt(i) == 'X') {
                count++;
            }
        }
        return count;
    }
    public static int find(String commonPrefix) {
        int ans = 0;
        int count = 0;
        for (int i = 0; i < commonPrefix.length(); i++) {
            if (commonPrefix.charAt(i) == 'X') {
                ans =i;
                 break;
            }
        }
        return ans;

    }
    static class SATProcess {
        public Map<Integer,PreAndTag> map;
        public int success;
        public int idle;

        public SATProcess() {
        }

        public SATProcess(Map<Integer, PreAndTag> map, int success) {
            this.map = map;
            this.success = success;
        }

        public Map<Integer, PreAndTag> getMap() {
            return map;
        }

        public void setMap(Map<Integer, PreAndTag> map) {
            this.map = map;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }
    }
    static class PreAndTag {
        public List<String> prefix;
        public List<Tag> tags;

        public PreAndTag() {
        }

        public PreAndTag(List<String> prefix, List<Tag> tags) {
            this.prefix = prefix;
            this.tags = tags;
        }

        public List<String> getPrefix() {
            return prefix;
        }

        public void setPrefix(List<String> prefix) {
            this.prefix = prefix;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }


}
