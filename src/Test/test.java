package Test;

import Aloha.CreateTag;
import Aloha.Tag;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<Tag> tags = CreateTag.createTags(4, 8);
        System.out.println(commonPrefix(tags));


    }
    public static String commonPrefix(List<Tag> tags){
        if (tags.isEmpty()){
            return "";
        }else if (tags.size()==1){
            return tags.get(0).getTag();
        }else {
            tags.sort(Comparator.comparing(Tag::getTag));
            return compareTwoString(tags.get(0).getTag(),tags.get(tags.size()-1).getTag());
        }
    }
    public static String compareTwoString(String str1,String str2){
        int i =  0;
        while (str1.charAt(i)==str2.charAt(i)){
            i++;
        }
        return str1.substring(0,i);
    }
}
