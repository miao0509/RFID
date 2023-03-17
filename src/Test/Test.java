package Test;

import Aloha.CreateTag;
import Aloha.Tag;
import Tree.GBAQT;
import Utils.TreeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {


    public static void main(String[] args) {
        List<String> tags = TreeUtil.createTags(3, 4);
        String s = TreeUtil.getMergedString(tags);
        System.out.println(tags);
        System.out.println(s);
    }
}
