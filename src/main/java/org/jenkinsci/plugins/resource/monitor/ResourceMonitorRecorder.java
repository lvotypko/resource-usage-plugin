/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Item;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.model.listeners.RunListener;
import hudson.slaves.SlaveComputer;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.resource.monitor.drow.CreateGraph;
import org.kohsuke.stapler.StaplerRequest;



/**
 *
 * @author lucinka
 */
public class ResourceMonitorRecorder extends Recorder{

    public BuildStepMonitor getRequiredMonitorService() {
       
        return BuildStepMonitor.BUILD;
    }
    
    @Override
    public boolean prebuild(AbstractBuild build, BuildListener listener){
        Computer computer = build.getExecutor().getOwner();
        Launcher launcher = computer.getNode().createLauncher(listener);
        String type = "unix";
        if(computer instanceof SlaveComputer && (!((SlaveComputer)computer).isUnix()))
            type = "windows";
        try {
            ResourceMonitorStarter starter = new ResourceMonitorStarter(computer.getDisplayName(),build.getWorkspace().getRemote(), type);
            launcher.getChannel().call(starter);
        } catch (Exception ex) {
            listener.getLogger().println("Resource monitor plugin failed to start resource usage statistics monitor");
            Logger.getLogger(ResourceMonitorRecorder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        ResourceMonitorStopper stopper = new ResourceMonitorStopper(build.getExecutor().getOwner().getDisplayName());
        try {
            launcher.getChannel().call(stopper);
        } catch (Exception ex) {
            listener.getLogger().println("Resource monitor failde to stop resource usage monitor");
            Logger.getLogger(ResourceMonitorRecorder.class.getName()).log(Level.SEVERE, null, ex);
        }
        try{
            build.getRootDir();
            CreateGraph creator = new CreateGraph();
            ArrayList<String> cpuRows = new ArrayList<String>();
            cpuRows.add("cpu total");
            cpuRows.add("cpu Jenkins processes");
            creator.createImage("Cpu", new FilePath(build.getWorkspace(),"cpuStats.properties"), new File(build.getRootDir(),"cpuGraph.png"), cpuRows, false);
            ArrayList<String> memRows = new ArrayList<String>();
            memRows.add("Memory total");
            memRows.add("Memory Jenkins processes");
            creator.createImage("Memory", new FilePath(build.getWorkspace(),"memStats.properties"), new File(build.getRootDir(),"memGraph.png"), memRows,false);
            FilePath file = new FilePath(build.getWorkspace(),"diskStats.properties");
            BufferedReader out = new BufferedReader(new InputStreamReader(file.read()));
            String partitions[] = out.readLine().split(" ");
            List<String> diskRows = Arrays.asList(partitions);       
            creator.createImage("Disk", new FilePath(build.getWorkspace(),"diskStats.properties"), new File(build.getRootDir(),"diskGraph.png"), diskRows,true);
            build.addAction(new ResourcesGrafAction(build));
        }
        catch(Exception ex){
            listener.getLogger().println("Resource monitor failde to load resource usage statistics");
            Logger.getLogger(ResourceMonitorRecorder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Resource monitor";
        }
        
        @Override
        public ResourceMonitorRecorder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return new ResourceMonitorRecorder();
        }
        
    }
    
}
