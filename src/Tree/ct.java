package Tree;

import Aloha.CreateTag;
import Aloha.DataSet2SA;
import Aloha.Tag;
import Test.Result;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;

public class ct {
    public static Map<String, List<Tag>> CT = new HashMap<>(); // 碰撞树  key->前缀    val->前缀下的子树
    public static int dataSize = 1;  // 数据集大小
    public static Integer tagSize = 96;
    public static int frameSize = 64; // 时隙数
    public static int CTCount = 1000; // 每个值下做多少次实验
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        testThough_put();
        long end = System.currentTimeMillis();
        System.out.println("花费的时间：   " +  (end - start)/100);
    }

    public static void testThough_put() {
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        int size = 0;
        ArrayList<Double> trough_put = new ArrayList<>();
        ArrayList<Long> traffic = new ArrayList<>();
        ArrayList<Long> takeTime = new ArrayList<>();
        ArrayList<Integer> tagNums = new ArrayList<>();
        DataSet2SA dataSet2SA = new DataSet2SA(trough_put, tagNums,traffic,takeTime);
        while (dataSize <= 500) {
            long start = System.currentTimeMillis();
            int i = 0;
            List<Tag> tags = CreateTag.createTags(dataSize, tagSize); // 每次创建不同的标签值（标签数量  标签长度）
            Result result = new Result();
            while (i++ < CTCount) { // 每个对应帧数下做CT次实验 取平均值
                result.efficiency += CTProcess(tags, 0, tagSize).efficiency;
                result.traffic +=CTProcess(tags, 0, tagSize).traffic;
            }
            trough_put.add(result.efficiency / CTCount);
            traffic.add(result.traffic/CTCount);
            tagNums.add(dataSize);
            if (dataSize == 1) {
                dataSize += 9;
            } else {
                dataSize += 10;
            }
            long end = System.currentTimeMillis();
            takeTime.add((end-start));
        }
        size = tagNums.size();
        map.put(frameSize, dataSet2SA);

        CategoryDataset dataset1 = Utils.createDoubleDataset(map, size,1);
        JFreeChart freeChart1 = Utils.createChart(dataset1, "CT吞吐量", "标签数", "效率");
        Utils.saveAsFile(freeChart1, Utils.jpgFilePath + "\\CT吞吐量.jpg");
        CategoryDataset dataset2 = Utils.createDoubleDataset(map, size,2);
        JFreeChart freeChart2 = Utils.createChart(dataset2, "CT通信量", "标签数", "通信量");
        Utils.saveAsFile(freeChart2, Utils.jpgFilePath + "\\CT通信量.jpg");
        CategoryDataset dataset3 = Utils.createDoubleDataset(map, size,3);
        JFreeChart freeChart3 = Utils.createChart(dataset3, "CT耗时", "标签数", "耗时");
        Utils.saveAsFile(freeChart3, Utils.jpgFilePath + "\\CT耗时.jpg");

    }

    public static void testCount() {
        List<Integer> times = new ArrayList<Integer>();   // 节点数 花费总次数
        List<Integer> TagNums = new ArrayList<Integer>();  //标签数量
        System.out.println("仿真次数");
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        while (count-- > 0) {
            System.out.println("标签总数为");
            Scanner sc = new Scanner(System.in);
            int n = sc.nextInt();
            TagNums.add(n);
            List<Tag> tags = CreateTag.createTags(n, tagSize);   // 仿真标签
            List<String> signlist = new ArrayList<String>();  // 前缀栈
            String sign = "";// 二进制前缀
            CTResult value;//  返回值
            int time = 0;// 标签数对应的查找次数
            String commonPrefix = Utils.commonPrefix(tags);   // 公共前缀
            signlist.add(commonPrefix);
            int success = 0;// 已识别标签个数
            while (signlist.size() > 0) {
                sign = signlist.get(0);
                value = seek(sign, tags, tagSize);
                time++;
                signlist.remove(sign);
                switch (value.getResult()) {
                    case 0:// 无响应
                        break;
                    case 1:// 识别成功
                        success++;
                        break;
                    default:
                        signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                        signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                        break;
                }
                System.out.println("    当前成功识别总数为：" + success);
            }
            times.add(time);
            System.out.println("识别次数为" + time + "次，标签总数为：" + success + "  识别完成！");
        }
        CategoryDataset intDataset = Utils.createIntDataset(TagNums, times);
        JFreeChart chart = Utils.createChart(intDataset, "碰撞树", "识别数量", "识别次数");
        Utils.saveAsFile(chart, Utils.jpgFilePath + "\\ct.jpg");
    }

    public static Result CTProcess(List<Tag> tags, int success, int tagSize) {
        Result result = new Result();
        result.success = success;
        List<String> signlist = new ArrayList<String>();  // 前缀栈
        String sign = "";// 二进制前缀
        CTResult value;//  返回值
        int time = 0;// 标签数对应的查找次数
        String commonPrefix = Utils.commonPrefix(tags);   // 公共前缀
        signlist.add(commonPrefix);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            result.traffic +=sign.length();
            value = seek(sign, tags, tagSize);
            time++;
            signlist.remove(sign);
            switch (value.getResult()) {
                case 0:// 无响应
                    break;
                case 1:// 识别成功
                    result.success++;
                    break;
                default:
                    if (value.getPrefix0().size() == 1&&value.getPrefix1().size() == 1){
                        success +=2; //改进的CT算法 如果只有一位碰撞 成功识别两个标签
                    }else {
                        signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                        signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    }
                    break;

                    /*signlist.add(0, Utils.commonPrefix(value.getPrefix0()));
                    signlist.add(1, Utils.commonPrefix(value.getPrefix1()));
                    break;*/
            }
//            System.out.println("    当前成功识别总数为：" + success);
        }
        result.time = time;
        result.efficiency = (double) tags.size() / time;
        return result;
    }
    /**
     * 将公共前缀与列表中的ID进行比较
     *
     * @param sign 公共前缀
     * @param list 分支内的标签
     * @return
     */
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

}

