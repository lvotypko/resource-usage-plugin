/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author lucinka
 */
public interface PerformanceStatistics {
    
     public void getStatistics(long time, PrintStream cpuData, PrintStream memoryData) throws IOException;
     
}
