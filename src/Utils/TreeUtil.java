package Utils;

import Aloha.Tag;

import java.util.*;

public class TreeUtil {
    public static List<String> createTags(int dataSize, int tagSize) {
        Set<String> tags = new HashSet<>();
        Random rd = new Random();
        // i 标签数量 j 标签位数
        while (tags.size() != dataSize) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < tagSize; j++) {
                s.append(rd.nextInt(2));
            }
            tags.add(s.toString());
        }
        return new ArrayList<>(tags);
    }
    public static String commonPrefix2(List<String> tags){
        if (tags.isEmpty()){
            return "";
        }else if (tags.size()==1){
            return tags.get(0);
        }else {
            Collections.sort(tags);
            return compareTwoString2(tags.get(0),tags.get(tags.size()-1));
        }
    }
    public static String compareTwoString2(String str1,String str2){
        int i =  0;
        int count = 0;
        int j= 0,k = 0;
        while (i <str1.length()){
            if (str1.charAt(i)!=str2.charAt(i) ){
                count++;
                if (count == 1) j = i;
                if (count == 2) k = i;
            }
            i++;
            if (count == 2) break;
        }
        String s = str1.substring(0, i);
        char[] chars = s.toCharArray();
        chars[j] = 'x';
        chars[k] = 'x';
        return String.valueOf(chars);
    }
}