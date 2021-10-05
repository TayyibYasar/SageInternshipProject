package com.company;


import Missiles.Missile;
import Mission.Mission;
import Planes.Plane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game implements Runnable{
    private ArrayList<Plane> planes;
    private ArrayList<Missile> missiles = new ArrayList<Missile>();
    private float[] Borders;
    private float unitX;
    private float unitY;
    private int finish_calculator = 0;
    private Display display;
    public int width, height;
    public String title;
    private boolean running = false;
    private Thread thread;
    private BufferStrategy bs;
    private Graphics g;
    private ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.BLUE,Color.GRAY,Color.MAGENTA,Color.ORANGE,Color.BLACK,
            Color.PINK,Color.RED,Color.LIGHT_GRAY,Color.yellow));
    private int color_index = 0;

    public Game(String title, int width, int height,ArrayList<Plane> planes, float []Borders) throws IOException {
        this.width = width;
        this.height = height;
        this.title = title;
        Random rand = new Random();
        this.planes = planes;
        this.Borders = Borders;
        unitY = (Borders[0]-Borders[1])/700;
        unitX = (Borders[2]-Borders[3])/1200;
        for(int i = 0 ; i < planes.size(); i++) {
            Plane plane = planes.get(i);
            plane.CalculateRota(unitY, unitX);
            plane.color = colors.get(color_index);
            color_index++;
            if(color_index >= colors.size())
                color_index = 0;
            plane.stop = false;
        }
        init();
    }
    public double LatToYCalculator(float lat){
        return ((Borders[0]-lat)/unitY);
    }
    public double LongToXCalculator(float long_){
        return ((long_-Borders[3])/unitX);
    }
    public void screenShoot() throws AWTException, IOException {
        System.out.println("aaa");
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
        ImageIO.write(capture, "bmp", new File("Abcdef.png"));
    }
    private void init() throws IOException {

        display = new Display(title, width, height);
    }

    private void tick(){
        for(int i = 0 ; i < planes.size(); i++) {
            Plane plane = planes.get(i);
            double a_y = unitY * plane.rotaY;
            double a_x = unitX * plane.rotaX;
            double carpan = plane.getSpeed()/(Math.abs(a_y) + Math.abs(a_x));
            if(Double.isInfinite(carpan))
                carpan = 1;
            if (!plane.stop) {
                plane.laty -= a_y*carpan*1.2;
                plane.longx -= a_x*carpan*1.2;
                if (plane.CheckCollision(unitY, unitX)){
                    Missile[] missile = plane.FireMissile(unitY, unitX);
                    missiles.add(missile[0]);
                    missiles.add(missile[1]);
                }
            }
        }
        for(int i = 0 ; i < missiles.size(); i++){
            Missile missile = missiles.get(i);
            if(!missile.stop){
                missile.laty -= unitY*missile.rotaY;
                missile.longx -= unitX*missile.rotaX;
                if(missile.CheckCollision(unitY,unitX)){
                    //Notification notification = new Notification(missile, "Füze hedefe ulaşti");
                    missiles.remove(i);
                    i--;
                };
            }
        }
    }

    private void render() throws IOException, AWTException {
        bs = display.frame.getBufferStrategy();
        if(bs == null){
            display.frame.createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        //Clear Screen
        g.clearRect(0,0,1500,900);
        //Draw Here!
        finish_calculator = 0;
        g.setColor(Color.red);
        for(int i = 0; i < planes.size();i++) {
            Plane plane = planes.get(i);
            Mission[] missions = plane.getMissions();
            for(int j = 0; j < plane.full; j++) {
                if(missions[j].getMissile().stop) {//Füzesi durduysa mission'ı çizmiyor
                    continue;
                }
                if(missions[j].getType() && missions[j].getMissile().rotaY != 0){
                    missions[j].IncrementLocation(-(float)(missions[j].getMissile().rotaY)/60,-(float)(missions[j].getMissile().rotaX)/60);
                    missions[j].getMissile().target = missions[j].getLocation();
                    missions[j].getMissile().CalculateRota(missions[j].getLocation(),unitY,unitX);
                }
                g.setColor(plane.color);
                float[] loc = missions[j].getLocation();
                float[] rp = missions[j].getRp();
                Ellipse2D.Double rect = new Ellipse2D.Double(LongToXCalculator(loc[1]), LatToYCalculator(loc[0]), 10, 10);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.fill(rect);
                if(missions[j].show_rp){
                    Rectangle2D.Double rect_ = new Rectangle2D.Double(LongToXCalculator(rp[1]), LatToYCalculator(rp[0]), 10, 10);
                    g2d.draw(rect_);
                }
            }
        }
        for(int i = 0 ; i < missiles.size(); i++){
            Missile missile = missiles.get(i);
            double x = LongToXCalculator(missile.longx);
            double y = LatToYCalculator(missile.laty);
            Rectangle2D.Double rect = new Rectangle2D.Double(x, y, 5, 5);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.green);
            g2d.fill(rect);
            g2d.drawString(missile.getName(),(float)(x),(float)(y));
        }
        Rectangle2D.Double rect = new Rectangle2D.Double(LongToXCalculator(32.5f), LatToYCalculator(39.5f), 20, 20);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.BLACK);
        g2d.drawString("Base",(int)(LongToXCalculator(32.5f)),(int)(LatToYCalculator(39.5f) - 5));
        g2d.fill(rect);
        float text_location = 700/planes.size();
        for(int i = 0 ; i < planes.size(); i++) {
            Plane plane = planes.get(i);
            if(plane.stop) {
                finish_calculator++;
                continue;
            }
            g2d.setColor(plane.color);
            double x = LongToXCalculator(plane.longx);
            double y = LatToYCalculator(plane.laty);
            rect = new Rectangle2D.Double(x, y, 10, 10);
            if(plane.missile_count != plane.full)
                g2d.fill(rect);
            else
                g2d.draw(rect);
            String text = plane.getName() + "(id: " + plane.getId() + ")" ;
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            g2d.drawString(text, (float)(x), (float)(y));
        }
        for(int i = 0; i < planes.size(); i++){
            Plane plane = planes.get(i);
            g2d.setColor(plane.color);
            String text = plane.getId() + " " + plane.getName();
            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            g2d.drawString(text, 1350, 100 + text_location * i);
            if(plane.stop){
                g2d.drawString("Alınan Mesafe: " + plane.getTraveled_distance(), 1350, 120 + text_location*i);
            }
            else {
                for (int j = plane.missile_count; j < plane.full; j++) {
                    Mission mission = plane.getMissions()[j];
                    String text_ = mission.getId() + " " + mission.getBest_missile();
                    g2d.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                    g2d.drawString(text_, 1350, 110 + text_location * i + 10 * j);
                }
            }
        }
        if(finish_calculator == planes.size()) {
            //finish
        }
        if(display.stop) {
            //screenShoot();
            stop();
        }
        //End Drawing!
        bs.show();
        g.dispose();
    }

    public void run(){
        /*try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        int fps = 20;
        double timePerTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;
        while(running){
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            timer += now - lastTime;
            lastTime = now;

            if(delta >= 1){
                tick();
                try {
                    render();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                ticks++;
                delta--;
            }

            if(timer >= 1000000000){
                ticks = 0;
                timer = 0;
            }
        }
        while(!running) {
            stop();
            if(running)
                run();
        }
    }

    public synchronized void start(){
        if(running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop(){
        if(!running) {
            if(!display.stop){
                running = true;
            }
            return;
        }
        running = false;
        /*try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

}