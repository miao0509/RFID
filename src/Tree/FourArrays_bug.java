package Tree;

import SAT.Result;
import Utils.TreeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FourArrays_bug {
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
//[0.0, 0.6177211410880138, 0.6053986909354803, 0.6053303136343597, 0.6060487641195759, 0.606272767117578, 0.6031637278024281, 0.6004413130100468, 0.6053711744921231, 0.6017803550932752, 0.6025003767464017, 0.6018718206002991, 0.6036409732941163, 0.6027201496170151, 0.6009544476139407, 0.6034947729464046, 0.6017741600067512, 0.601783834248787, 0.6002739074968955, 0.6016406476178249, 0.6008194432690099, 0.6011846253183813, 0.601977744205282, 0.600439188472704, 0.6013765444655496, 0.6012475731340335, 0.6019598712213591, 0.6004695332884777, 0.6016813682838031, 0.6018426373831683, 0.5999947083370121, 0.6017514038016099, 0.6015868598373229, 0.6009171129278128, 0.6016472563868213, 0.6003103983265365, 0.6015124179081662, 0.6012533640643979, 0.6008183241839816, 0.6013038237688655, 0.6007551113779851, 0.6013409147486992, 0.6017869529101949, 0.6013658784463773, 0.6007407568206232, 0.6009367359194787, 0.6016281162064611, 0.6006359046247983, 0.601546851744063, 0.6011514990733479, 0.6009942256837196]
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
            int[] xpos = find(commonPrefix);
            int x = countCollision(commonPrefix);
            if (x == 1) {
                char[] chars = commonPrefix.toCharArray();
                chars[xpos[0]] = '0';
                list.remove(String.valueOf(chars).substring(0, 96));
                chars[xpos[0]] = '1';
                list.remove(String.valueOf(chars).substring(0, 96));
                result.success += 2;
            } else {  //两位碰撞  根据异或分组
                detect(sign, tags, xpos);
//                result.time++;
                int detect = Detect(response);
                int and = and(response);
                int or = or(response);
                char[] chars = commonPrefix.toCharArray();
                if (detect == 0) {
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                } else if (detect == 1) {
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                } else if (and == 0) {
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                } else if (or == 1) {
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                } else {
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '1';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '1';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
                    chars[xpos[0]] = '0';
                    chars[xpos[1]] = '0';
                    signlist.add(String.valueOf(chars).substring(0, xpos[1]+1));
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
            if (count == 2) break;
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

    public static List<BMQTTag> detect(String sign, List<BMQTTag> list, int[] xpos) {
        List<BMQTTag> response = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BMQTTag tag = list.get(i);
            if (tag.ID.startsWith(sign)) {
                response.add(tag);
            }
            if (tag.ID.length() - sign.length() >= 2) {
                tag.and = (tag.ID.charAt(xpos[1]) - '0') & ((tag.ID.charAt(xpos[0])) - '0');
                tag.or = ((tag.ID.charAt(xpos[1]) - '0') | (tag.ID.charAt(xpos[0])) - '0');
                tag.detect = ((tag.ID.charAt(xpos[1]) - '0') == (tag.ID.charAt(xpos[0]) - '0')) ? 0 : 1;
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
        int[] ans = new int[2];
        int count = 0;
        for (int i = 0; i < commonPrefix.length(); i++) {
            if (commonPrefix.charAt(i) == 'X') {
                ans[count++] = i;
                if (count == 2 ) break;
            }
        }
        return ans;

    }


}
