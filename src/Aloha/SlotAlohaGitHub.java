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

/***
 * @author goatdidi
 * 实现对slotted aloha算法的仿真
 **/
public class SlotAlohaGitHub {


    public static void main(String[] args) {
        double load = 0.1;
        ArrayList<Double> loads = new ArrayList<>();
        ArrayList<Double> trough_put = new ArrayList<>();
        //方法应该全改为static 疏忽了 就不改了
        SlotAlohaGitHub m = new SlotAlohaGitHub();
        while (load <= 5) {
            loads.add(load);
            ArrayList<Double> pkts = m.generate_pkts(load, 10000);
            trough_put.add(m.troughput(pkts, load));
            load = load + 0.1;
            BigDecimal bigDecimal = new BigDecimal(load);
            load = bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        CategoryDataset dataset = Utils.createDoubleDataset(loads, trough_put);
        JFreeChart freeChart = Utils.createChart(dataset, "时隙ALOHA仿真", "装载", "效率");
        //这里我直接用的绝对路径
        Utils.saveAsFile(freeChart, Utils.jpgFilePath + "\\slotAloha.jpg");

    }


    public double next_interval(double load) {
        return (-1.0 / load) * Math.log(Math.random());
    }

    public ArrayList<Double> generate_pkts(double load, int no_pkts) {
        double time = 0;
        ArrayList<Double> pkts = new ArrayList<>();
        for (int i = 0; i < no_pkts; i++) {
            time += next_interval(load);
            pkts.add(Math.ceil(time));
        }
        return pkts;
    }

    public double troughput(ArrayList<Double> pkts, double load) {
        double success = 0.0;
        for (int i = 1; i < pkts.size() - 1; i++) {
            double fail = 0.0;
            if (!pkts.get(i).equals(pkts.get(i - 1)) && !pkts.get(i + 1).equals(pkts.get(i))) {
                success = success + 1;
            }
        }
        return (success / pkts.size()) * load;
    }

    public static JFreeChart createChart(CategoryDataset categoryDataset) {
        // 创建JFreeChart对象：ChartFactory.createLineChart
        JFreeChart jfreechart = ChartFactory.createLineChart("slotted aloha仿真", // 标题
                "装载", // categoryAxisLabel （category轴，横轴，X轴标签）
                "传输数量", // valueAxisLabel（value轴，纵轴，Y轴的标签）
                categoryDataset, // dataset
                PlotOrientation.VERTICAL, true, // legend
                false, // tooltips
                false); // URLs
        // 使用CategoryPlot设置各种参数。以下设置可以省略。
        CategoryPlot plot = (CategoryPlot) jfreechart.getPlot();
        // 背景色 透明度
        plot.setBackgroundAlpha(0.5f);
        // 前景色 透明度
        plot.setForegroundAlpha(0.5f);
        // 其他设置 参考 CategoryPlot类
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true); // series 点（即数据点）可见
        renderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
        renderer.setUseSeriesOffset(true); // 设置偏移量
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        return jfreechart;
    }

    public static CategoryDataset createDataset(ArrayList<Double> loads, ArrayList<Double> trough_put) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        for (int i = 0; i < loads.size(); i++) {
            defaultcategorydataset.addValue(trough_put.get(i), "experiment", loads.get(i));
        }
        return defaultcategorydataset;
    }


}

