/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor.drow;

import hudson.FilePath;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

/**
 *
 * @author lucinka
 */
public class CreateGraph {
    
    public void createImage(String title, FilePath path, File graph, List<String> rowNames, boolean startOnSecondLine){
        ResourcesDataSet dataset = new ResourcesDataSet(path, rowNames, startOnSecondLine);
        JFreeChart chart = createJFreeChart(dataset, title);
        try {
            ChartUtilities.writeChartAsPNG(new FileOutputStream(graph),chart,700,500);
                    
        } catch (IOException ex) {
            Logger.getLogger(CreateGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }

    public JFreeChart createJFreeChart(ResourcesDataSet dataset, String title){
      return ChartFactory.createLineChart3D(title, /*categoryAxisLabel=*/null, "Time", dataset, PlotOrientation.VERTICAL, true, /*tooltips=*/true, /*url=*/false);
    }
}
