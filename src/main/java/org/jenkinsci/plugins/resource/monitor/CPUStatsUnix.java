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
public class CPUStatsUnix implements PerformanceStatistics{
    
    private boolean hp;
    private int pidParent;
    private int wholeMemory;   
    
    public CPUStatsUnix(int pidParent) throws IOException{
        hp = "HP-UX".equals(System.getProperty("os.name"));
        this.pidParent=pidParent;
        if(hp){
            Process process = Runtime.getRuntime().exec("machinfo | grep -i memory");
            process.getInputStream();
            BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            pOut.readLine();
            String line = pOut.readLine();
            while(line!=null && (!line.contains("Memory"))){
              line = pOut.readLine();
            }
            String mem[] = line.trim().split("\\W+");
            int memory = Integer.parseInt(mem[1]);
            this.wholeMemory = memory * 1025;
        }
    }
    
    
    public void getStatistics(long time, PrintStream cpuData, PrintStream memData) throws IOException{
       getStatisticMemAndCpu(cpuData, memData, time);
    }
    
     public void getStatisticMemAndCpu(PrintStream cpuData, PrintStream memData, long time) throws IOException{
        String cmd;
        List<String> env = new ArrayList<String>();
        if(hp){
            env.add("UNIX95=\"\"");
            cmd ="ps -e -o pcpu,sz";
        }
        else{
            cmd ="ps -e -o pcpu,pmem";
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
        Process process = Runtime.getRuntime().exec("ps -e -o pid,pcpu,pmem",env.toArray(new String[1]));
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
     
     public List<Integer> getPidsHP(int pidParent) throws IOException{
         List<Integer> pids = new ArrayList<Integer>();
          List<String> env = new ArrayList<String>();       
            env.add("UNIX95=\"\"");
            Process process = Runtime.getRuntime().exec("ps -eH",env.toArray(new String[1]));
            BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = pOut.readLine();
            while(line!=null){
                String items[] = line.split("\\W+");
                int pid = Integer.parseInt(items[0]);
                if(pid==pidParent){
                  addAllSubProcessPid(pOut, line, pid, pids);
                  break;
                }
            }
            return pids;
     }
     
     public void addAllSubProcessPid(BufferedReader pOut, String line, int pid, List<Integer> pids) throws IOException{
         line = line.replace(String.valueOf(pid), "");
         line = line.trim();
         int length = line.length() - (line.replaceAll(" ", "").length());
         boolean hasSubProcess = true;
         line = pOut.readLine();
         while(hasSubProcess && line!=null){
            String items[] = line.split("\\W+");
            line = line.replace(String.valueOf(items[0]), "");
            line = line.trim();
            hasSubProcess = length< (line.length() - (line.replaceAll(" ", "").length()));
            if(hasSubProcess){
                pids.add(Integer.parseInt(items[0]));
            }
         }
     }
    
}
