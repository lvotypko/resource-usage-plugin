/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.util.ProcessTree;
import hudson.util.ProcessTree.OSProcess;
import java.io.File;
import java.io.IOException;
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
    
    public OSProcess getCurrentProcess() throws IOException{
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        String id[] = bean.getName().split("@");
        OSProcess p = ProcessTree.get().get(Integer.parseInt(id[0]));
        return p;       
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
                cpu = new CPUStatsUnix(getCurrentProcess().getPid());                   
            }
            else{
                cpu = new CPUStatsWindows(getCurrentProcess().getPid());
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
}
