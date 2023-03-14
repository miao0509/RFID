package Tree;

import Aloha.DataSet2SA;
import SAT.Result;
import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

import java.util.*;
import java.util.stream.Collectors;

public class GBAQT {
    public static int dataSize = 15;  // 数据集大小
    public static int tagSize = 15;  // 标签长度
    public static int CT = 1; // 每个值下做多少次实验
    public static boolean flag = true;  // 组号是否碰撞  true 为不碰撞    碰撞就不加前缀
    public static String title = "GBAQT";

    public static void main(String[] args) {
        Map<Integer, DataSet2SA> map = new HashMap<>(); // key-> 帧大小 val-> 吞吐量和标签数目
        ArrayList<Double> trough_put = new ArrayList<>();
        ArrayList<Long> traffic = new ArrayList<>();
        ArrayList<Long> takeTime = new ArrayList<>();
        ArrayList<Integer> tagNums = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        DataSet2SA dataSet2SA_put = new DataSet2SA(trough_put, tagNums, traffic, takeTime);
        int size = 0; // 图有多少个横坐标
        while (dataSize <= 15) {
            int i = 0;
            long start = System.currentTimeMillis();
            Result AdaptTree = new Result();
            while (i++ < CT) { // 每个对应帧数下做CT次实验 取平均值
                List<String> tags = Utils.createTags(dataSize, tagSize);

                Result result = process(tags);
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
        JFreeChart freeChart1 = Utils.createChart(dataset1, title + "吞吐量", "标签数", "效率");
        Utils.saveAsFile(freeChart1, Utils.jpgFilePath + "\\" + title + "吞吐量吞吐量.jpg");
        CategoryDataset dataset2 = Utils.createDoubleDataset(map, size, 2);
        JFreeChart freeChart2 = Utils.createChart(dataset2, title + "吞吐量通信量", "标签数", "通信量");
        Utils.saveAsFile(freeChart2, Utils.jpgFilePath + "\\" + title + "吞吐量通信量.jpg");
        CategoryDataset dataset3 = Utils.createDoubleDataset(map, size, 3);
        JFreeChart freeChart3 = Utils.createChart(dataset3, title + "吞吐量耗时", "标签数", "耗时");
        Utils.saveAsFile(freeChart3, Utils.jpgFilePath + "\\" + title + "吞吐量耗时.jpg");
    }

    public static Result process(List<String> list) {
        List<GNAQTSign> tags = new ArrayList<>(list.size());
        for (String tag : list) {
            tags.add(new GNAQTSign(tag, -1));
        }
        List<GNAQTSign> signlist = new ArrayList<>();
        GNAQTSign sign;// 二进制前缀
        List<GNAQTSign> values = new ArrayList<>();// 返回值
        Result result = new Result();
        String s = commonPrefix(list);
        s = s.length() == 3 ? s:"";
        signlist.add(new GNAQTSign(s, -1));
        //System.out.println(list);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            result.traffic += sign.prefix.length();
            values = seek(sign, tags, sign.group);
            result.time++;
            signlist.remove(sign);
            if (flag&&values.size() == 0) {
                result.idle++;
            } else if (values.size() == 1) {
                result.success++;
            } else {
                if (flag){  //组号没发生碰撞
                    for (GNAQTSign value : values) {
                        value.prefix = sign.prefix+value.prefix;
                    }
                    signlist.addAll(  values);
                }else {
                    signlist.add(new GNAQTSign(sign.prefix, 0));
                    signlist.add(new GNAQTSign(sign.prefix, 1));
                }

                result.collision++;
            }
            System.out.println("    当前成功识别总数为：" + result.success);
            if (list.size() == 0) {
                break;
            }
        }
        System.out.println("识别次数为" + result.time + "次，标签总数为：" + result.success + "  识别完成！");
        result.efficiency = (double) result.success / result.time;
        return result;
    }


    // 将二进制前缀与列表中的ID进行比较
    public static List<GNAQTSign> seek(GNAQTSign sign, List<GNAQTSign> list, int groupID) {
        List<GNAQTSign> collision0 = new ArrayList<>();  //组号为0
        List<GNAQTSign> collision1 = new ArrayList<>();  //组号为1
        List<GNAQTSign> values = new ArrayList<>();
        int count = 0;
        GNAQTSign first = null;// 记录识别出第一个标签的id;
        for (int i = 0; i < list.size(); i++) {
            if (sign.prefix.length() == tagSize){
                if (list.contains(sign)){
                    values.add(list.get(i));
                }
                return values;
            }
            if (sign.prefix.length()+3 >list.get(0).prefix.length()){
                collision0.add(new GNAQTSign(list.get(i).prefix+0,0));
                collision0.add(new GNAQTSign(list.get(i).prefix+1,0));
                values.addAll(collision0);
                return values;
            }
            if (groupID != -1 &&list.get(i).group == groupID &&list.get(i).prefix.startsWith(sign.prefix) ) {
                count++; // 统计引起冲突的位
                if (count == 1) {
                    first = list.get(i);
                }
                if (groupID == 0){
                    collision0.add(new GNAQTSign(list.get(i).prefix.substring(sign.prefix.length(), sign.prefix.length()+3 ), groupID));
                }else {
                    collision1.add(new GNAQTSign(list.get(i).prefix.substring(sign.prefix.length(), sign.prefix.length()+3 ), groupID));
                }
            }else if (groupID == -1  &&list.get(i).prefix.startsWith(sign.prefix)){
                count++; // 统计引起冲突的位
                if (count == 1) {
                    first = list.get(i);
                }
                int group = group(list.get(i).prefix.substring(sign.prefix.length(), sign.prefix.length() + 3));
                list.get(i).group = group;
                if (group == 0) {
                    collision0.add(new GNAQTSign(list.get(i).prefix.substring(sign.prefix.length(), sign.prefix.length() + 3), group));
                } else {
                    collision1.add(new GNAQTSign(list.get(i).prefix.substring(sign.prefix.length(), sign.prefix.length() + 3), group));
                }
            }
        }
        collision0 = collision0.stream().distinct().collect(Collectors.toList());
        collision1 = collision1.stream().distinct().collect(Collectors.toList());
        if (count == 0) {
            //System.out.print("发出信号为 " + sign + "  无响应");
        } else if (count == 1) {
            //System.out.print("发出信号为 " + sign + "  识别成功    ");
            list.remove(first);
        } else {
            if (collision0.size() * collision1.size() == 0) {//组号没有发生碰撞
                flag = true;
                int[] detect = new int[4];
                if (collision0.size() != 0) { // 只有组0有
                    detect = detect(collision0);
                    if (detect[0] == 2) {   //只有两个GNAQTSign
                        int collisionBit = 0, loc = 0;  // 找到碰撞位是0还是1    碰撞位坐标
                        for (int i = 1; i < detect.length; i++) {
                            if (detect[i] == 0) {
                                loc = i-1;
                                collisionBit = collision0.get(0).prefix.charAt(loc)-'0';
                                break;
                            }
                        }
                        int num = 0;
                        for (GNAQTSign gnaqtSign : collision0) {
                            char[] chars = gnaqtSign.prefix.toCharArray();
                            for (int i = 0; i < chars.length; i++) {
                                if (i != loc && collisionBit == 0 && num == 0) {
                                    chars[i] = '0';
                                }else if (i != loc && collisionBit == 0 && num == 1)
                                    chars[i] = '1';
                            }
                            num++;
                            gnaqtSign.prefix = new String(chars);
                        }
                    } else { // 三位都碰撞
                        collision0.clear();
                        collision0.add(new GNAQTSign("000",0));
                        collision0.add(new GNAQTSign("011",0));
                        collision0.add(new GNAQTSign("101",0));
                        collision0.add(new GNAQTSign("110",0));
                    }

                } else { // 只有组1有
                    detect = detect(collision1);
                    if (detect[0] == 2) {   //只有两个GNAQTSign
                        int collisionBit = 0, loc = 0;  // 找到碰撞位是0还是1    碰撞位坐标
                        for (int i = 1; i < detect.length; i++) {
                            if (detect[i] == 0) {
                                loc = i-1;
                                collisionBit = collision1.get(0).prefix.charAt(loc);
                                break;
                            }
                        }
                        for (GNAQTSign gnaqtSign : collision1) {
                            char[] chars = gnaqtSign.prefix.toCharArray();
                            int num = 0;
                            for (int i = 0; i < chars.length; i++) {
                                if (i != loc) {
                                    if (num++ == 0) {
                                        chars[i] = (char) (collisionBit & chars[i] - '0');
                                    } else {
                                        chars[i] = (char) (collisionBit);
                                    }
                                }
                            }
                        }
                    } else {// 三位都碰撞
                        collision1.clear();
                        collision1.add(new GNAQTSign("001",1));
                        collision1.add(new GNAQTSign("010",1));
                        collision1.add(new GNAQTSign("100",1));
                        collision1.add(new GNAQTSign("111",1));
                    }
                }
            } else { //组号发生碰撞
                flag = false;
                return values;
            }
            //System.out.print("发出信号为 " + sign + "  冲突个数" + count + "    ");
        }
        values.addAll(collision1);
        values.addAll(0, collision0);
        return values;
    }

    public static List<GNAQTSign> init(List<String> list) {
        List<GNAQTSign> tags = new ArrayList<>(list.size());
        for (String s : list) {
            tags.add(new GNAQTSign(s, -1));
        }
        return tags;
    }

    /**
     * 位仲裁机制   true 为0组  false 为1 组
     *
     * @param str
     * @return
     */
    public static int group(String str) {
        char[] chars = str.substring(str.length() - 3).toCharArray();
        return (chars[0] ^ chars[1]) == (chars[2] - '0') ? 0 : 1;
    }

    /**
     * 判断有几位碰撞  一共三位 并且指出位置在哪（2，0，1,1） 两位碰撞 分别在第二第三位  至少为2
     *
     * @param tags
     * @return
     */
    public static int[] detect(List<GNAQTSign> tags) {
        int[] count = new int[4];
        for (int i = 0; i < 3; i++) {
            int res = 1;
            for (int j = 1; j < tags.size(); j++) {
                if (tags.get(j - 1).prefix.charAt(i) == tags.get(j).prefix.charAt(i)) {
                    res++;
                } else {
                    break;
                }
            }
            if (res != tags.size()) {  //
                count[0]++;
                count[i + 1]++;
            }

        }
        return count;
    }

    public static String commonPrefix(List<String> tags) {
        if (tags.isEmpty()) {
            return "";
        } else if (tags.size() == 1) {
            return tags.get(0);
        } else {
            Collections.sort(tags);
            return compareTwoString(tags.get(0), tags.get(tags.size() - 1));
        }
    }

    public static String compareTwoString(String str1, String str2) {
        int i = 0;
        while (i < str1.length() && str1.charAt(i) == str2.charAt(i)) {
            i++;
        }
        return str1.substring(0, i);
    }

}

