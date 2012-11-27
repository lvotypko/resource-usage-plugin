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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author lucinka
 */
public class ResourcesData {
    
    private List<String> axisX = new ArrayList<String>();
    
    private Map<String,List<Integer>> rowValues = new TreeMap<String,List<Integer>>();

    
    public void load(FilePath file, List<String> namesOfRows, boolean startOnSecondLine) throws IOException{
        BufferedReader pOut = new BufferedReader(new InputStreamReader(file.read()));
        if(startOnSecondLine)
            pOut.readLine();
        String line = pOut.readLine();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        while(line!=null){
            String values[] = line.split(" ");
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.decode(values[0]));
            axisX.add(format.format(calendar.getTime()));
            int count = 1;
            for(String rowName: namesOfRows){
                Double value = Double.parseDouble(values[count]);
                List<Integer> rowNameValues = rowValues.get(rowName);
                if(rowNameValues==null){
                    rowNameValues = new ArrayList<Integer>();
                    rowValues.put(rowName, rowNameValues);
                }
                rowNameValues.add(value.intValue());
                count++;
            }
            line = pOut.readLine();
        }
    }
    
    public String getAxisXValue(int index){
        return axisX.get(index);
    }
    
    public List<String> getAxisX(){
        return axisX;
    }
    
    public Integer getValue(String axisXKey,String rowName){
        int index = axisX.lastIndexOf(axisXKey);
        return rowValues.get(rowName).get(index);
    }
    
    public Map<String,List<Integer>> getRowValues(){
        return rowValues;
    }
    
//    public Integer getAxisYValue(int index){
//        return axisY.get(index);
//    }
//    
}
