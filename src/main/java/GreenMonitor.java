import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GreenMonitor
{
    static List<String> systemData = new ArrayList<>();
    static final Logger logger = Logger.getLogger(GreenMonitor.class.getName());

    public static final String LOCATION = "location";
    public static final String UPTIME = "uptime";
    public static final String APPLICATIONS = "apps";
    public static final String WATTAGE = "wattage";
    public static final String DATETIME = "datetime";
    public static final String MACHINE_ID = "machine_id";


    public static void main(String args[])
    {
        GreenMonitor gMonitor = new GreenMonitor();

        gMonitor.testMonitor();
        logger.info(gMonitor.generateReport());

    }

    public void testMonitor()
    {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        OperatingSystem os = si.getOperatingSystem();


        logger.info("Checking Processes...");
        printProcesses(os, hal.getMemory());

        logger.info("Print memory");
        printMemory(hal.getMemory());


        StringBuilder output = new StringBuilder();
        for (String line : systemData) {
            output.append(line);
            if (line != null && !line.endsWith("\n")) {
                output.append('\n');
            }
        }
        logger.info(output.toString());
    }

    private String generateReport()
    {
        JSONObject jsonPayload = new JSONObject();

        jsonPayload.put(LOCATION,"CEBU");
        jsonPayload.put(UPTIME,00);
        jsonPayload.put(APPLICATIONS, new JSONArray());
        jsonPayload.put(WATTAGE,50);
        jsonPayload.put(DATETIME, new Date());
        jsonPayload.put(MACHINE_ID,"1");

        return jsonPayload.toJSONString();
    }

    private static void printMemory(GlobalMemory memory) {
        systemData.add("Physical Memory: \n " + memory.toString());
        VirtualMemory vm = memory.getVirtualMemory();
        systemData.add("Virtual Memory: \n " + vm.toString());
        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        if (!pmList.isEmpty()) {
            systemData.add("Physical Memory: ");
            for (PhysicalMemory pm : pmList) {
                systemData.add(" " + pm.toString());
            }
        }
    }

    private void printProcesses(OperatingSystem os, GlobalMemory memory)
    {
        OSProcess myProc = os.getProcess(os.getProcessId());

        // current process will never be null. Other code should check for null here
        systemData.add(
                "My PID: " + myProc.getProcessID() + " with affinity " + Long.toBinaryString(myProc.getAffinityMask()));
        systemData.add("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<OSProcess> procs = os.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.CPU_DESC, 5);
        systemData.add("   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size(); i++) {
            OSProcess p = procs.get(i);
            systemData.add(String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
        }
        OSProcess p = os.getProcess(os.getProcessId());
        systemData.add("Current process arguments: ");
        for (String s : p.getArguments()) {
            systemData.add("  " + s);
        }
        systemData.add("Current process environment: ");
        for (Map.Entry<String, String> e : p.getEnvironmentVariables().entrySet()) {
            systemData.add("  " + e.getKey() + "=" + e.getValue());
        }
    }
}
