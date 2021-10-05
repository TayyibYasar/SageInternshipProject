package Planes;

import Missiles.Missile;
import Mission.Mission;
import com.company.Notification;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plane {
    final private String name;
    final private float distance;
    private static int counter = 0;
    final private int id, station;
    final private float speed;
    final private Mission[] missions;
    private float traveled_distance = 0;
    public int full;
    public float longx = 32.5f;
    public float laty = 39.5f;
    public double rotaX, rotaY;
    public int rota_count = 0;
    public int missile_count = 0;
    public boolean stop = true;
    public Color color;
    public float Collision_Carpan;
    public Plane(String name, float distance, int station, float speed){
        this.speed = speed;
        this.name = name;
        this.distance = distance;
        this.station = station;
        missions = new Mission[station];
        this.counter++;
        this.id = counter;
        full = 0;
        Collision_Carpan = (float)(speed/0.05)*(1.2f);
    }
    public String getName(){return this.name;}
    public float getDistance(){return this.distance;}
    public int getId(){return this.id;}
    public void LoadMission(Mission new_mission){
        if(full < station){
            missions[full] = new_mission;
            full++;
        }
    }
    public Mission[] getMissions(){return missions;}
    public void CalculateRota(float unitY, float unitX){
        float[] rp;
        if(rota_count >= full) {
            rota_count = full-2;
            rp = new float[2];
            rp[0] = 39.5f;
            rp[1] = 32.5f;
        }
        else
            rp = missions[rota_count].getRp();
        rotaY = Math.abs(((laty-rp[0])/unitY)/((longx-rp[1])/unitX));
        rotaX = 1;
        float[] x = {laty,longx};
        traveled_distance += Mission.distanceCalculator(rp,x);
        if(Double.isNaN(rotaY)) {
            rotaY = 0;
            rotaX = 0;
        }
        if(laty-rp[0] < 0)
            rotaY *= -1;
        if(longx-rp[1] < 0)
            rotaX *= -1;
    }
    public boolean CheckCollision(float unitY, float unitX){
       float[] rp = missions[rota_count].getRp();
        if(rota_count >= full-2){
            if(Math.abs(this.laty-39.5) < unitY*Collision_Carpan && Math.abs(this.longx-32.5) < unitX*Collision_Carpan){
                rotaX = 0;
                rotaY = 0;
                stop = true;
                //Notification notification = new Notification(this);
            }
        }
       if(Math.abs(this.laty-rp[0]) < unitY*Collision_Carpan && Math.abs(this.longx-rp[1]) < unitX*Collision_Carpan){
           laty = rp[0];
           longx = rp[1];
           rota_count+=2;
           CalculateRota(unitY,unitX);
           return true;
       }
       return false;
    }
    public Missile[] FireMissile(float unitY, float unitX){
        Missile[] missiled = new Missile[2];
        missiled[0] = missions[missile_count].getMissile();
        missiled[1] = missions[missile_count+1].getMissile();
        missiled[0].Fire(laty,longx,missions[missile_count].getLocation(),unitY,unitX);
        missiled[1].Fire(laty,longx,missions[missile_count+1].getLocation(),unitY,unitX);
        missions[missile_count].show_rp = false;
        missions[missile_count+1].show_rp = false;
        missile_count+=2;
        return missiled;
    }
    public int getStation(){return station;}
    public float getSpeed(){return speed;}
    public float getTraveled_distance(){return traveled_distance;}
    public void OrderMissions(){
        if(full > 4) {
            float[] origin = {39.5f, 32.5f};
            for(int i = 0 ; i < full; i+=2){
                int max_index = i;
                float[] max_rp = missions[i].getRp();
                for(int j = i+2; j < full; j+=2){
                    float[] j_rp = missions[j].getRp();
                    if(Mission.distanceCalculator(origin,max_rp) < Mission.distanceCalculator(origin,j_rp)){
                        max_index = j;
                        max_rp = j_rp;
                    }
                }
                if(max_index > i){
                    Mission temp =missions[i];
                    Mission temp_ = missions[i+1];
                    missions[i] = missions[max_index];
                    missions[i+1] = missions[max_index+1];
                    missions[max_index] = temp;
                    missions[max_index+1] = temp_;
                }
            }
        }
    }
}
