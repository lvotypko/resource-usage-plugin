/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.model.ViewGroup;
import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.jvnet.hudson.Windows;

/**
 *
 * @author lucinka
 */
public class CPUStatsUnix implements PerformanceStatistics{
    
    private boolean hp;
    private int pidParent;
    private int wholeMemory;
    
    public PrintStream log;
    
    public CPUStatsUnix(int pidParent) throws IOException{
        hp = "HP-UX".equals(System.getProperty("os.name"));
        this.pidParent=pidParent;
        if(hp){
            Process process = Runtime.getRuntime().exec("free");
            process.getInputStream();
            BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            pOut.readLine();
            String line = pOut.readLine();
            String[] mem = line.trim().split("\\W+");
            this.wholeMemory = Integer.parseInt(mem[1]);
        }
    }
    
    
    public void getStatistics(long time, PrintStream cpuData, PrintStream memData) throws IOException{
       getStatisticMemAndCpu(cpuData, memData, time);
    }
    
     public void getStatisticMemAndCpu(PrintStream cpuData, PrintStream memData, long time) throws IOException{
        String cmd[];
        if(hp){
            cmd =new String[]{"export UNIX95=\"\"", "ps -e -o pcpu,sz"};
        }
        else{
            cmd =new String[]{"ps -e -o pcpu,pmem"};
        }
        OSProcess proc = ProcessTree.get().get(pidParent);
        List<Integer> pids = new ArrayList<Integer>();
        Double cpuTotal = new Double(0);
        Double cpuPart = new Double(0);
        Double memPart =new Double(0);
        Double memTotal =new Double(0);
        for(OSProcess p:proc.getChildren()){
            pids.add(p.getPid());
        }
        pids.add(pidParent);
        Process process = Runtime.getRuntime().exec("ps -e -o pid,pcpu,pmem");
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
        cpuData.println(time + " " + cpuTotal.intValue() + " " + cpuPart.intValue());
        if(hp){
            memTotal = (memTotal / wholeMemory) * 100;
            memPart = (memPart / wholeMemory) * 100;
        }
        memData.println(time + " " + memTotal.intValue() + " " + memPart.intValue());
        
    }
    
}
