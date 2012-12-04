/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor.drow;

import hudson.FilePath;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author lucinka
 */
public class ResourcesData {
    
    public TimeSeriesCollection load(FilePath file, List<String> nameSeries, boolean startOnSecondLine) throws IOException{
        Map<String, TimeSeries> series = new HashMap<String,TimeSeries>();;
        BufferedReader pOut = new BufferedReader(new InputStreamReader(file.read()));
        if(startOnSecondLine)
            pOut.readLine();
        String line = pOut.readLine();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        while(line!=null){
            String values[] = line.split(" ");
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.decode(values[0]));           
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
