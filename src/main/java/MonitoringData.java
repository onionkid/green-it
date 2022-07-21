import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

public class MonitoringData {
    private static final Logger logger = Logger.getLogger(GreenMonitor.class.getName());

    public static final String LOCATION = "location";
    public static final String UPTIME = "uptime";
    public static final String APPLICATIONS = "apps";
    public static final String WATTAGE = "wattage";
    public static final String DATETIME = "datetime";
    public static final String MACHINE_ID = "machine_id";
    private JSONObject payload = null;

    public MonitoringData(String location, String machineID)
    {
        this.payload = new JSONObject();

        this.setLocation(location);
        this.setMachineID(machineID);
    }

    public void setLocation(String location)
    {
        this.payload.put(LOCATION,location);
    }

    public void setMachineID(String machineID)
    {
        this.payload.put(MACHINE_ID,machineID);
    }

    public void setApplications(JSONArray apps)
    {
        this.payload.put(APPLICATIONS, apps);
    }

    public void setUptime(long uptime)
    {
        this.payload.put(UPTIME, uptime);
    }

    public void setWattage(int wattage)
    {
        this.payload.put(WATTAGE, wattage);
    }

    public void setDatetime(Date datetime)
    {
        String datePattern = "MM/dd/YYYY HH:mm:ss";
        this.payload.put(DATETIME, new SimpleDateFormat(datePattern).format(datetime));
    }

    //Getters
    public String getMachineId()
    {
        return this.payload.get(MACHINE_ID).toString();
    }

    public String getLocation()
    {
        return this.payload.get(LOCATION).toString();
    }

    public String getDatetime()
    {
        return this.payload.get(DATETIME).toString();
    }

    public int getWattage()
    {
        return Integer.parseInt(this.payload.get(WATTAGE).toString());
    }

    public long getUptime()
    {
        return Integer.parseInt(this.payload.get(UPTIME).toString());
    }

    public JSONArray getApplications()
    {
        return  (JSONArray) this.payload.get(APPLICATIONS);
    }

    public String getJSONPayload()
    {
        return this.payload.toJSONString();
    }

    public JSONObject getJSONObject()
    {
        return this.payload;
    }
}
