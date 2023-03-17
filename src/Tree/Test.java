package Tree;

import SAT.Result;
import Utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Test {
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
//        A4PQT.process(list1);
    }

    public static Result process(List<String> list) {
        List<BMQTTag> tags = new ArrayList<>();
        for (String s : list) {
            tags.add(new BMQTTag(s, 0, 0));
        }
        List<String> signlist = new ArrayList<String>();
        String sign = "";// 二进制前缀
        List<BMQTTag> response = new ArrayList<>();
        Result result = new Result();
        signlist.add(sign);
        //System.out.println(list);
        while (signlist.size() > 0) {
            sign = signlist.get(0);
            result.traffic += sign.length();
            response = seek(sign, tags);
            result.time++;
            signlist.remove(sign);
            if (response.size() == 0) {
                result.idle++;
                continue;
            }
            if (response.size() == 1) {
                result.success++;
                list.remove(response.get(0).ID);
                continue;
            }
            String commonPrefix = TreeUtil.getMergedString(response.stream().map(BMQTTag::getID).collect(Collectors.toList()));
            int x = countCollision(commonPrefix);
            if (x == 1) {//只有一位 自动识别
                AutoIdentify(list, commonPrefix);
                result.success += 2;
            } else if (x == 2) {  //两位碰撞  根据异或分组
                int detect = Detect(response, commonPrefix);
                int[] ints = find(commonPrefix);
                char[] chars = commonPrefix.toCharArray();
                if (detect == -1) {
                    chars[ints[0]] = '0';chars[ints[1]] = '0';
                    signlist.add(String.valueOf(chars));
                    chars[ints[0]] = '0';chars[ints[1]] = '1';
                    signlist.add(String.valueOf(chars));
                    chars[ints[0]] = '1';chars[ints[1]] = '0';
                    signlist.add(String.valueOf(chars));
                    chars[ints[0]] = '1';chars[ints[1]] = '1';
                    signlist.add(String.valueOf(chars));
                }else if (detect == 0){
                    chars[ints[0]] = '0';chars[ints[1]] = '0';
                   list.remove(String.valueOf(chars));
                    chars[ints[0]] = '1';chars[ints[1]] = '1';
                    list.remove(String.valueOf(chars));
                }else {
                    chars[ints[0]] = '1';chars[ints[1]] = '0';
                    list.remove(String.valueOf(chars));
                    chars[ints[0]] = '0';chars[ints[1]] = '1';
                    list.remove(String.valueOf(chars));
                }
            }else if (x == 3){
                hmProcess(commonPrefix,signlist,response,result);
            }
        }
        System.out.println("识别次数为" + result.time + "次，标签总数为：" + result.success + "  识别完成！");
        result.efficiency = (double) result.success / result.time;
        return result;
    }
    public static void hmProcess(String commonPrefix,List<String> signlist,List<BMQTTag> response,Result result){
        char[] array = commonPrefix.toCharArray();
        int[] ints = find(commonPrefix);
        Set<String> hmWeights = HMIdentify(response, ints); // 标签设置自己的汉明权重  返回0010 / 0100
        result.time++;
        ArrayList<String> hm = new ArrayList<>(hmWeights);
        String detectHMWeight = detectHMWeight(hm);
        int collisionNum = countCollision(detectHMWeight);
        if (collisionNum == 0){
            if (hm.get(0) == "0010"){
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
            }else if (hm.get(0) == "0100"){
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
            }
        }else if (collisionNum == 2){
            if (detectHMWeight.equals("00XX")){
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
            } else if (detectHMWeight.equals("0X0X")) {
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
            }else if (detectHMWeight.equals("X00X")) {
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
            }else if (detectHMWeight.equals("0XX0")) {
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
            }else if (detectHMWeight.equals("X0X0")) {
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
            }else if (detectHMWeight.equals("XX00")) {
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
            }
        }else if (collisionNum ==3){
            if (detectHMWeight.equals("XXX0")){
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
            } else if (detectHMWeight.equals("XX0X")) {
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
            }else if (detectHMWeight.equals("X0XX")) {
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
            }else if (detectHMWeight.equals("0XXX")) {
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
                signlist.add(String.valueOf(array));
                array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
                signlist.add(String.valueOf(array));
            }
        }else {
            array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '0';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '0';array[ints[1]] = '0';array[ints[2]] = '1';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '0';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '0';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '0';array[ints[1]] = '1';array[ints[2]] = '1';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '0';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '1';array[ints[1]] = '0';array[ints[2]] = '1';
            signlist.add(String.valueOf(array));
            array[ints[0]] = '1';array[ints[1]] = '1';array[ints[2]] = '1';
            signlist.add(String.valueOf(array));
        }
    }
    public static String detectHMWeight(List<String> strs) {
        if (strs == null || strs.size() == 0) {
            return "";
        }
        int count = 0;
        StringBuilder result = new StringBuilder();
        int len = strs.get(0).length();
        for (int i = 0; i < len; i++) {
            char c = strs.get(0).charAt(i);
            for (int j = 1; j < strs.size(); j++) {
                if (strs.get(j).charAt(i) != c) {
                    c = 'X';
                    count++;
                    break;
                }
            }
            result.append(c);
            if (count == 4){
                break;
            }
        }
        return result.toString();
    }
    private static Set<String> HMIdentify(List<BMQTTag> response, int[] ints) {
        Set<String> ans = new HashSet<>();
        for (BMQTTag tag : response) {
            int hmWeight = tag.ID.charAt(ints[0])+tag.ID.charAt(ints[1])+tag.ID.charAt(ints[2])-3* (int)'0';
            tag.HMWeight = hmWeight;
            if (hmWeight == 0){
                ans.add("0001");
            }else if (hmWeight == 1){
                ans.add("0010");
            }else if (hmWeight == 2){
                ans.add("0100");
            }else {
                ans.add("1000");
            }
        }
        return ans;
    }

    private static void AutoIdentify(List<String> list, String commonPrefix) {
        char[] chars = commonPrefix.toCharArray();
        int i = 0;
        for (; i < chars.length; i++) {
            if (chars[i] == 'X') break;
        }
        chars[i] = '0';
        String one = String.valueOf(chars);
        chars[i] = '1';
        String two = String.valueOf(chars);
        list.remove(one);
        list.remove(two);
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


    /**
     * 标签比较自身ID是否和前缀相符
     *
     * @param sign 前缀
     * @param list 剩余标签
     * @return 返回Str【3】  第一个为碰撞信息（直接定位两个碰撞位） 第二个为 两个碰撞位的与信息  第三个为两个碰撞位的或信息
     */
    public static List<BMQTTag> seek(String sign, List<BMQTTag> list) {
        List<BMQTTag> response = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BMQTTag tag = list.get(i);
            if (tag.ID.startsWith(sign)) {
                response.add(tag);
            }
        }
        return response;
    }

    /**
     * 只有两位碰撞   取异或信息剪枝
     *
     * @param list
     * @param commonPrefix
     * @return
     */
    public static int Detect(List<BMQTTag> list, String commonPrefix) {

        int[] ints = find(commonPrefix);

        for (int i = 0; i < list.size(); i++) {
            BMQTTag tag = list.get(i);
            tag.and = ((tag.ID.charAt(ints[0]) - '0') & (tag.ID.charAt(ints[1]) - '0')) == tag.ID.charAt(ints[0]) - '0' ? 0 : 1;
        }
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).and != list.get(i).and) {
                return -1;
            }
        }
        return list.get(0).and;

    }

    public static int[] find(String commonPrefix) {

        int[] ans = new int[3];
        int count = 0;
        for (int i = 0; i < commonPrefix.length(); i++) {
            if (commonPrefix.charAt(i) == 'X') {
                ans[count++] = i;
            }
        }
        return ans;

    }


}
