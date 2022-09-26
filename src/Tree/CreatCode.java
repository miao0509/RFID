package Tree;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class CreatCode {
    public static List<String> createData(int dataSize, int tagSize) {
        List<String> list = new ArrayList<String>();
        Random rd = new Random();
        // i 标签数量 j 标签位数
        for (int i = 0; i < dataSize; i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < tagSize; j++) {
                s.append(rd.nextInt(2));
            }
            list.add(s.toString());
        }
        //去重
        System.out.println("去重前大小  " + list.size());
        list = list.stream().distinct().collect(Collectors.toList());
        System.out.println("去重后大小  " + list.size());
        System.out.println(list);
        try {
            File file = new File("D:/2.txt");
            FileWriter fr = new FileWriter(file, false);
            for (int i = 0; i < list.size(); i++) {
                fr.write(list.get(i) + "\n");
            }
            fr.flush();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}


