import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import LogAnalyzer.*;
public class Main {

    public static void main(String[] args) {
        //C:\ProgramData\MySQL\MySQL Server 5.7\Data
        Path path = Paths.get("C:\\ProgramData\\MySQL\\MySQL Server 5.7\\Data\\DESKTOP-K2AVIBS.log");
        //Map<LogStructure,Integer> obj= new HashMap<>();
        try (BufferedReader in = Files.newBufferedReader(path)) {
            String str;
            //System.out.println("Read Successful");
            for(;;)
            {
                while((str=in.readLine())!=null)
                {
                    new Analyze(str);
                    str=in.readLine();
                }
                //Thread.wait(300);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
