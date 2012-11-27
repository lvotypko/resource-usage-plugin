/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
    

    public ResourceMonitor(String name, String path, String type){
        super(name);
        this.path = path;
        this.type = type;
    }
    
    public void stopMonitor(){
        stop = true;
    }
    
    public OSProcess getCurrentProcess() throws IOException{
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String id[] = bean.getName().split("@");
        OSProcess p = ProcessTree.get().get(Integer.parseInt(id[0]));
        return p.getParent();       
    }
    
    @Override
    public void run(){
        File file = new File(path);
        try {
            PrintStream cpuStats = new PrintStream(new File(file, "cpuStats.properties"));
            PrintStream memStats = new PrintStream(new File(file, "memStats.properties"));
            PrintStream diskStats = new PrintStream(new File(file, "diskStats.properties"));
            PerformanceStatistics cpu = null;
            if("unix".equals(type)){
                CPUStatsUnix c = new CPUStatsUnix(getCurrentProcess().getPid());                   
            }
            else{
                cpu = new CPUStatsWindows(getCurrentProcess().getPid());
            }
            DiskUsageStatistics disk = new DiskUsageStatistics();
            while(!stop){
                disk.getStatistic(System.currentTimeMillis(), diskStats);
                cpu.getStatistics(System.currentTimeMillis(), cpuStats, memStats);
                sleep(10000);
            }
             cpuStats.close();
             memStats.close();
             diskStats.close();
        } catch (Exception ex) {
            Logger.getLogger(ResourceMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
