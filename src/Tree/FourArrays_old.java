package Tree;

import SAT.Result;
import Utils.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 按照QT算法 每次只访问连续两位
 */
public class FourArrays_old {
    public static void main(String[] args) {
        List<String> list = Utils.createTags(500, 96);
        process(list);

    }
    //[0.0, 0.5752743211779388, 0.5651091349309286, 0.5649251811301343, 0.5622741588370629, 0.5625462218607755, 0.5611233400055786, 0.5611441892473813, 0.5608037765938665, 0.5611255452928836, 0.5603774486891521, 0.5614042005365942, 0.560781751390328, 0.5617335488221484, 0.562088349636401, 0.5605959901955305, 0.5609332933183604, 0.5604588956937033, 0.5595305980250785, 0.5597392438596711, 0.5600204072526803, 0.5594574037780188, 0.5598383158944517, 0.5604168969703457, 0.5597940224587992, 0.5599430095675122, 0.5598400896368276, 0.5599138325493416, 0.5596487751540826, 0.5599518793672477, 0.5599068659052235, 0.5600763030281001, 0.5597969318940563, 0.5596416094307128, 0.5600956280847228, 0.5596155101467494, 0.5597541093742506, 0.5602975043424165, 0.560104368552607, 0.5599632388045332, 0.5596397712653629, 0.559956041696005, 0.5600070165299367, 0.5601161092175099, 0.5600836505376204, 0.560084593165998, 0.5594377086730666, 0.5599756088556113, 0.5597941100926279, 0.5601889119252322, 0.5601645639462772]

    public static Result process(List<String> list) {
        int a = 0,b=0,c=0,d=0,e=0,f=0,maxLength = 0;
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
            maxLength = Math.max(sign.length(),maxLength);
            result.traffic += sign.length();
            response = seek(sign, tags);
            result.time++;
            signlist.remove(sign);
            if (response.size() == 0) {
                d++;
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
                f++;
                signlist.add(sign + commonPrefix);
            }
            if (x == 1) {
                e++;
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
                    a++;
                    signlist.add(sign+"00");
                    signlist.add(sign+"11");
                }
                else if (detect == 1) {
                    a++;
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                }
                else if (and == 0){
                    b++;
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                    signlist.add(sign+"00");
                } else if (or ==1) {
                    b++;
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                    signlist.add(sign+"11");
                }
                else {
                    c++;
                    signlist.add(sign+"01");
                    signlist.add(sign+"10");
                    signlist.add(sign+"11");
                    signlist.add(sign+"00");
                }
            }
        }
        System.out.println(maxLength);
        System.out.println("识别次数为" + result.time + "次，标签总数为：" + result.success + "  识别完成！");
        System.out.println("a= "+a + "b= "+b+"c= "+c+"d= "+d+"e= "+e+"f= "+f);
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
