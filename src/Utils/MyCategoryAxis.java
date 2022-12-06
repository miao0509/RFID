package Utils;

import org.jfree.chart.axis.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.text.TextBlock;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MyCategoryAxis  extends CategoryAxis {
    private int stepNum;
    public MyCategoryAxis(int stepNum){
        this.stepNum = stepNum;
    }
    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge){
        List<Tick> ticks = new ArrayList<>();
        if (dataArea.getHeight() <= 0.0 || dataArea.getWidth() < 0.0) {
            return ticks;
        }
        CategoryPlot plot = (CategoryPlot) getPlot();
        List<?> categories = plot.getCategoriesForAxis(this);
        double max = 0.0;
        if (categories != null) {
            CategoryLabelPosition position = super.getCategoryLabelPositions().getLabelPosition(edge);
            int categoryIndex = 0;
            for (Object o : categories) {
                Comparable<?> category = (Comparable<?>) o;
                g2.setFont(getTickLabelFont(category));
                TextBlock label = new TextBlock();
                label.addLine(category.toString(), getTickLabelFont(category), getTickLabelPaint(category));
                if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                    max = Math.max(max, calculateTextBlockHeight(label, position, g2));
                } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                    max = Math.max(max, calculateTextBlockWidth(label, position, g2));
                }
                if (categoryIndex % stepNum == 0) {
                    Tick tick = new CategoryTick(category, label, position.getLabelAnchor(), position.getRotationAnchor(), position.getAngle());
                    ticks.add(tick);
                }
                categoryIndex = categoryIndex + 1;
            }
        }
        state.setMax(max);
        return ticks;
    }

}
