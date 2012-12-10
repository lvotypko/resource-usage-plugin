/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucinka
 */
public class ResourceMonitor extends Thread{
    private String path;
    private boolean stop = false;
    private String type;
    private int interval;
    

    public ResourceMonitor(String name, String path, String type, int interval){
        super(name);
        this.path = path;
        this.type = type;
        this.interval=interval;
    }
    
    public void stopMonitor(){
        stop = true;
    }
    
    public int getCurrentProcess() throws IOException{
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String id[] = bean.getName().split("@");
        return Integer.parseInt(id[0]);   
    }
    
    @Override
    public void run(){
        File file = new File(path);
        PrintStream log = null;
        try {
            log = new PrintStream(new File(path,"error.log"));
            PrintStream cpuStats = new PrintStream(new File(file, "cpuStats.properties"));
            PrintStream memStats = new PrintStream(new File(file, "memStats.properties"));
            PrintStream diskStats = new PrintStream(new File(file, "diskStats.properties"));
            PerformanceStatistics cpu = null;
            if("unix".equals(type)){
                if("HP-UX".equals(System.getProperty("os.name"))){
                    cpu = new StatsHPUX(getCurrentProcess(), getHPUXMemory());
                }
                else{
                    cpu = new StatsUnix(getCurrentProcess());
                }
                
            }
            else{
                cpu = new CPUStatsWindows(getCurrentProcess(), getFullMemoryForWindows());
            }
            DiskUsageStatistics disk = new DiskUsageStatistics();
            while(!stop){
                disk.getStatistic(System.currentTimeMillis(), diskStats);
                cpu.getStatistics(System.currentTimeMillis(), cpuStats, memStats);
                sleep(interval);
            }
             cpuStats.close();
             memStats.close();
             diskStats.close();
        } catch (Exception ex) {
            log.println(ex);
            ex.printStackTrace(log);
            Logger.getLogger(ResourceMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Long getFullMemoryForWindows() throws IOException{
        Process process = Runtime.getRuntime().exec("systeminfo | find \"Total Physical Memory\"");
        process.getInputStream();
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = pOut.readLine();
        while(line!=null && (!line.contains("Total Physical Memory"))){
            line = pOut.readLine();
        }
        line = line.replace("Total Physical Memory","");
            while(line.contains("  ")){
                line = line.replaceAll("  ", " ");
           }
        String mem[] = line.split(" ");
        return (Long.parseLong(mem[1].replace(",",""))) * 1025;
    }
    
    public Long getHPUXMemory() throws IOException{
        Process process = Runtime.getRuntime().exec("machinfo | grep -i memory");
        process.getInputStream();
        BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
        pOut.readLine();
        String line = pOut.readLine();
        while(line!=null && (!line.contains("Memory"))){
          line = pOut.readLine();
        }
        String mem[] = line.trim().split("\\W+");
        Long memory = Long.parseLong(mem[1]);
        return memory * 1025;
    }
}
