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
public class StatsHPUXTest extends HudsonTestCase{
    
    StatsHPUX stats = new StatsHPUX(424, 2050000L);
    
    public void test() {
        long time = System.currentTimeMillis();
        BufferedReader pOut = getPerformanceOutput();
        List<Integer> pids = new ArrayList<Integer>();
        pids.add(424);
        pids.add(5112);
        pids.add(3956);
        ByteArrayOutputStream outputCpu = new ByteArrayOutputStream();
        ByteArrayOutputStream outputMem = new ByteArrayOutputStream();
        System.out.println(stats);
        try{
            stats.parseStatistics(new PrintStream(outputCpu), new PrintStream(outputMem), time, pOut, pids);
            pOut.close();
            outputCpu.close();
            outputMem.close();
        }catch(Exception ex){
            ex.printStackTrace(System.out);
            fail("During parsing was thrown exception " + ex);
        }
        String resultsCpu[] = outputCpu.toString().split(" ");            
        assertTrue("Method parseMemory return wrong time. It retursn " + resultsCpu[0] + " instead of " + time,resultsCpu[0].equals(String.valueOf(time)));
        assertTrue("Method parseMemory return wrong total cpu usage. It returns " + resultsCpu[1] + " instead of " + 91,resultsCpu[1].equals("91"));
        assertTrue("Method parseMemory return wrong cpu usage of subprocesses. It returns " + resultsCpu[2] + " instead of " + 82,resultsCpu[2].trim().equals("82"));//remove char \n
        String resultsMem[] = outputMem.toString().split(" ");            
        assertTrue("Method parseMemory return wrong time. It retursn " + resultsMem[0] + " instead of " + time,resultsMem[0].equals(String.valueOf(time)));
        assertTrue("Method parseMemory return wrong total memory usage. It returns " + resultsMem[1] + " instead of " + 8,resultsMem[1].equals("8"));
        assertTrue("Method parseMemory return wrong memory usage of subprocesses. It returns " + resultsMem[2] + " instead of " + 5,resultsMem[2].trim().equals("5"));//remove char \n
    }
    
    
    public void testParseSubprocesses() throws IOException{
        StatsHPUX sHP = new StatsHPUX(1,890L);
        List<Integer> pids = new ArrayList<Integer>();
        BufferedReader bOut = getOutputSubProcesses();
        sHP.parseSubprocesses(1, pids, bOut);
        assertTrue("Method parseSubProcesses does not add all subprocesses. It adds " + pids.size() + " processes instead of " + 13, pids.size()==13);
        assertTrue("Method parseSubProcesses does not add all subprocess with id 921", pids.contains(2726));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 921", pids.contains(921));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 673", pids.contains(673));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 476", pids.contains(476));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 480", pids.contains(480));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 669", pids.contains(669));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 640", pids.contains(640));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 698", pids.contains(698));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 836", pids.contains(836));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 19549", pids.contains(19549));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 19552", pids.contains(19552));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 19553", pids.contains(19553));
        assertTrue("Method parseSubProcesses does not add all subprocess with id 19658", pids.contains(19658));
    }
    
    public BufferedReader getOutputSubProcesses(){
        StatsHPUX sHP = new StatsHPUX(1,890L);
        StringBuilder builder = new StringBuilder();
        builder.append("  PID TTY          TIME CMD\n");
        builder.append("    1 ?           04:06 init\n");
        builder.append(" 2726 ?           00:54   sysstat_em\n");
        builder.append("  921 ?           01:19   rpc.statd\n");
        builder.append("  673 ?           00:00   ptydaemon\n");
        builder.append("  476 ?           01:11   syncer\n");
        builder.append("  480 ?           10:13   utmpd\n");
        builder.append("  669 ?           00:19   syslogd\n");
        builder.append("  640 ?           17:49   ipmon\n");
        builder.append("  698 ?           00:00   nktl_daemon\n");
        builder.append("  836 ?           00:00   sshd\n");
        builder.append("19549 ?           00:00     sshd:\n");
        builder.append("19552 ?           00:00       sshd:\n");
        builder.append("19553 ?           00:05         java\n");
        builder.append("19658 ?           00:00           ps\n");
        builder.append("  858 ?           00:00 rpcbind\n");
        BufferedReader pOut = new BufferedReader(new StringReader(builder.toString()));
        return pOut;
    }
    
    public BufferedReader getPerformanceOutput(){
        StatsHPUX sHP = new StatsHPUX(1,890L);
        StringBuilder builder = new StringBuilder();
        builder.append("  PID  %CPU   SZ\n");
        builder.append("    0  0.00     0\n");
        builder.append("    4  0.00    24\n");
        builder.append("  572  0.01 50500\n");
        builder.append("  652  9.00  8000\n");
        builder.append("  540  0.00  9900\n");
        builder.append("  424 80.80 90000\n");
        builder.append(" 5112  1.00  8000\n");
        builder.append(" 3956  0.00  7000\n");
        BufferedReader pOut = new BufferedReader(new StringReader(builder.toString()));
        return pOut;
    }
    
}
