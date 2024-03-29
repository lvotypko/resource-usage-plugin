/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;
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
public class CPUStatsWindows implements PerformanceStatistics{
    
    private int pidParent;
    private Long wholeMemory;
    
    public CPUStatsWindows(int pidParent, Long wholeMemory) {
        this.pidParent=pidParent;
        this.wholeMemory=wholeMemory;
    }
    
    public void getStatistics(long time, PrintStream cpuData, PrintStream memoryData) throws IOException{   
        getStatisticCpu(cpuData, time);
        getStatisticMemory(time, memoryData);
    }
    
    public void parseMemory(long time, PrintStream memoryData, BufferedReader pOut, List<Integer> pids) throws IOException{
        pOut.readLine();
        pOut.readLine();
        pOut.readLine();
        pOut.readLine();
        Double total = new Double(0);
        Double part = new Double(0);
        String line = pOut.readLine();
        while(line!=null){
            while(line.contains("  ")){
                line = line.replaceAll("  ", " ");
            }
            String values[] = line.split(" ");
            String number = values[values.length-2].replaceAll(",", "");          
            Integer memoryUsage = Integer.parseInt(number);
            Integer pid = Integer.parseInt(values[1]);
            total = total + memoryUsage;
            if(pids.contains(pid))
                part = part + memoryUsage;
            line = pOut.readLine();
        }
        total = (total / wholeMemory) * 100;
        part = (part / wholeMemory) * 100;
        memoryData.println(time + " " + Math.round(total) + " " + Math.round(part));
    }
    
    public void getStatisticMemory(long time, PrintStream memoryData) throws IOException{
        String cmd = "tasklist";
        OSProcess proc = ProcessTree.get().get(pidParent);
        List<Integer> pids = new ArrayList<Integer>();
        for(OSProcess p:proc.getChildren()){
            pids.add(p.getPid());
        }
        pids.add(pidParent);
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        parseMemory(time, memoryData, pOut, pids);
        
    }
    
    /**
     * Monitor cpu resource
     * 
     * @param cpuData
     * @param time
     * @throws IOException 
     */
    public void getStatisticCpu(PrintStream cpuData, long time) throws IOException{
        String cmd = "typeperf -sc 1 \"\\Process(*)\\ID Process\" \"\\Process(*)\\% Processor time\"";
        OSProcess proc = ProcessTree.get().get(pidParent);
        List<Integer> pids = new ArrayList<Integer>();
        for(OSProcess p:proc.getChildren()){
            pids.add(p.getPid());
        }
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        parseCpu(time, cpuData, pOut, pids);
    }
    
    public void parseCpu(long time, PrintStream cpuData, BufferedReader pOut, List<Integer> pids) throws IOException{
        Double total = new Double(0);
        Double part = new Double(0);
        //pOut.readLine();
        pOut.readLine();
        String line = pOut.readLine();
        String values[] = line.split("\",\"");
        int countOfProcess = ((values.length-1)/2);
        System.out.println(countOfProcess + " " + values.length);
        for(int i=2;i<(countOfProcess);i++){
            Double id = Double.parseDouble(values[i]);
            Double cpuUsage = Double.parseDouble(values[(i+countOfProcess)]);
            total = total + cpuUsage;
            System.out.println(cpuUsage + " " + id.intValue());
            if(pids.contains(id.intValue()))
                part = part + cpuUsage;
        }
        cpuData.println(time + " " + Math.round(total) + " " + Math.round(part));
    }
    
}
