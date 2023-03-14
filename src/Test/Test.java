package Test;

import Tree.GBAQT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("000110");
        list.add("000101");
        list.add("000011");
        list.add("011111");
        list.add("011010");
        list.add("111111");
        GBAQT.process(list);
    }
}
