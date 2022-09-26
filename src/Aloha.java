import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Aloha extends JFrame {
    //Site base is 1
    public static int base = 1;
    //Total number of sites
    public int MaxStation;
    //The time period needs to be set to be relatively long, otherwise the conflict of each time slot may be very large, and simulation will not be possible
    public final int randTime = 1000;
    //The larger the successPost, the closer to the real situation and the clearer the curve
    private final int successPost = 1000;
    //Time slot size
    public final int time = 2;
    //Total number of frames
    public int countPoint = 0;
    //Station queue
    private List<Integer> list;
    //canvas
    private Graphics g;

    //Set the maximum number of sending sites
    public void setNum(int num) {
        this.MaxStation = num;
    }

    //Simulation function
    public void GetStatus() {
        Random r = new Random();
        int total = 0;
        int success = 0;
        list = new ArrayList<Integer>();

        //给标签随机一个时间发送数据
        for (int i = 0; i < MaxStation; i++) {
            list.add(r.nextInt(randTime) + 1);
        }

        //排序
        Collections.sort(list);

        int count = 0;//记录尝试多少次,第几个时隙
        while (success <= successPost) { //模拟多少次
            int temcount = 0;//一个时隙内发送的tag数量
            for (int i = 0; i < MaxStation; i++) {
                if (list.get(i) >= count * time && list.get(i) <= (count + 1) * time) {
                    total += 1;
                    temcount += 1;
                } else {
                    break;
                }
            }
            count++;
            if (temcount == 0) {//no frame
                //continue;
            } else if (temcount == 1) {//Successfully send data
                success += 1;
                list.set(0, r.nextInt(randTime) + 1 + count * time);

            } else if (temcount > 1) {//Conflict, send data at random time
                for (int j = 0; j < temcount; j++) {
//                    list.get(j).set(1000+1+count*time);
                    list.set(j, r.nextInt(randTime) + 1 + count * time);
                }
            }
            Collections.sort(list);
        }
        drawPoint((int) ((1.0 * total / count) * 70 + 100), (int) (400 - (10.0 * success / count) * 29));
        System.out.println("负载:" + (1.0 * total / count));
        System.out.println("吞吐量:" + (1.0 * success / count));
    }

    public static void main(String[] args) {
        Aloha aloha = new Aloha();
        aloha.initUI();//Initialize UI
        int i = 0;
        while (true) {
            aloha.setNum(++i);//Increase the number of sites
            aloha.GetStatus();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    //Initialize interface
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.drawLine(100, 400, 500, 400);
        g.setColor(Color.YELLOW);
        g.drawLine(100, 100, 100, 400);
        g.setColor(Color.BLACK);
        g.drawString("0", 90, 405);

        for (int i = 1; i <= 5; i++) {
            g.drawString("|", 100 + 70 * i, 398);
            g.drawString(i + "", 100 + 70 * i, 413);
        }
        g.drawString("G (number of attempts per packet)", 260, 430);


        for (int i = 1; i <= 10; i++) {
            g.drawString("-", 100, 400 - 29 * i);
            if (i != 10) {
                g.drawString("0." + i, 80, 400 - 29 * i);
            } else {
                g.drawString(i / 10 + ".0", 80, 400 - 29 * i);
            }
        }
        g.setColor(Color.BLACK);
    }

    //Talk
    public void drawPoint(int x, int y) {
        g.drawLine(x, y, x, y);
    }

    //Initialize interface parameters
    public void initUI() {
        FlowLayout f1 = new FlowLayout();
        this.setTitle("Aloha Agreement");
        this.setLayout(f1);
        this.setSize(640, 480);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.g = this.getGraphics();
    }
}
   