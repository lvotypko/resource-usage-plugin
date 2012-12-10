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
public class StatsHPUX implements PerformanceStatistics{
    
    private int pidParent;
    private Long wholeMemory;   
    
    public StatsHPUX(int pidParent, Long wholeMemory){
        this.pidParent=pidParent;
        this.wholeMemory=wholeMemory;
    }
    
    
    public void getStatistics(long time, PrintStream cpuData, PrintStream memData) throws IOException{
       getStatisticMemAndCpu(cpuData, memData, time);
    }
    
     public void getStatisticMemAndCpu(PrintStream cpuData, PrintStream memData, long time) throws IOException{
        List<String> env = new ArrayList<String>();
        env.add("UNIX95=\"\"");
        List<Integer> pids = getPidsHP(pidParent);
        Process process = Runtime.getRuntime().exec("ps -e -o pid,pcpu,sz",env.toArray(new String[1]));
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        parseStatistics(cpuData, memData, time, pOut, pids);
        
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
            memTotal = (memTotal / wholeMemory) * 100;
            memPart = (memPart / wholeMemory) * 100;
        memData.println(time + " " + Math.round(memTotal) + " " + Math.round(memPart));
     }
     
     public List<Integer> getPidsHP(int pidParent) throws IOException{
         List<Integer> pids = new ArrayList<Integer>();
          List<String> env = new ArrayList<String>();       
            env.add("UNIX95=\"\"");
            Process process = Runtime.getRuntime().exec("ps -eH",env.toArray(new String[1]));
            BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            parseSubprocesses(pidParent, pids, pOut);
            return pids;
     }
     
     public void parseSubprocesses(int pidParent, List<Integer> pids, BufferedReader pOut) throws IOException{
         pOut.readLine();
         String line = pOut.readLine();
            while(line!=null){
                String items[] = line.split(" ");
                String pidString = items[0];
                int i = 1;
                while("".equals(pidString)){
                    pidString = items[i];
                    i++;
                }
                int pid = Integer.parseInt(pidString);
                if(pid==pidParent){
                  addAllSubProcessPid(pOut, line, pid, pids);
                  break;
                }
            }
     }
     
     public void addAllSubProcessPid(BufferedReader pOut, String line, int pid, List<Integer> pids) throws IOException{
         line = line.replace(String.valueOf(pid), "");
         line = line.trim();
         int length = line.length() - (line.replaceAll(" ", "").length());
         boolean hasSubProcess = true;
         line = pOut.readLine();
         while(hasSubProcess && line!=null){
            String items[] = line.split(" ");
            line = line.replace(String.valueOf(items[0]), "");
            line = line.trim();
            String pidString = items[0];
                int i = 1;
                while("".equals(pidString)){
                    pidString = items[i];
                    i++;
                }
            line = line.replace(pidString, "");
            line = line.trim();
            hasSubProcess = length< (line.length() - (line.replaceAll(" ", "").length()));
            if(hasSubProcess){
                pids.add(Integer.parseInt(pidString));
            }
            line = pOut.readLine();
         }
     }
    
}
