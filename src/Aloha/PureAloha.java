package Aloha;//import com.panayotis.gnuplot.JavaPlot;

import Utils.Utils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
//import sun.tools.jconsole.Plotter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/***
 * @author goatdidi
 * 实现对pure aloha算法的仿真
 **/
public class PureAloha extends JFrame {

    public static void main(String[] args) {
        double load = 0.1;
        List<Double> loads = new ArrayList<>();
        List<Double> through_put = new ArrayList<>();
        int i = 0;
        while (load <= 5) {
            loads.add(load);
            ArrayList<Double> pkts = generate_pkts(load, 10000);
            through_put.add(troughput(pkts, load));
            load = load + 0.1;
            i = i + 1;
        }
        CategoryDataset dataset = Utils.createDoubleDataset(loads, through_put);
        JFreeChart freeChart = Utils.createChart(dataset, "纯ALOHA仿真", "装载", "效率");
        //这里我直接用的绝对路径
        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\pureAloha.jpg");
    }

    public static double next_interval(double load) {
        return (-1.0 / load) * Math.log(Math.random());
    }

    public static ArrayList<Double> generate_pkts(double load, int no_pkts) {
        double time = 0;
        ArrayList<Double> pkts = new ArrayList<>();
        for (int i = 0; i < no_pkts; i++) {
            time += next_interval(load);
            pkts.add(time);
        }
        return pkts;
    }

    public static double troughput(ArrayList<Double> pkts, double load) {
        double success = 0.0;
        for (int i = 1; i < pkts.size() - 1; i++) {
            double fail = 0.0;
            if ((pkts.get(i) - pkts.get(i - 1)) > 1 && (pkts.get(i + 1) - pkts.get(i)) > 1) {
                success = success + 1;
            }
        }
        return (success / pkts.size()) * load;
    }


}

