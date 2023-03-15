package Test;

import Aloha.CreateTag;
import Aloha.Tag;
import Tree.GBAQT;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {


    public static void main(String[] args) {
        List<Tag> tags = CreateTag.createTags(3, 4);
        String s = Utils.commonPrefix2(tags);
        System.out.println(tags);
        System.out.println(s);
    }
}
