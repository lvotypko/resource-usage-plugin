/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.model.AbstractBuild;
import hudson.model.BuildBadgeAction;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;


/**
 *
 * @author lucinka
 */
public class ResourcesGrafAction implements BuildBadgeAction{
    
    private AbstractBuild build;
    
    public ResourcesGrafAction(AbstractBuild build){
        this.build=build;
    }
    
    public AbstractBuild getBuild(){
        return build;
    }

    public String getIconFileName() {
        return "graph.png";
    }

    public String getDisplayName() {
        return "Resources using";
    }

    public String getUrlName() {
        return "resources-using";
    }
    
    public void doCpuGraph(StaplerRequest req, StaplerResponse res) throws IOException, ServletException{
        File file = new File(build.getRootDir(),"cpuGraph.png");
        res.serveFile(req,file.toURI().toURL());
    }
    
    public void doMemGraph(StaplerRequest req, StaplerResponse res) throws IOException, ServletException{
        File file = new File(build.getRootDir(),"memGraph.png");
        res.serveFile(req,file.toURI().toURL());
    }
    
    public void doDiskGraph(StaplerRequest req, StaplerResponse res) throws IOException, ServletException{
        File file = new File(build.getRootDir(),"diskGraph.png");
        res.serveFile(req,file.toURI().toURL());
    }
    
}
