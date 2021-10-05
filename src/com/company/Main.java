package com.company;


import Manager.Manager;

import java.io.IOException;


public class Main{
    public static void main(String[] args) throws IOException {
        Manager manager = new Manager("C:\\Users\\tago1\\Desktop\\Tayyib\\Ben\\Targets.txt");
        manager.PrintMissions();
        manager.setLongestes();
        manager.PlanMissions();
        manager.ShowPlan();
        float[] Borders = {manager.LongestLat + 1,manager.LowestLat - 1,manager.LongestLong + 1,manager.LowestLong - 1};
        Game game = new Game("Animasyon",1000,700,manager.PlannedPlanes,Borders);
        game.start();
    }
}
