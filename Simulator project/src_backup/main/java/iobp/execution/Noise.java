package iobp.execution;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Noise {
    public Noise(){

    }

    public EventFromTrigger noiseOnEvent(EventFromTrigger e){
        Random rand = new Random();
        int x = rand.nextInt(3); // [0, 3)
        if(x == 0){ //MISSING TIMESTAMP
            e.timestamp = noiseOnTimestamp();
            //System.out.println("\t\tNoise. caseId empty");
            System.out.print("-M");
        }else if(x == 1){ //MISSING TASK NAME
            e.taskName = noiseOnTaskName();
            //System.out.println("\t\tNoise. taskName empty");
            System.out.print("-M");
        }else if(x == 2){ //NOISE ON TIMESTAMP
            e.timestamp = noiseOnTimestamp(e.timestamp);
            //System.out.println("\t\tNoise. timestamp");
            System.out.print("-T");
        }
        return e;
    }


    //MISSING COLUMN
    public String noiseOnTaskName(){
        return "";
    }
    public String noiseOnTimestamp(){
        return "";
    }

    //Change granularity
    public String noiseOnTimestamp(String timestamp){
        Random rand = new Random();
        int x = rand.nextInt(5); // [0, 5)
        Date date = new Date(timestamp); //timestamp format EXPECTED "MM/dd/yyyy HH:mm:ss"
        DateFormat dateformat = null;
        String ts = "";
        if(x == 0){ //Change DATE_TIME to ONLY DATE granularity
            dateformat = new SimpleDateFormat("MM/dd/yyyy");
        }else if(x == 1){ //Change 'MM/dd/yyyy HH:mm:ss' to 'MM-dd-yyyy HH:mm:ss'
            dateformat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        }else if(x == 2){ //Change DATE_TIME to only HOURS granularity
            dateformat = new SimpleDateFormat("HH:mm:ss");
        }else if(x == 3){ //Change DATE_TIME to only HOURS granularity with separator .
            dateformat = new SimpleDateFormat("HH.mm.ss");
        }else if(x==4){ //Change all separators
            dateformat = new SimpleDateFormat("MM-dd-yyyy HH.mm.ss");
        }
        ts = dateformat.format(date); //Get timestamp
        return ts; //Return only date
    }

    //DUPLICATE EVENT, EXTERNAL NOISE, ON EXECUTE TASK ORGANIZATION

    //FORM-BASED EVENT CAPTURE PROBLEM, EXTERNAL NOISE, ON EXECUTE TASK ORGANIZATION



}
