package Tree;

import Utils.Utils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class fourTree {
    // 读取文件中的ID存入列表


    // 将二进制前缀与列表中的ID进行比较
    public String[] seek(String sign, List<String> list) {
        Set<String> collisionSet = new HashSet<>();
        // values[0] 放置 count的值， 后面放置冲突的位
        String[] values = new String[5];
        int count = 0;
        int value = 0;
        String first = "";// 记录识别出第一个标签的id;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(sign)) {
                count++;
                // 统计引起冲突的位
                if (list.get(i).length() >= sign.length() + 2) { //剩余位数大于1
                    collisionSet.add(list.get(i).substring(sign.length(), sign.length() + 2));
                } else {//剩余位数等于1
                    collisionSet.add(list.get(i).substring(list.get(i).length() - 1));
                }
                if (count == 1) {
                    first = list.get(i);
                }
            }
        }
        int len = 1;
        if (count == 0) {
            System.out.print("发出信号为 " + sign + "  无响应");
        } else if (count == 1) {
            System.out.print("发出信号为 " + sign + "  识别成功    ");
            list.remove(first);
            value = 1;
        } else {
            value = 2;
            System.out.print("发出信号为 " + sign + "  冲突个数" + count + "    ");
            System.out.print("碰撞位: ");
            for (String str : collisionSet) {
                System.out.print(str + "    ");
                values[len++] = str;
            }
        }
        values[0] = String.valueOf(value);
        return values;
    }

    public static void main(String[] args) {
        fourTree m = new fourTree();
        List<Integer> times = new ArrayList<Integer>();
        List<Integer> all = new ArrayList<Integer>();
        System.out.println("仿真次数");
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        while (count-- > 0) {
            System.out.println("标签总数为");
            Scanner sc = new Scanner(System.in);
            int n = sc.nextInt();
            all.add(n);
            List<String> list = Utils.createTags(n, 16);
            Utils.saveTags(new HashSet<>(list), "D:/fourTree.txt");
            List<String> signlist = new ArrayList<String>();
            String sign = "";// 二进制前缀
            String[] values = new String[5];// 返回值
            values[0] = "0";
            int time = 0;// 次数
            signlist.add(sign + "00");
            signlist.add(sign + "01");
            signlist.add(sign + "10");
            signlist.add(sign + "11");
            int success = 0;// 成功个数
            while (signlist.size() > 0) {
                sign = signlist.get(0);
                values = m.seek(sign, list);
                time++;
                signlist.remove(sign);
                switch (values[0]) {
                    case "0":// 无响应
                        break;
                    case "1":// 识别成功
                        success++;
                        break;
                    case "2":
                        for (int i = 1; i < values.length && values[i] != null; i++) {
                            signlist.add(i - 1, sign + values[i]);
                        }
                        break;
                }
                System.out.println("    当前成功识别总数为：" + success);
                if (list.size() == 0) {
                    break;
                }
            }
            times.add(time);
            System.out.println("识别次数为" + time + "次，标签总数为：" + success + "  识别完成！");

            CategoryDataset intDataset = Utils.createIntDataset(all, times);
            JFreeChart chart = Utils.createChart(intDataset, "四叉树仿真", "识别数量", "识别次数");
            Utils.saveAsFile(chart, Utils.jpgFilePath + "\\fourTree.jpg");
        }
    }


}

