package Manager.ConfigurationReader;
import Mission.Mission;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class ConfigurationReader {
    Scanner scanner;
    String filePath;
    ArrayList<Mission> MissionList = new ArrayList<Mission>();
    public ConfigurationReader(String filePath){
        this.filePath = filePath;
        OpenFile();
        ReadFile();
        CloseFile();
    }
    public void OpenFile() {
        try{
            scanner = new Scanner(new File(filePath));
        }catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println("Dosya Bulunamadi");
        }
    }
    public void ReadFile(){
        if(scanner != null) {
            while (scanner.hasNext()){
                float latl = Float.parseFloat(scanner.next());
                float longl = Float.parseFloat(scanner.next());
                float latr = Float.parseFloat(scanner.next());
                float longr = Float.parseFloat(scanner.next());
                boolean type = Boolean.parseBoolean(scanner.next());
                int target_type = Integer.parseInt(scanner.next());
                float location[] = {latl,longl};
                float rp[] = {latr, longr};
                MissionList.add(new Mission(location, rp,type,target_type));
            }
        }
    }
    public void CloseFile(){
        scanner.close();
    }
    public ArrayList<Mission> getMissionList(){return MissionList;}
}
