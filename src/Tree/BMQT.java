package Tree;

import SAT.Result;
import Utils.Utils;

import java.util.*;

public class BMQT {
    public static void main(String[] args) {
//        List<String> list = new ArrayList<>();
//        list.add("000110");
//        list.add("101001");
//        list.add("011011");
//        list.add("111110");
//        list.add("110111");
//        list.add("101101");
        List<String> list = Utils.createTags(500, 96);
        String[] tags = {"0000", "0001", "0010"};
        List<String> list1 = new ArrayList<>(list);
        process(list);
        A4PQT.process(list1);
    }
    public static Result process(List<String> list) {
        List<String> signlist = new ArrayList<String>();
        String sign = "";// 二进制前缀
        String[] values = new String[5];// 返回值
        values[0] = "0";
        Result result = new Result();
        signlist.add(sign);
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
                        signlist.add(0, sign + values[i]);
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
        int detect1 = 0;
        int detect2 = 1;
        String first = "";// 记录识别出第一个标签的id;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith(sign)) {
                count++;
                // 统计引起冲突的位
                if (list.get(i).length() >= sign.length() + 2) { //剩余位数大于1
                String s = list.get(i).substring(sign.length(), sign.length() + 2);
                collisionSet.add(s);
                detect1 = detect1|(s.charAt(0)-'0'&s.charAt(1)-'0');
                detect2 = detect2&(s.charAt(0)-'0'|s.charAt(1)-'0');
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
            //System.out.print("发出信号为 " + sign + "  无响应   ");
        } else if (count == 1) {
            //System.out.print("发出信号为 " + sign + "  识别成功    ");
            list.remove(first);
            value = 1;
        } else {
            value = 2;
            //System.out.print("发出信号为 " + sign + "  冲突个数" + count + "    ");
            int countX0 = 0;
            int countX1 = 0;
            int count0X = 0;
            int count1X = 0;
            for (String str : collisionSet) { //统计碰撞位能否剪枝
                if (str.charAt(1) == '0') {
                    countX0++;
                } else if (str.charAt(1) == '1'){
                    countX1++;
                }
                if (str.charAt(0) == '0'){
                    count0X++;
                }else {
                    count1X++;
                }
            }
            if (countX0 == collisionSet.size()) {
                values[len++] = "00";
                values[len] = "10";
            } else if (countX1 == collisionSet.size()) {
                values[len++] = "01";
                values[len] = "11";
            } else if (count0X== collisionSet.size()) {
                values[len++] = "01";
                values[len] = "00";
            } else if (count1X== collisionSet.size()) {
                values[len++] = "10";
                values[len] = "11";
            } else  if (detect1 == 0){
                values[len++] = "00";
                values[len++] = "01";
                values[len] = "10";
            }else if (detect2 ==1){
                values[len++] = "01";
                values[len++] = "10";
                values[len] = "11";
            }else {
                //System.out.print("碰撞位: ");
//                for (String s : collisionSet) {
                    //System.out.print(s + "    ");
//                    values[len++] = s;
//                }
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
