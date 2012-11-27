/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import hudson.remoting.DelegatingCallable;
import jenkins.model.Jenkins;

/**
 *
 * @author lucinka
 */
public class ResourceMonitorStopper implements DelegatingCallable<Object, RuntimeException> {

    private String nodeName;
    
    ResourceMonitorStopper(String nodeName){
        this.nodeName=nodeName;
    }
    
    public ClassLoader getClassLoader() {
        return Jenkins.getInstance().getPluginManager().uberClassLoader;
    }

    public Object call() throws RuntimeException {
        String name = "resource monitor " + nodeName;
        for(Thread t:Thread.getAllStackTraces().keySet()){
            if(name.endsWith(t.getName()))
                t.interrupt();                   
        }
        return true;
    }
    
}
