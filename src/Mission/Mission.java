package Mission;
import Missiles.Missile;


public class Mission {
    final private boolean type;
    final private float distance;
    final private int target_type;
    private String best_missile;
    private final float[] location;
    private final float[] rp;
    private static int counter = 0;
    private final int id;
    private Missile missile;
    public boolean show_rp = true;

    static public double toRad(float degree)
    {
        return ((degree/180)*Math.PI);
    }
    static public float distanceCalculator(float[] location, float[] rp){
        float distance_;
        distance_ = (float)(Math.sin(toRad(location[0]))*Math.sin(toRad(rp[0])) + Math.cos(toRad(location[0]))*Math.cos(toRad(rp[0]))*Math.cos(toRad(location[1]-rp[1])));
        distance_ = (float)(6371*Math.acos(distance_));
        return distance_;
    }
    public Mission(float [] location, float[] rp, boolean type, int target_type){
        this.location = location;
        this.rp = rp;
        this.type = type;
        this.target_type = target_type;
        distance = distanceCalculator(location,rp);
        counter++;
        id = counter;
        if(!type)
        {
            if(distance < 8)
            {
                best_missile = "Bozok";
            }
            else if(distance > 8 && distance < 25)
            {
                best_missile = "HGK-82";
            }
            else if(distance < 28)
            {
                best_missile = "HGK-83";
                if(target_type == 0) // Araba Hareketli
                    best_missile = "HGK-84";
            }
            else if(distance < 111){
                best_missile = "KGK-82";
                if(target_type == 2) // Sığınaksa KGK-83
                    best_missile = "KGK-83";
            }
            else if(distance <= 250) {
                best_missile = "SOM-B1";
                if (target_type == 2)//beton
                    best_missile = "SOM-B2";
                else if(target_type == 0){//hareketli araba
                    best_missile = "SOM-J";
                }
            }
        }
        else{
            if(distance <= 25)
                best_missile = "Bozdogan";
            else if(distance <= 65)
                best_missile = "Gokdogan";
        }
    }
    public float[] getLocation(){return location;}
    public float[] getRp(){return rp;}
    public boolean getType(){return type;}
    public float getDistance(){return distance;}
    public String getBest_missile(){return best_missile;}
    public void setBest_missile(String newMissile){
        this.best_missile = newMissile;
    }
    public int getId(){return id;}
    public int compareTo(Mission other){
        float[] origin = {0.0f, 0.0f};
        int compareRp = (int)(distanceCalculator(this.rp,origin) - distanceCalculator(other.rp, origin));
        return compareRp;
    }
    public void LoadMissile(Missile in_missile){
        missile = in_missile;
    }
    public Missile getMissile(){return missile;}
    public int getTarget_type(){return target_type;}
    public void IncrementLocation(float lat, float long_){
        location[0]+=lat;
        location[1]+=long_;
    }
}
