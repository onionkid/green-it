import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class GreenMonitor
{
    static List<String> systemData = new ArrayList<>();
    private static Properties GProperties = new Properties();
    static final Logger logger = Logger.getLogger(GreenMonitor.class.getName());

    public static void main(String args[])
    {

        GreenMonitor gMonitor = new GreenMonitor();

        //load the property file
        try
        {
            gMonitor.readConfig(args[0]);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        logger.info(gMonitor.generateReport());

    }

    /**
     * loads the property from file
     * @param configFilePath
     * @throws IOException
     */
    private void readConfig(String configFilePath) throws IOException {
        FileInputStream propsInput = new FileInputStream(configFilePath);
        GProperties.load(propsInput);
    }

    /**
     * Generates the JSON payload for the server endpoint
     * @return JSON Object
     */
    private String generateReport()
    {
        MonitoringData monitorD = new MonitoringData
                (
                    GProperties.getProperty(MonitoringData.LOCATION.toUpperCase()),
                    GProperties.getProperty(MonitoringData.MACHINE_ID.toUpperCase())
                );

        //set the date when captured
        monitorD.setDatetime(new Date());

        // gets the apps based on uptime
        monitorD.setApplications(GSystemMonitor.getInstance().getApplications());

        // wattage of the laptop
        //TODO: Check what to do with wattage
        monitorD.setWattage(55);

        // gets the uptime of the machine
        monitorD.setUptime(GSystemMonitor.getInstance().getSystemUptime());

        return monitorD.getJSONPayload();
    }
}
