/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.remoting.DelegatingCallable;
import java.util.Set;
import jenkins.model.Jenkins;

/**
 *
 * @author lucinka
 */
public class ResourceMonitorStarter implements DelegatingCallable<Object, RuntimeException>{
    
    private String nodeName;
    private String path;
    private String type;
    
    ResourceMonitorStarter(String nodeName, String path, String type){
        this.nodeName=nodeName;
        this.path =path;
        this.type = type;
    }
    
    public ClassLoader getClassLoader() {
        return Jenkins.getInstance().getPluginManager().uberClassLoader;
    }

    public Object call() throws RuntimeException {
       ResourceMonitor monitor =  new ResourceMonitor("resource monitor " + nodeName, path, type);
       monitor.start();
       return true;
    }
    
    
}
