
package iobp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.*;

public class LogToXES {

    public static String CSV_HEADER = "Case ID, Event ID, Activity, Timestamp, Resource, Cost";
    public static String DATE_TIME_FORMAT_EXPECTED = "MM/dd/yyyy HH:mm:ss";

    public static void CSVtoXES(String eventlog, String outputFileName) throws IOException, ParseException {
        int idTrace = 1, xKeyMap = 0;

        XFactory factory = XFactoryRegistry.instance().currentDefault();
        XLog log = factory.createLog();

        XAttributeLiteral atrTraceName = factory.createAttributeLiteral("concept:name", String.valueOf(idTrace), null);
        XAttributeMap xmapTraceName = factory.createAttributeMap();
        xmapTraceName.put(String.valueOf(xKeyMap++), atrTraceName);

        XTrace trace = factory.createTrace(xmapTraceName);

        BufferedReader bufReader = new BufferedReader(new StringReader(eventlog)); //event log must be in csv
        String line = null;
        while ((line = bufReader.readLine()) != null) {
            if(line.contains(CSV_HEADER))
                continue;
            if(line.equals(""))
                continue;
            String event[] = line.split(",");
            if(!event[0].trim().equals(Integer.toString(idTrace))){ //new trace
                log.add(trace); //add current trace to log

                idTrace = Integer.parseInt(event[0].trim()); //get number of actual trace

                XAttributeLiteral attrNewTraceName = factory.createAttributeLiteral("concept:name", String.valueOf(idTrace), null);
                XAttributeMap xmapNewTraceName = factory.createAttributeMap();
                xmapNewTraceName.put(String.valueOf(xKeyMap++), attrNewTraceName);

                trace = factory.createTrace(xmapNewTraceName); //create new trace
            }

            //Create event's attributes
            XAttributeLiteral eventID = factory.createAttributeLiteral("eventID", event[1].trim(), null);
            String eventName = event[2].trim();
            if(eventName.equals(null) || eventName.equals("")){ // No name
                eventName = " ";
            }
            XAttributeLiteral activityName = factory.createAttributeLiteral("concept:name", eventName, null);
            Date d = null;
            try{d = new SimpleDateFormat(DATE_TIME_FORMAT_EXPECTED).parse(event[3]);}catch (Exception e){}
            try{d = new SimpleDateFormat("MM/dd/yyyy").parse(event[3]);}catch (Exception e){}
            try{d = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(event[3]);}catch (Exception e){}
            try{d = new SimpleDateFormat("HH:mm:ss").parse(event[3]);}catch (Exception e){}
            try{d = new SimpleDateFormat("HH.mm.ss").parse(event[3]);}catch (Exception e){}
            try{d = new SimpleDateFormat("MM-dd-yyyy HH.mm.ss").parse(event[3]);}catch (Exception e){}
            try{d = new Date(event[3]);}catch (Exception e){}
            XAttributeTimestamp timestamp = factory.createAttributeTimestamp("time:timestamp", d, null);

            XAttributeLiteral resource = factory.createAttributeLiteral("org:resource", event[4].trim(), null);
            XAttributeDiscrete cost = factory.createAttributeDiscrete("cost", Long.parseLong(event[5].trim()), null);
            XAttributeMap xmap = factory.createAttributeMap();
            xmap.put(String.valueOf(xKeyMap++), eventID);
            xmap.put(String.valueOf(xKeyMap++), activityName);
            xmap.put(String.valueOf(xKeyMap++), timestamp);
            xmap.put(String.valueOf(xKeyMap++), resource);
            xmap.put(String.valueOf(xKeyMap++), cost);

            XEvent e = factory.createEvent(xmap);//Create event
            trace.add(e); //Add event to trace
        }

        log.add(trace); //add last trace


        //Create XES file from XLog
        OutputStream outputStream = new FileOutputStream(outputFileName);
        new XesXmlSerializer().serialize(log, outputStream);
        outputStream.close();
    }


}
