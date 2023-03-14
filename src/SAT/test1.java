package SAT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class test1 {

    public static void main(String[] args) {
        String[] strings = {"000010","010111","100011","111011","010101","011111","111111","011101","0100100","001000","110100","001100"};
        List<String> list = Arrays.asList("000010", "010111", "100011", "111011", "010101", "011111", "111111", "011101", "0100100", "001000", "110100", "001100");
        list = list.stream().distinct().collect(Collectors.toList());
        System.out.println(list.size());
    }

}
