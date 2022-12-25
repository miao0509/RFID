package Tree;

import Aloha.CreateTag;
import Aloha.Tag;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class ct {
    public static Map<String, List<Tag>> CT = new HashMap<>(); // 碰撞树  key->前缀    val->前缀下的子树
    public static Integer tagSize = 8;

    /**
     * 将公共前缀与列表中的ID进行比较
     *
     * @param sign 公共前缀
     * @param list 分支内的标签
     * @return
     */
    public static CTResult seek(String sign, List<Tag> list,int tagSize) {
        CTResult result;
        List<Tag> collection0 = new ArrayList<>();//碰撞标签 有共同的前缀（sign+0）
        List<Tag> collection1 = new ArrayList<>();//碰撞标签 有共同的前缀（sign+1）
        for (int i = 0; i < list.size(); i++) {
            if (sign.length() == tagSize) {
                System.out.print("发出信号为 " + sign + "  识别成功");
                return new CTResult(1);
            }
            if (list.get(i).getTag().startsWith(sign + 0)) {
                collection0.add(list.get(i));
            } else if (list.get(i).getTag().startsWith(sign + 1)) {
                collection1.add(list.get(i));
            }
        }

        result = new CTResult(2, collection0, collection1);
        System.out.print("发出信号为 " + sign + "  冲突个数" + (collection1.size() + collection0.size()));

        return result;
    }

    public static void main(String[] args) {
        ct ct = new ct();
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
                value = seek(sign, tags,tagSize);
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

    public static int CTProcess(List<Tag> tags,int success,int tagSize) {
        List<String> signlist = new ArrayList<String>();  // 前缀栈
        String sign = "";// 二进制前缀
        CTResult value;//  返回值
        int time = 0;// 标签数对应的查找次数
        String commonPrefix = Utils.commonPrefix(tags);   // 公共前缀
        signlist.add(commonPrefix);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            value = seek(sign, tags,tagSize);
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
        return time;
    }

}

