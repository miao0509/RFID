package Tree;

import SAT.Result;
import Utils.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 按照CT算法 每次访问连续的两位  加自动识别机制
 */
public class FourArrays {
    public static void main(String[] args) {
//        List<String> list = Utils.createTags(5, 6);
//        System.out.println(list);
//        Result process = process(list);
//        while (process.success !=list.size()) {
//           list = Utils.createTags(5, 6);
//            System.out.println(list);
//          process = process(list);
//        }
        List<String> list = new ArrayList<>();
        list.add("010011");
        list.add("000111");
        list.add("011111");
        list.add("011100");
        list.add("001101");
        process(list);
    }
//[0.0, 0.6091070200009826, 0.609150680985645, 0.5969111187764288, 0.5964731740201986, 0.6005389132170148, 0.5939184917620559, 0.5986590312000079, 0.5973885590908475, 0.6001500151257442, 0.5951954598383048, 0.5968883101223564, 0.59813431964612, 0.5987465648118273, 0.5982825018639961, 0.5957048060264937, 0.5968148378853537, 0.5979636527505732, 0.5973370632067874, 0.5960370016167521, 0.5965350709561532, 0.5964364663785305, 0.596699676360034, 0.5951953606577878, 0.5951907673478852, 0.5960481050670708, 0.5960926285155598, 0.5944843456023627, 0.5961963371771279, 0.5948661587553964, 0.5953303793426674, 0.5957587390228132, 0.59638225375881, 0.5958042245622379, 0.5961030535752155, 0.5963494226015826, 0.5959997366151759, 0.5965010280766951, 0.5960402298457247, 0.595989665757187, 0.5963843933533749, 0.5972843731620441, 0.5961916637085994, 0.5965782498637252, 0.5959363617730071, 0.5973404321365957, 0.597081402595958, 0.5959785377297729, 0.5962565418063303, 0.5967307623328886, 0.5971663094549143]
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
                list.remove(String.valueOf(chars).substring(0, 6));
                chars[xpos] = '1';
                list.remove(String.valueOf(chars).substring(0, 6));
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
                }
//                else if (and == 0) {
//                    chars[xpos] = '0';
//                    chars[xpos + 1] = '0';
//                    signlist.add(String.valueOf(chars).substring(0, end));
//                    chars[xpos] = '0';
//                    chars[xpos + 1] = '1';
//                    signlist.add(String.valueOf(chars).substring(0, end));
//                    chars[xpos] = '1';
//                    chars[xpos + 1] = '0';
//                    signlist.add(String.valueOf(chars).substring(0, end));
//                } else if (or == 1) {
//                    chars[xpos] = '1';
//                    chars[xpos + 1] = '1';
//                    signlist.add(String.valueOf(chars).substring(0, end));
//                    chars[xpos] = '0';
//                    chars[xpos + 1] = '1';
//                    signlist.add(String.valueOf(chars).substring(0, end));
//                    chars[xpos] = '1';
//                    chars[xpos + 1] = '0';
//                    signlist.add(String.valueOf(chars).substring(0, end));
//                }
                else {
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
