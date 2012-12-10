/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.resource.monitor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 *
 * @author lucinka
 */
public class CPUStatsUnixTest extends HudsonTestCase{
    
    private StatsUnix stats = new StatsUnix(424);
    
    public void testGetStatistics() throws IOException{
        long time = System.currentTimeMillis();
        BufferedReader pOut = prepareOutput();
        List<Integer> pids = new ArrayList<Integer>();
        pids.add(424);
        pids.add(5112);
        pids.add(3956);
        ByteArrayOutputStream outputCpu = new ByteArrayOutputStream();
        ByteArrayOutputStream outputMem = new ByteArrayOutputStream();
        System.out.println(stats);
        try{
            stats.parseStatistics(new PrintStream(outputCpu), new PrintStream(outputMem), time, pOut, pids);
        }catch(Exception ex){
            ex.printStackTrace(System.out);
            fail("During parsing was thrown exception " + ex);
        }
        pOut.close();
        outputCpu.close();
        outputMem.close();
        String resultsCpu[] = outputCpu.toString().split(" ");            
        assertTrue("Method parseMemory return wrong time. It retursn " + resultsCpu[0] + " instead of " + time,resultsCpu[0].equals(String.valueOf(time)));
        assertTrue("Method parseMemory return wrong total cpu usage. It returns " + resultsCpu[1] + " instead of " + 91,resultsCpu[1].equals("91"));
        assertTrue("Method parseMemory return wrong cpu usage of subprocesses. It returns " + resultsCpu[2] + " instead of " + 82,resultsCpu[2].trim().equals("82"));//remove char \n
        String resultsMem[] = outputMem.toString().split(" ");            
        assertTrue("Method parseMemory return wrong time. It retursn " + resultsMem[0] + " instead of " + time,resultsMem[0].equals(String.valueOf(time)));
        assertTrue("Method parseMemory return wrong total memory usage. It returns " + resultsMem[1] + " instead of " + 51,resultsMem[1].equals("51"));
        assertTrue("Method parseMemory return wrong memory usage of subprocesses. It returns " + resultsMem[2] + " instead of " + 50,resultsMem[2].trim().equals("50"));//remove char \n
    }
    
    public BufferedReader prepareOutput(){
        StringBuilder builder = new StringBuilder();
        builder.append("  PID %CPU %MEM\n");
        builder.append("    0  0.0  0.1\n");
        builder.append("    4  0.0  0.1\n");
        builder.append("  572  0.1  0.1\n");
        builder.append("  652  9.0  0.2\n");
        builder.append("  540  0.0  0.1\n");
        builder.append("  424 80.8 50.2\n");
        builder.append(" 5112  1.0  0.1\n");
        builder.append(" 3956  0.0  0.1\n");
        BufferedReader pOut = new BufferedReader(new StringReader(builder.toString()));
        return pOut;
    }
    
}
