package Manager;
import Manager.ConfigurationReader.ConfigurationReader;
import Missiles.HavaHava.Goktug.*;
import Missiles.HavaYer.Kit.Hgk.*;
import Missiles.HavaYer.Kit.Kgk.*;
import Missiles.HavaYer.Seyir.Som.*;
import Missiles.Missile;
import Mission.Mission;
import Planes.Fighting.*;
import Planes.Iha.Akinci;
import Planes.Plane;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.Location;
import java.sql.SQLOutput;
import java.util.*;

public class Manager {
    private ArrayList<Mission> missions;
    private ArrayList<Missile> missiles = new ArrayList<Missile>();
    private ArrayList<Plane> planes = new ArrayList<Plane>(5);
    ConfigurationReader fileReader;
    public float LongestLat = 0;
    public float LowestLat = 100;
    public float LongestLong = 0;
    public float LowestLong = 100;
    public boolean two_akinci = false;
    public ArrayList<Plane> PlannedPlanes = new ArrayList<>();
    public Manager(String path){
        for(int i = 0 ; i < 10; i++)
        {
            planes.add(new F16());
            planes.add(new F4());
            planes.add(new Akinci());
        }
        for(int i = 0; i < 20; i++) {
            missiles.add(new HGK82());
            missiles.add(new HGK83());
            missiles.add(new HGK84());
            missiles.add(new KGK82());
            missiles.add(new KGK83());
            missiles.add(new SOM_A());
            missiles.add(new SOM_B1());
            missiles.add(new SOM_B2());
            missiles.add(new SOM_J());
            missiles.add(new Gokdogan());
            missiles.add(new Bozdogan());
        }
        fileReader = new ConfigurationReader(path);
        missions = fileReader.getMissionList();
    }
    public void PrintPlanes(){
        for(int i = 0 ; i < planes.size(); i++)
        {
            Plane plane = planes.get(i);
            System.out.println("id: " + plane.getId() + " " + plane.getName());
        }
    }
    public void PrintMissiles(){
        for(int i = 0 ; i < missiles.size(); i++){
            Missile missile = missiles.get(i);
            System.out.println(" id: " + missile.getId() + " " + missile.getName());
        }
    }
    public void PrintMissions(){
        for(int i = 0 ; i < missions.size(); i++){
            Mission mission = missions.get(i);
            float[] location = mission.getLocation();
            float[] rp = mission.getRp();
            String Type = "Yer";
            if(mission.getType())
                Type = "Hava";
            String target_type = "";
            if(Type == "Yer") {
                if (mission.getTarget_type() == 1)
                    target_type = "Bina";
                else if (mission.getTarget_type() == 2)
                    target_type = "Sığınak";
                else
                    target_type = "Araba";
            }
            System.out.println("id:" + mission.getId() + ", Target: " + location[0] + " " + location[1]
            + ", Rp: " + rp[0] + " " + rp[1] + ", " + Type + ", " + target_type +  ", Missile: " + mission.getBest_missile() + ", Distance: " + mission.getDistance());
        }
    }
    public void PrintMission(@NotNull Mission mission){
        System.out.println("Mission_Id: " + mission.getId() + ", Target: " + mission.getLocation()[0] + " " + mission.getLocation()[1]
        + ", Rp: " + mission.getRp()[0] + " " + mission.getRp()[1] + ", " + mission.getMissile().getName() + ", ID: "+ mission.getMissile().getId());
    }
    public void PrintPlane(Plane plane){
        System.out.println("Plane Id: " + plane.getId() + ", " + plane.getName() + "\n" + "*******************Missions*****************");
        for(int j = 0 ; j < plane.full; j++){
            PrintMission(plane.getMissions()[j]);
        }
        System.out.println("--------------------------------");
    }
    public int FindProperMissile(String missile_name){
        int index = 0;
        for(; index < missiles.size(); index++ ){
            if(missiles.get(index).getName() == missile_name)
                break;
        }
        return index;
    }
    public int FindProperPlane(float weight, boolean hava_hava, boolean four, float total_distance){
        String name;
        if(four) {
            if (hava_hava)
                name = "F-16";
            else {
                if (weight > 2113) {
                    name = "F-16";
                } else {
                    name = "F-4";
                }
            }
        }
        else{
            if(weight > 1200)
                name = "F-4";
            else if(weight > 240)
                name = "Akinci";
            else
                name = "F-16";
        }
        if(name == "F-4" && total_distance > 2816){
            name = "F-16";
        }
        if(name == "F-16" && total_distance > 4220 ){
            if(!hava_hava) {
                two_akinci = true;
            }
        }
        int index = 0;
        for(; index < planes.size(); index++){
            Plane plane = planes.get(index);
            if(plane.getName() == name)
                break;
        }
        return index;
    }
    public void Missile2Mission(int mission_index){
        Mission mission = missions.get(mission_index);
        int missile_index = FindProperMissile(mission.getBest_missile());
        mission.LoadMissile(missiles.get(missile_index));
        missiles.remove(missile_index);
    }
    public void PlanMissions(){
        int n = missions.size();
        for(int i = 0 ; i < n-1; i++)
        {
            int min_idx = i;
            for(int j = i+1 ; j < n; j++)
            {
                if(missions.get(j).compareTo(missions.get(min_idx)) < 0)
                    min_idx = j;
            }
            missions.add(i,missions.get(min_idx));
            missions.remove(min_idx + 1);
        }
        for(int i = 0; i < n ; i+=2){
            int j = i + 1;
            for(; j < n-1; j++){
                if(missions.get(i).getBest_missile() == missions.get(j).getBest_missile())
                    break;
            }
            if((j != i + 1))
            {
                missions.add(i+1, missions.get(j));
                missions.remove(j+1);
            }
        }
        for(int i = 0 ; i < n ; i++){
            Missile2Mission(i);
        }
        while(0 < missions.size()) {
            int i = 0;
            int count = 0;
            boolean four = true;
            boolean hava_hava = false;
            float total_weight = 0;
            float total_distance = 0;
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            if(missions.size() < 4){
                for(; i < missions.size(); i++) {
                    total_weight += missions.get(i).getMissile().getWeight();
                    indexes.add(i);
                }
                four = false;
            }
            else {
                while (count < 4) {
                    Missile missile = missions.get(i).getMissile();
                    if (missile.getType()) {
                        if (!hava_hava) {
                            hava_hava = true;
                            indexes.add(i);
                            indexes.add(i + 1);
                            i += 2;
                            if (missions.size() - i <= 0) {
                                break;
                            }
                        } else
                            i += 2;
                    } else {
                        total_weight += missile.getWeight();
                        count++;
                        indexes.add(i);
                        i++;
                    }
                    if(i >= missions.size())
                        break;
                }
            }
            for(int a = 0; a < indexes.size(); a++){
                if(a == 0 || a == indexes.size()-1) {
                    float[] origin = {39.5f, 32.5f};
                    total_distance += Mission.distanceCalculator(origin,missions.get(indexes.get(a)).getRp());
                }
                else
                    total_distance += Mission.distanceCalculator(missions.get(indexes.get(a)).getRp(),missions.get(indexes.get(a-1)).getRp());
            }
            int plane_index = FindProperPlane(total_weight, hava_hava, four,total_distance);
            if(!two_akinci) {
                for (int a = 0; a < indexes.size(); a++) {
                    planes.get(plane_index).LoadMission(missions.get(indexes.get(a) - a));
                    missions.remove(indexes.get(a) - a);
                }
                planes.get(plane_index).OrderMissions();
                PlannedPlanes.add(planes.get(plane_index));
                planes.remove(plane_index);
            }
            else{
                for(int b = 0 ; b < 2; b++) {
                    plane_index = FindProperPlane(100, false, false, 0);
                    for (int a = 0; a < 2; a++) {
                        planes.get(plane_index).LoadMission(missions.get(indexes.get(a) - a));
                        missions.remove(indexes.get(a) - a);
                    }
                    planes.get(plane_index).OrderMissions();
                    PlannedPlanes.add(planes.get(plane_index));
                    planes.remove(plane_index);
                }
                two_akinci = false;
            }
        }
    }
    public void ShowPlan(){
        for(int i = 0 ; i < PlannedPlanes.size(); i++){
            PrintPlane(PlannedPlanes.get(i));
        }
    }
    public void AddMission(Mission new_mission){missions.add(new_mission);}
    public void AddMissile(Missile new_missile){missiles.add(new_missile);}
    public void AddPlane(Plane new_plane){planes.add(new_plane);}
    public void changeMissileofMission(){
        PrintMissions();
        Scanner scanner = new Scanner(System.in);
        float[] location = new float[2];
        float[] rp = new float[2];
        String best_missile;
        System.out.println("Enter the Target Lat/Lon: ");
        location[0] = scanner.nextFloat();
        location[1] = scanner.nextFloat();
        System.out.println("Enter the Rp Lat/Lon: ");
        rp[0] = scanner.nextFloat();
        rp[1] = scanner.nextFloat();
        System.out.println("Which missile do you prefer for the mission: ");
        best_missile = scanner.next();
        for(int i = 0; i < missions.size(); i++){
            Mission mission = missions.get(i);
            if(mission.getLocation()[0] == location[0] && mission.getLocation()[1] == location[1] &&
                    mission.getRp()[0] == rp[0] && mission.getRp()[1] == rp[1]){
                mission.setBest_missile(best_missile);
            }
        }
    }
    public void setLongestes(){
        for(int i = 0 ; i < missions.size(); i++){
            float temp_max;
            float temp_low;
            float temp_long_max;
            float temp_long_min;
            if(missions.get(i).getLocation()[1] > missions.get(i).getRp()[1]){
                temp_long_max = missions.get(i).getLocation()[1];
                temp_long_min = missions.get(i).getRp()[1];
            }
            else{
                temp_long_max =missions.get(i).getRp()[1];
                temp_long_min = missions.get(i).getLocation()[1];
            }
            if(LongestLong < temp_long_max){
                LongestLong = temp_long_max;
            }
            if(LowestLong > temp_long_min){
                LowestLong = temp_long_min;
            }
            if(missions.get(i).getLocation()[0] > missions.get(i).getRp()[0]){
                temp_max = missions.get(i).getLocation()[0];
                temp_low = missions.get(i).getRp()[0];
            }
            else{
                temp_max =missions.get(i).getRp()[0];
                temp_low = missions.get(i).getLocation()[0];
            }
            if(LongestLat < temp_max){
                LongestLat = temp_max;
            }
            if(LowestLat > temp_low){
                LowestLat = temp_low;
            }
        }
        if(LongestLat < 39.5)
            LongestLat = 39.5f;
        if(LowestLat > 39.5)
            LowestLat = 39.5f;
        if(LongestLong < 32.5)
            LongestLong = 32.5f;
        if(LowestLong > 32.5)
            LowestLong = 32.5f;
    }
}
