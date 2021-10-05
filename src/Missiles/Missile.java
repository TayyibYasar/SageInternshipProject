package Missiles;

public class Missile {
    final private String name;
    final private float range;
    final private float weight;
    final private boolean type;
    private static int counter = 0;
    private int id = 0;
    public float longx;
    public float laty;
    public float[] target;
    public boolean stop = false;
    public double rotaY = 0;
    public double rotaX = 0;
    public Missile(String name, float range, float weight,boolean type){
        this.name = name;
        this.range = range;
        this.weight = weight;
        this.type = type;
        counter++;
        id = counter;
    }
    public String getName(){return name;}
    public float getRange(){return range;}
    public float getWeight(){return weight;}
    public boolean getType(){return type;}
    public int getId(){return id;}
    public void Fire(float laty, float longx, float[] target, float unitY, float unitX){
        this.target = target;
        this.laty = laty;
        this.longx = longx;
        stop = false;
        CalculateRota(target, unitY,unitX);
    }
    public void CalculateRota(float[] target, float unitY, float unitX){
        rotaY = Math.abs(((laty-target[0])/unitY)/((longx-target[1])/unitX));
        rotaX = 1;
        if(Double.isInfinite(rotaY)){
            rotaY = Math.abs((laty-target[0])/unitY)/10;
            rotaX = 0;
        }
        if(Double.isNaN(rotaY)) {
            rotaY = 0;
            rotaX = 0;
        }
        if(laty-target[0] < 0)
            rotaY *= -1;
        if(longx-target[1] < 0)
            rotaX *= -1;
    }
    public boolean CheckCollision(float unitY, float unitX){
        if(Math.abs(this.laty-target[0]) < unitY*1.5 && Math.abs(this.longx-target[1]) < unitX*1.5) {
            rotaX = 0;
            rotaY = 0;
            stop = true;
            return true;
        }
        return false;
    }
}
