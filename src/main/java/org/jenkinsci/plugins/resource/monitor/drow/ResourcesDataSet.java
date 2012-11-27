/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor.drow;

import hudson.FilePath;
import java.awt.Polygon;
import java.awt.Shape;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;

/**
 *
 * @author lucinka
 */
public class ResourcesDataSet extends AbstractDataset implements CategoryDataset{
    
    private ResourcesData data;
    private List<String> rowNames;
    private static final DrawingSupplier supplier = new DefaultDrawingSupplier(
            DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            // the plot data points are a small diamond shape 
            new Shape[] { new Polygon(new int[] {3, 0, -3, 0},
                    new int[] {0, 4, 0, -4}, 4) }
    );
    
    public ResourcesDataSet(FilePath file, List<String> rowNames, boolean startOnSecondLine){
        try {
            data = new ResourcesData();
            data.load(file,rowNames, startOnSecondLine);
            this.rowNames=rowNames;
        } catch (IOException ex) {
            Logger.getLogger(ResourcesDataSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public Comparable getRowKey(int row) {
        return rowNames.get(row);
    }

    public int getRowIndex(Comparable key) {
        return rowNames.lastIndexOf(key);
    }

    public List getRowKeys() {
        return rowNames;
    }

    public Comparable getColumnKey(int column) {
        return data.getAxisXValue(column);
    }

    public int getColumnIndex(Comparable key) {
        return data.getAxisX().lastIndexOf(key);
    }

    public List getColumnKeys() {
        return data.getAxisX();
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return data.getValue((String) rowKey, (String) columnKey);
    }

    public int getRowCount() {
        return rowNames.size();
    }

    public int getColumnCount() {
        return data.getAxisX().size();
    }

    public Number getValue(int row, int column) {
        return data.getRowValues().get(rowNames.get(row)).get(column);
    }
    
}
