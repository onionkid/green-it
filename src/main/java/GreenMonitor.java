import org.json.simple.JSONObject;

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

    static final double PH_NATIONAL_CO2_EMISSION = 0.6032;

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

        MonitoringData myMonitoringData  = gMonitor.generateReport();
        logger.info(myMonitoringData.getJSONPayload());

        try {
            GWebServer.getInstance().setTest(true);
            GWebServer.getInstance().postPayload("https://718ecdd1-9892-4f72-988e-feb68453eb33.mock.pstmn.io/test", myMonitoringData.getJSONObject());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

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
    private MonitoringData generateReport()
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
        monitorD.setWattage(65);

        // gets the uptime of the machine
        monitorD.setUptime(GSystemMonitor.getInstance().getSystemUptime());

        logger.info("Uptime in hours: "+(monitorD.getUptime()/3600));
        double hoursUptime = (double) (monitorD.getUptime()/3600.0);

        double kWattHour = hoursUptime * (monitorD.getWattage() / 1000.0); //kwh

        // Data logging
        logger.info("Uptime in hours: "+hoursUptime);
        logger.info("KWH: "+String.format("%.2f",kWattHour));
        logger.info("Emission (kgs CO2): "+String.format("%.2f",kWattHour*PH_NATIONAL_CO2_EMISSION));

        return monitorD;
    }

    private boolean sendMonitoringData()
    {


        return true;
    }
}
