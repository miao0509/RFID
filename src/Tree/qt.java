package Tree;

import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class qt {
    // 读取文件中的ID存入列表
    public List<String> readFile(String string) {
        List<String> list = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(string);
            BufferedReader br = new BufferedReader(fr);
            String s = "";
            while ((s = br.readLine()) != null) {
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 将二进制前缀与列表中的ID进行比较
    public int seek(String sign, List<String> list) {
        int count = 0;
        int value = 0;
        String first = "";// 记录识别出第一个标签的id;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(sign)) {
                count++;
                if (count == 1) {
                    first = list.get(i);
                }
            }
        }
        if (count == 0) {
            System.out.print("发出信号为 " + sign + "  无响应");
        } else if (count == 1) {
            System.out.print("发出信号为 " + sign + "  识别成功");
            list.remove(first);
            value = 1;
        } else {
            value = 2;
            System.out.print("发出信号为 " + sign + "  冲突个数" + count);
        }
        return value;
    }

    public static void main(String[] args) {
        qt qt = new qt();
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
            List<String> signlist = new ArrayList<String>();
            String sign = "";// 二进制前缀
            int value = 0;//  返回值
            int time = 0;// 次数
            signlist.add(sign + "0");
            signlist.add(sign + "1");
            int success = 0;// 成功个数
            while (signlist.size() > 0) {
                sign = signlist.get(0);
                value = qt.seek(sign, list);
                time++;
                signlist.remove(sign);
                switch (value) {
                    case 0:// 无响应
                        break;
                    case 1:// 识别成功
                        success++;
                        break;
                    case 2:
                        signlist.add(0, sign + "0");
                        signlist.add(1, sign + "1");
                        break;
                }
                System.out.println("    当前成功识别总数为：" + success);
            }
            times.add(time);
            System.out.println("识别次数为" + time + "次，标签总数为：" + success + "  识别完成！");
        }
        CategoryDataset intDataset = Utils.createIntDataset(all, times);
        JFreeChart chart = Utils.createChart(intDataset, "二分查找树", "识别数量", "识别次数");
        Utils.saveAsFile(chart, Utils.jpgFilePath + "\\qt.jpg");
    }

}

