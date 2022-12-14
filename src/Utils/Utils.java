package Utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Utils {
    public static String jpgFilePath = "D:\\研究生时期\\rfid_test\\RFID\\jpg";

    public static List<String> createTags(int dataSize, int tagSize) {
        Set<String> tags = new HashSet<>();
        Random rd = new Random();
        // i 标签数量 j 标签位数
        while (tags.size() != dataSize) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < tagSize; j++) {
                s.append(rd.nextInt(2));
            }
            tags.add(s.toString());
        }
        System.out.println(tags.size());
        return new ArrayList<>(tags);
    }

    public static List<String> readFile(String string) {
        List<String> list = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(string);
            BufferedReader br = new BufferedReader(fr);
            String s = "";
            while ((s = br.readLine()) != null) {
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveTags(Set<String> tags, String path) {
        try {
            File file = new File(path);
            FileWriter fr = new FileWriter(file, false);
            List<String> list = new ArrayList<>(tags);
            for (String s : list) {
                fr.write(s + "\n");
            }
            fr.flush();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JFreeChart createChart(CategoryDataset categoryDataset, String title, String xName, String yName) {
        // 创建JFreeChart对象：ChartFactory.createLineChart
        JFreeChart jfreechart = ChartFactory.createLineChart(title, // 标题
                xName, // categoryAxisLabel （category轴，横轴，X轴标签）
                yName, // valueAxisLabel（value轴，纵轴，Y轴的标签）
                categoryDataset, // dataset
                PlotOrientation.VERTICAL, true, // legend
                false, // tooltips
                false); // URLs
        jfreechart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
        // 使用CategoryPlot设置各种参数。以下设置可以省略。
        CategoryPlot plot = (CategoryPlot) jfreechart.getPlot();
        CategoryAxis mDomainAxis = plot.getDomainAxis();
        //设置x轴标题的字体
        mDomainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 15));
        //设置x轴坐标字体
        mDomainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 15));
        //y轴
        ValueAxis mValueAxis = plot.getRangeAxis();
        //设置y轴标题字体
        mValueAxis.setLabelFont(new Font("宋体", Font.PLAIN, 15));
        //设置y轴坐标字体
        mValueAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 15));
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


    public static CategoryDataset createIntDataset(List<Integer> loads, List<Integer> trough_put) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        for (int i = 0; i < loads.size(); i++) {
            defaultcategorydataset.addValue(trough_put.get(i), "experiment", loads.get(i));
        }
        return defaultcategorydataset;
    }

    public static CategoryDataset createDoubleDataset(List<Double> loads, List<Double> trough_put) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        for (int i = 0; i < loads.size(); i++) {
            defaultcategorydataset.addValue(trough_put.get(i), "experiment", loads.get(i));
        }
        return defaultcategorydataset;
    }

    public static void saveAsFile(JFreeChart chart, String outputPath) {

        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            File outFile = new File(outputPath);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 保存为PNG
            // ChartUtilities.writeChartAsPNG(out, chart, 600, 400);
            // 保存为JPEG
            ChartUtilities.writeChartAsJPEG(out, chart, 1920, 1080);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
