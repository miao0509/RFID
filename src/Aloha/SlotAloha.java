package Aloha;

import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlotAloha {
    private int loop = 100000; //实验次数

    public void slotAloha(int total, int slotSize) {
        double ave = 0;
        int totalCount = 0;
        for (int x = 0; x < loop; x++) {
            int count = 0; //循环次数
            int idleSlot = 0;
            int success = 0;
            List<Integer> tags = createTags(total, slotSize);
            Random random = new Random();
            while (success != total) {
                for (int i = 0; i < slotSize; i++) {
                    List<Integer> colList = new ArrayList<>();
                    count++;
                    int response = 0;//响应个数
                    for (int j = 0; j < tags.size(); j++) {
                        if (tags.get(j) == 0) {
                            response++;
                            colList.add(j);
                        } else {
                            tags.set(j, tags.get(j) - 1);
                        }
                    }
                    if (response == 0) {
                        idleSlot++;
                    } else if (response == 1) {
                        success++;
                        tags.remove(0);
                    } else {
                        for (int j = 0; j < response; j++) {
                            tags.set(colList.get(j), random.nextInt(slotSize));
                        }
                    }
                }
            }
//            System.out.print("循环次数：  "+ count+"\t");
//            System.out.println("系统效率：  "+1.0*total/count);
            ave += 1.0 * total / count;
        }
        System.out.println("系统平均效率  " + ave / loop);

    }

    public List<Integer> createTags(int total, int slotSize) {
        List<Integer> tags = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < total; i++) {
            tags.add(random.nextInt(slotSize));
        }
        return tags;
    }

    public static void main(String[] args) {
        SlotAloha SlotAloha = new SlotAloha();
        SlotAloha.slotAloha(8, 8);

    }

}
