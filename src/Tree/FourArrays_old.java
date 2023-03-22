package Tree;

import SAT.Result;
import Utils.*;

import java.util.*;
import java.util.stream.Collectors;

public class FourArrays_old {
    public static void main(String[] args) {
        List<String> list = Utils.createTags(8, 6);
        System.out.println(list);
        list.clear();;
        list.add("000111");
        list.add("000100");
        list.add("011111");
        list.add("110011");
        list.add("101100");
        list.add("100101");
        list.add("111000");
        list.add("001000");
        List<String> list1  = new ArrayList<>(list);
        process(list);
        FourArrays.process(list1);
    }
//[0.0, 0.6087802840434419, 0.6039918336296669, 0.5990452339580576, 0.6006214340945685, 0.5972005999286989, 0.5969304527202035, 0.5988573495964938, 0.5977000546353224, 0.5973682339430025, 0.5966732527889966, 0.5973095282884944, 0.5992771749035346, 0.596854771410247, 0.5964306923218939, 0.5977322006325823, 0.597835980858047, 0.5984640754333986, 0.5964903163947963, 0.5980401708370509, 0.5961084985145816, 0.5961852891653543, 0.5956539858073229, 0.59580958682299, 0.5958454745866543, 0.5960420579109603, 0.5953923904202402, 0.5951534470854981, 0.5955945087375146, 0.595698438060213, 0.5955706117114845, 0.5960077320508833, 0.5953854838146387, 0.5957852208434358, 0.5971062158174196, 0.5963420195974953, 0.595547850124843, 0.5972215284227431, 0.5966218963133199, 0.5963509021715853, 0.5968457388783786, 0.5959061133615043, 0.5966934748975543, 0.5967310234070208, 0.5958921150188992, 0.5955815012893523, 0.5965758334378322, 0.5964030774558866, 0.59601653327725, 0.5968678101507495, 0.5960657808820089]
    public static Result process(List<String> list) {
        List<BMQTTag> tags = new ArrayList<>();
        for (String s : list) {
            tags.add(new BMQTTag(s, 0, ""));
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
            result.collision++;
            // 每次识别两位
            String commonPrefix = TreeUtil.getMergedString(response.stream().map(BMQTTag::getID).collect(Collectors.toList()))
                    .substring(sign.length(), sign.length()+2);
            int x = countCollision(commonPrefix);
            if (x == 0) {
                signlist.add(sign + commonPrefix);
            }
            if (x == 1) {
                int[] ints = find(commonPrefix);
                char[] chars = commonPrefix.toCharArray();
                chars[ints[0]] = '0';
                signlist.add(sign + String.valueOf(chars));
                chars[ints[0]] = '1';
                signlist.add(sign + String.valueOf(chars));
            } else if (x == 2) {  //两位碰撞  根据异或分组
                int detect = Detect(response);
                int and = and(response);
                int or = or(response);
                if (detect == 0) {
                    signlist.add(sign+"00");
                    signlist.add(sign+"11");
                } else if (detect == 1) {
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                }
                else if (and == 0){
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                    signlist.add(sign+"00");
                } else if (or ==1) {
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                    signlist.add(sign+"11");
                }
                else {
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                    signlist.add(sign+"11");
                    signlist.add(sign+"00");
                }
            }
        }
        System.out.println("识别次数为" + result.time + "次，标签总数为：" + result.success + "  识别完成！");
        result.efficiency = (double) result.success / result.time;
        return result;
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
     * @return 符合前缀的标签
     */
    public static List<BMQTTag> seek(String sign, List<BMQTTag> list) {
        List<BMQTTag> response = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BMQTTag tag = list.get(i);
            if (tag.ID.startsWith(sign)) {
                response.add(tag);
            }
            if (tag.ID.length() -  sign.length() >=2) {
                tag.and = (tag.ID.charAt(sign.length() + 1) - '0') & ((tag.ID.charAt(sign.length())) - '0');
                tag.or = ((tag.ID.charAt(sign.length() + 1) - '0') | (tag.ID.charAt(sign.length())) - '0');
                tag.detect = ((tag.ID.charAt(sign.length() + 1) - '0') ==(tag.ID.charAt(sign.length()) - '0'))  ? 0 : 1;
            }
        }
        return response;
    }

    /**
     * 只有两位碰撞   取异或信息剪枝
     *
     * @param response
     * @return
     */
    public static int Detect(List<BMQTTag> response) {
        for (int i = 1; i < response.size(); i++) {
            if (response.get(i - 1).detect != response.get(i).detect) {
                return -1;
            }
        }
        return response.get(0).detect;
    }
    public static int and(List<BMQTTag> response) {
        for (int i = 1; i < response.size(); i++) {
            if (response.get(i - 1).and != response.get(i).and) {
                return -1;
            }
        }
        return response.get(0).and;
    }
    public static int or(List<BMQTTag> response) {
        for (int i = 1; i < response.size(); i++) {
            if (response.get(i - 1).or != response.get(i).or) {
                return -1;
            }
        }
        return response.get(0).or;
    }

    /**
     * @param commonPrefix
     * @return
     */
    public static int[] find(String commonPrefix) {
        int[] ans = new int[3];
        int count = 0;
        for (int i = 0; i < 2; i++) {
            if (commonPrefix.charAt(i) == 'X') {
                ans[count++] = i;
            }
        }
        return ans;

    }


}
