import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

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
        List<OSProcess> procs = os.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.UPTIME_ASC, 5);

        // Loop all process
        for (int i = 0; i < procs.size(); i++) {
            OSProcess p = procs.get(i);

//            logger.info(p.toString());

            appsList.add(p.getName());
        }

        return appsList;
    }

}
