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
public class StatsUnix implements PerformanceStatistics{
    
    private int pidParent;
    
    public StatsUnix(int pidParent) {
        this.pidParent=pidParent;
    }
    
    
    public void getStatistics(long time, PrintStream cpuData, PrintStream memData) throws IOException{
       getStatisticMemAndCpu(cpuData, memData, time);
    }
    
     public void getStatisticMemAndCpu(PrintStream cpuData, PrintStream memData, long time) throws IOException{
        OSProcess proc = ProcessTree.get().get(pidParent);
        List<Integer> pids = new ArrayList<Integer>();
        for(OSProcess p:proc.getChildren()){
            pids.add(p.getPid());
        }
        pids.add(pidParent);
        Process process = Runtime.getRuntime().exec("ps -e -o pid,pcpu,pmem");
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        parseStatistics(cpuData, memData, time, pOut,pids);
        
    }
     
     public void parseStatistics(PrintStream cpuData, PrintStream memData, long time, BufferedReader pOut, List<Integer> pids) throws IOException{
        Double cpuTotal = new Double(0);
        Double cpuPart = new Double(0);
        Double memPart =new Double(0);
        Double memTotal =new Double(0);
        pOut.readLine();
         String line = pOut.readLine();
        while(line!=null){
            String values[] = line.split(" ");
            String pid = values[0];
            String mem ="";
            String cpu ="";
            int i =1;
            while("".equals(pid)){
                pid=values[i];
                i++;
            }
            while("".equals(cpu)){
                cpu = values[i];
                i++;
            }
             while("".equals(mem)){
                mem = values[i];
                i++;
            }
            Integer id = Integer.decode(pid);
            Double cpuUsage = Double.parseDouble(cpu);
            Double memUsage = Double.parseDouble(mem);
            cpuTotal = cpuTotal + cpuUsage;
            memTotal = memTotal + memUsage;
            if(pids.contains(id)){
                cpuPart = cpuPart + cpuUsage;
                memPart = memPart + memUsage;
            }
            line = pOut.readLine();
        }
        cpuData.println(time + " " + Math.round(cpuTotal) + " " + Math.round(cpuPart));
        memData.println(time + " " + Math.round(memTotal) + " " + Math.round(memPart));
     }
     
    
}
