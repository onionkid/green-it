import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

public class GSystemMonitor {

    private static final Logger logger = Logger.getLogger(GreenMonitor.class.getName());
    private static GSystemMonitor myInstance = null;

    private SystemInfo si = new SystemInfo();
    private HardwareAbstractionLayer hal = null;
    private CentralProcessor cpu = null;
    private OperatingSystem os = null;

    private GSystemMonitor()
    {
        this.si = new SystemInfo();
        this.hal = si.getHardware();
        this.cpu = hal.getProcessor();
        this.os = si.getOperatingSystem();
    }

    public static GSystemMonitor getInstance()
    {
        if(myInstance == null)
        {
            myInstance = new GSystemMonitor();
        }

        return myInstance;
    }

    /**
     * gets the system up time
     * @return the system uptime
     */
    public long getSystemUptime()
    {
        return os.getSystemUptime();
    }

    public JSONArray getApplications()
    {
        JSONArray appsList  = new JSONArray();

        // Sort by highest CPU
        List<OSProcess> procs = os.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.CPU_DESC, 5);

        // Loop all process
        for (int i = 0; i < procs.size(); i++) {
            OSProcess p = procs.get(i);
            appsList.add(p.getName());
        }

        return appsList;
    }

    public void getProcessorData()
    {
        logger.info(""+this.cpu.toString());
        logger.info(" Cores:");
        for (CentralProcessor.PhysicalProcessor p : cpu.getPhysicalProcessors()) {
            logger.info("  " + (cpu.getPhysicalPackageCount() > 1 ? p.getPhysicalPackageNumber() + "," : "")
                    + p.getPhysicalProcessorNumber() + ": efficiency=" + p.getEfficiency() + ", id=" + p.getIdString());
        }
    }

    public void computeTDP()
    {

    }

    public void printCpu() {
        CentralProcessor processor = cpu;
        logger.info("Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        logger.info("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        logger.info("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        logger.info(String.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu));
        logger.info(String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
        double[] loadAverage = processor.getSystemLoadAverage(3);
        logger.info("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        logger.info(procCpu.toString());
        long freq = processor.getProcessorIdentifier().getVendorFreq();
        if (freq > 0) {
            logger.info("Vendor Frequency: " + FormatUtil.formatHertz(freq));
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            logger.info("Max Frequency: " + FormatUtil.formatHertz(freq));
        }
        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb = new StringBuilder("Current Frequencies: ");
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(FormatUtil.formatHertz(freqs[i]));
            }
            logger.info(sb.toString());
        }
    }

}
