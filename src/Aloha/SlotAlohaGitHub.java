package Aloha;

import Utils.Utils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * @author goatdidi
 * 实现对slotted aloha算法的仿真
 **/
public class SlotAlohaGitHub {


    public static void main(String[] args) {
        double load = 0.01;
        ArrayList<Double> loads = new ArrayList<>();
        Map<String,Map<Double,ArrayList<Double>>> map = new HashMap<>();
        ArrayList<Double> trough_put = new ArrayList<>();
        ArrayList<Double> pure_trough_put = new ArrayList<>();
        while (load <= 4) {
            loads.add(load);
            ArrayList<Double> pkts = generate_pkts(load, 1000);
            ArrayList<Double> pure_pkts = generate_pure_pkts(load, 1000);
            trough_put.add(troughput(pkts, load));
            pure_trough_put.add(pureTroughput(pure_pkts,load));
            load = load + 0.01;
            BigDecimal bigDecimal = new BigDecimal(load);
            load = bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        CategoryDataset dataset = Utils.createDoubleDataset(loads, trough_put,pure_trough_put);
        JFreeChart freeChart = Utils.createChart(dataset, "时隙ALOHA仿真", "装载", "效率");
        //这里我直接用的绝对路径
        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\slotAloha.jpg");

    }


    public static double next_interval(double load) {
        return (-1.0 / load) * Math.log(Math.random());
    }

    public static ArrayList<Double> generate_pkts(double load, int no_pkts) {
        double time = 0;
        ArrayList<Double> pkts = new ArrayList<>();
        for (int i = 0; i < no_pkts; i++) {
            time += next_interval(load);
            pkts.add(Math.ceil(time));
        }
        return pkts;
    }
    public static ArrayList<Double> generate_pure_pkts(double load, int no_pkts) {
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
            if (!pkts.get(i).equals(pkts.get(i + 1)) && !pkts.get(i - 1).equals(pkts.get(i))) {
                success = success + 1;
            }
        }
        return (success / pkts.size()) * load;
    }
    public static double pureTroughput(ArrayList<Double> pkts, double load) {
        double success =0;
        for (int i = 0; i < pkts.size(); i++) {
            if ((i == 0 &&(pkts.get(i + 1) - pkts.get(i)) > 1 )||(i == pkts.size()-1 &&(pkts.get(i) - pkts.get(i - 1)) > 1)){
                success++;
            }
            if(i != 0 && i != pkts.size()-1 &&(pkts.get(i) - pkts.get(i - 1)) > 1 && (pkts.get(i + 1) - pkts.get(i)) > 1) {
                success ++;
            }
        }
        return (success / pkts.size()) * load;
    }
}

