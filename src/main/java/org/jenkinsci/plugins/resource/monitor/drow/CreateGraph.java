/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor.drow;

import hudson.FilePath;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author lucinka
 */
public class CreateGraph {
    
    public void createImage(String title, FilePath path, File graph, List<String> rowNames, boolean startOnSecondLine, String dateFormat, String timezone) throws IOException{
        if(dateFormat==null)
            dateFormat = "dd.MM. hh:mm:ss";
        TimeSeriesCollection collection = load(path, rowNames, startOnSecondLine, timezone);
        JFreeChart chart = createJFreeChart(collection, title);
        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new DateAxis("time"));
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat(dateFormat));

        try {
            ChartUtilities.writeChartAsPNG(new FileOutputStream(graph),chart,700,500);
                    
        } catch (IOException ex) {
            Logger.getLogger(CreateGraph.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }

    public JFreeChart createJFreeChart(TimeSeriesCollection dataset, String title){
        return ChartFactory.createXYLineChart(title, title, title, dataset, PlotOrientation.VERTICAL, true, true, false);
    }
    
    
    public TimeSeriesCollection load(FilePath file, List<String> nameSeries, boolean startOnSecondLine, String timezone) throws IOException{
        Map<String, TimeSeries> series = new HashMap<String,TimeSeries>();
        BufferedReader pOut = new BufferedReader(new InputStreamReader(file.read()));
        if(startOnSecondLine)
            pOut.readLine();
        String line = pOut.readLine();
        while(line!=null){
            String values[] = line.split(" ");
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.decode(values[0]));
            if(timezone!=null)
                calendar.setTimeZone(TimeZone.getTimeZone(timezone));
            int count = 1;
            for(String serieName: nameSeries){
                Double value = Double.parseDouble(values[count]);
                TimeSeries serie = series.get(serieName);
                if(serie==null){
                    serie = new TimeSeries(serieName, Second.class);
                    series.put(serieName, serie);
                }
                serie.add(new Second(calendar.getTime()),value);
                count++;
            }
            line = pOut.readLine();
        }
        TimeSeriesCollection collection = new TimeSeriesCollection();
        for(TimeSeries serie:series.values()){
            collection.addSeries(serie);
        }
        return collection;
    }
}
