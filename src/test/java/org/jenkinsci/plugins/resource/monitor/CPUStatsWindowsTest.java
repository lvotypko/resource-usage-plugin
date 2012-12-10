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
public class CPUStatsWindowsTest extends HudsonTestCase{
    
    CPUStatsWindows stats = new CPUStatsWindows(424, 2050000L);
    
    public void testParseMemory() throws IOException{
        long time = System.currentTimeMillis();
        BufferedReader pOut = prepareMemoryOutput();
        List<Integer> pids = new ArrayList<Integer>();
        pids.add(424);
        pids.add(5112);
        pids.add(3956);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try{
            stats.parseMemory(time, new PrintStream(output), pOut, pids);
        }catch(Exception ex){
            fail("During parsing was thrown exception " + ex);
        }
        pOut.close();
        String results[] = output.toString().split(" ");            
        assertTrue("Method parseMemory return wrong time. It retursn " + results[0] + " instead of " + time,results[0].equals(String.valueOf(time)));
        assertTrue("Method parseMemory return wrong total memory usage. It returns " + results[1] + " instead of " + 16,results[1].equals("16"));
        assertTrue("Method parseMemory return wrong memory usage of subprocesses. It returns " + results[2] + " instead of " + 15,results[2].trim().equals("15"));//remove char \n
    }
    
     public void testParseCPU() throws IOException{
        long time = System.currentTimeMillis();
        BufferedReader pOut = prepareCPUOutput();
        List<Integer> pids = new ArrayList<Integer>();
        pids.add(424);
        pids.add(5112);
        pids.add(3956);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.out.println(stats);
        try{
            stats.parseCpu(time, new PrintStream(output), pOut, pids);
        }catch(Exception ex){
            ex.printStackTrace(System.out);
            fail("During parsing was thrown exception " + ex);
        }
        pOut.close();
        String results[] = output.toString().split(" ");            
        assertTrue("Method parseMemory return wrong time. It retursn " + results[0] + " instead of " + time,results[0].equals(String.valueOf(time)));
        assertTrue("Method parseMemory return wrong total memory usage. It returns " + results[1] + " instead of " + 93,results[1].equals("93"));
        assertTrue("Method parseMemory return wrong memory usage of subprocesses. It returns " + results[2] + " instead of " + 82,results[2].trim().equals("82"));//remove char \n
    }
    
    public BufferedReader prepareMemoryOutput(){
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("Image Name                     PID Session Name        Session#    Mem Usage\n");
        builder.append("========================= ======== ================ =========== ============\n");
        builder.append("System Idle Process              0 Services                   0         24 K\n");
        builder.append("System                           4 Services                   0        296 K\n");
        builder.append("svchost.exe                    572 Services                   0      8,992 K\n");
        builder.append("svchost.exe                    652 Services                   0      7,656 K\n");
        builder.append("svchost.exe                    540 Services                   0     10,772 K\n");
        builder.append("java.exe                       424 Services                   0    285,216 K\n");
        builder.append("bash.exe                      5112 Services                   0      7,056 K\n");
        builder.append("tasklist.exe                  3956 Services                   0      5,264 K\n");
        BufferedReader pOut = new BufferedReader(new StringReader(builder.toString()));
        return pOut;
    }
    
    public BufferedReader prepareCPUOutput(){
        StringBuilder builder = new StringBuilder();
        builder.append("\"(PDH-CSV 4.0)\",\"\\\\VMG44\\Process(Idle)\\ID Process\",\"\\\\VMG44\\Process(System)\\ID Process\",\"\\\\VMG44\\Process(svchost)\\ID Process\","
                + "\"\\\\VMG44\\Process(svchost#1)\\ID Process\",\"\\\\VMG44\\Process(svchost#2)\\ID Process\",\"\\\\VMG44\\Process(bash)\\ID Process\","
                + "\"\\\\VMG44\\Process(java)\\ID Process\",\"\\\\VMG44\\Process(typeperf)\\ID Process\",\"\\\\VMG44\\Process(_Total)\\ID Process\"\n");
        builder.append("\"12/10/2012 09:26:49.461\",\"0.000000\",\"4.000000\",\"572.000000\",\"652.000000\",\"540.000000\",\"424.000000\",\"5112.000000\","
                + "\"3956.000000\",\"0.000000\",\"198.662834\",\"0.000000\",\"9.000000\",\"1.367800\",\"0.000000\",\"1.123456\",\"78.000000\",\"3.056351\",\"201.719185\"\n");
        BufferedReader pOut = new BufferedReader(new StringReader(builder.toString()));
        return pOut;
    }
    
}
