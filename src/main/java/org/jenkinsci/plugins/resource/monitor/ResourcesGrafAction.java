/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildBadgeAction;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author lucinka
 */
public class ResourcesGrafAction implements BuildBadgeAction{
    
    private String path;
    
    public ResourcesGrafAction(AbstractBuild build){
        try {
            this.path = build.getRootDir().getCanonicalPath();
            System.out.println(path);
        } catch (IOException ex) {
            Logger.getLogger(ResourcesGrafAction.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public String getCpuGraphPath(){
        return (path + "/cpuGraph.png");
    }
    
}
