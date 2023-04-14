package Tree;

import SAT.Result;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class qt {
    public static void main(String[] args) {
        List<String> tags = Utils.createTags(10, 96);
        process(tags);
    }


    public static Result process(List<String> list) {
        Result result = new Result();
        List<String> signlist = new ArrayList<String>();
        String sign = "";// 二进制前缀
        int value = 0;//  返回值
        signlist.add(sign);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            value = qt.seek(sign, list);
            result.time++;
            signlist.remove(sign);
            switch (value) {
                case 0:// 无响应
                    result.idle++;
                    break;
                case 1:// 识别成功
                    result.success++;
                    break;
                case 2:
                    signlist.add(0, sign + "0");
                    signlist.add(1, sign + "1");
                    result.collision++;
                    break;
            }
            System.out.println("    当前成功识别总数为：" + result.success);
        }
        result.efficiency = (double)result.success / result.time;
        return result;
    }
    // 将二进制前缀与列表中的ID进行比较
    public static int seek(String sign, List<String> list) {
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

}

