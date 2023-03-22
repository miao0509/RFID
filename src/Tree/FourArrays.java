package Tree;

import SAT.Result;
import Utils.*;

import java.util.*;
import java.util.stream.Collectors;

public class FourArrays {
    public static void main(String[] args) {
//        List<String> list = Utils.createTags(5, 6);
        List<String> list = new ArrayList<>();
        list.add("010011");
        list.add("010110");
        list.add("000011");
        list.add("011100");
        list.add("010100");
        list.add("001100");
        list.add("100000");
        list.add("110111");
        list.add("101101");
        list.add("001110");
        process(list);
    }

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
            String commonPrefix = TreeUtil.getMergedString(response.stream().map(BMQTTag::getID).collect(Collectors.toList()));
            int xpos = find(commonPrefix);
            int x = countCollision(commonPrefix);
            if (xpos != commonPrefix.length() - 1) {
                x = commonPrefix.charAt(xpos + 1) == 'X' ? 2 : x;
            }
            int end = xpos == commonPrefix.length() - 1 ? xpos + 1 : xpos + 2;
            if (x == 1) {
                char[] chars = commonPrefix.toCharArray();
                chars[xpos] = '0';
                list.remove(String.valueOf(chars).substring(0, 96));
                chars[xpos] = '1';
                list.remove(String.valueOf(chars).substring(0, 96));
                result.success += 2;
            } else if (commonPrefix.charAt(xpos+1) !='X') {
                char[] chars = commonPrefix.toCharArray();
                chars[xpos] = '0';
                signlist.add(String.valueOf(chars).substring(0, end));
                chars[xpos] = '1';
                signlist.add(String.valueOf(chars).substring(0, end));
            } else {  //两位碰撞  根据异或分组
                detect(sign, tags, xpos);
//                result.time++;
                int detect = Detect(response);
                int and = and(response);
                int or = or(response);
                char[] chars = commonPrefix.toCharArray();
                if (detect == 0) {
                    chars[xpos] = '0';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '1';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                } else if (detect == 1) {
                    chars[xpos] = '0';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '1';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
                } else if (and == 0) {
                    chars[xpos] = '0';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '0';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '1';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
                } else if (or == 1) {
                    chars[xpos] = '1';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '0';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '1';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
                } else {
                    chars[xpos] = '1';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '0';
                    chars[xpos + 1] = '1';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '1';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
                    chars[xpos] = '0';
                    chars[xpos + 1] = '0';
                    signlist.add(String.valueOf(chars).substring(0, end));
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
            if (tag.ID.length() - sign.length() >= 2) {
                tag.and = (tag.ID.charAt(sign.length() + 1) - '0') & ((tag.ID.charAt(sign.length())) - '0');
                tag.or = ((tag.ID.charAt(sign.length() + 1) - '0') | (tag.ID.charAt(sign.length())) - '0');
                tag.detect = ((tag.ID.charAt(sign.length() + 1) - '0') == (tag.ID.charAt(sign.length()) - '0')) ? 0 : 1;
            }
        }
        return response;
    }

    public static List<BMQTTag> detect(String sign, List<BMQTTag> list, int xpos) {
        List<BMQTTag> response = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BMQTTag tag = list.get(i);
            if (tag.ID.startsWith(sign)) {
                response.add(tag);
            }
            if (tag.ID.length() - sign.length() >= 2) {
                tag.and = (tag.ID.charAt(xpos + 1) - '0') & ((tag.ID.charAt(xpos)) - '0');
                tag.or = ((tag.ID.charAt(xpos + 1) - '0') | (tag.ID.charAt(xpos)) - '0');
                tag.detect = ((tag.ID.charAt(xpos + 1) - '0') == (tag.ID.charAt(xpos) - '0')) ? 0 : 1;
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
    public static int find(String commonPrefix) {
        int count = 0;
        for (int i = 0; i < commonPrefix.length(); i++) {
            if (commonPrefix.charAt(i) == 'X') {
                count = i;
                break;
            }
        }
        return count;

    }


}
