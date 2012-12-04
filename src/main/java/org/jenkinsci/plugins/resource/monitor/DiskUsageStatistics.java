/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucinka
 */
public class DiskUsageStatistics {
    
    private boolean firstTime = true;
    
    public void getStatistic(long time, PrintStream diskData) throws IOException{
        List<String> partitions = new ArrayList<String>();
        String command = "df -P";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        pOut.readLine();
        String line = pOut.readLine();
        StringBuilder builder = new StringBuilder();
        StringBuilder names = new StringBuilder();
        boolean first = true;
        while(line!=null){
            while(line.contains("  ")){
            line = line.replaceAll("  ", " ");
            }
            String[] diskPartition = line.split(" ");
            String diskUsage = diskPartition[4];
            String partition = diskPartition[5];
            if(partitions.contains(partition)){
                line=pOut.readLine();
                continue;
            }
            partitions.add(partition);
            if(firstTime){
                if(first){
                names.append(partition);
                }
                else{
                    names.append(" ");
                    names.append(partition);
                }
            }
            diskUsage = diskUsage.replace("%", "");
            if(first){
                builder.append(diskUsage);
            }
            else{
                builder.append(" ");
                builder.append(diskUsage);
            }
            line = pOut.readLine();
            first = false;
        }
        if(firstTime){
            diskData.println(names.toString());
            firstTime=false;
        }
        diskData.println(time + " " + builder.toString());
    }
    
}
