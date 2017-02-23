package com.example.sanderbrugge.publictransportassistant.persistentie;

import com.example.sanderbrugge.publictransportassistant.model.Stop;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by sanderbrugge on 23/02/17.
 */

public class BusMapper {
    private ArrayList<Stop> Stopsen;
    private static final String TAG ="Stopmapper";
    public BusMapper() {
        Stopsen = new ArrayList<>();
       // initStopsen();
    }


    /*public void initStopsen(){
        Stopsen.add(new Stop(new Date(),1,1,1,"testhalte","testLocatie"));
        Stopsen.add(new Stop(new Date(),2,2,2,"testhalte","testLocatie"));
        Stopsen.add(new Stop(new Date(),3,3,3,"testhalte","testLocatie"));
        Stopsen.add(new Stop(new Date(),4,4,4,"testhalte","testLocatie"));


    }*/
    public ArrayList<Stop> getBussen(){
        return Stopsen;
    }
}
