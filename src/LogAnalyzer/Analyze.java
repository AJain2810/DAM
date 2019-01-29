package LogAnalyzer;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyze {
    String str;
    Time laststore= Time.PM;
    ArrayList<LogStructure> obj= new ArrayList<>();
    public Analyze(String str)
    {
        this.str=str;
        //System.out.println("Analysis begins");
        this.parseLog();
    }
    public Analyze()
    {
        this.str="";
    }

    public void parseLog()
    {
        //System.out.println("Parsing begins");
        String date, time;
        QueryType var = null;
        String type1;
        if(str.startsWith("2018"))
        {
            date=str.substring(0,10);
            time=str.substring(11,25);
            int index=str.indexOf("\t   ");
            //System.out.println(index);
            str=str.substring(index+5);
            //Gets the string after the first column ends
            //System.out.println(str);
            index=str.indexOf(" ");
            int index2=str.indexOf("\t");
            if(str.contains("Connect"))
            {

            }
            //else {
                //System.out.println(str);
                type1 = str.substring(index + 1, index2);
                //System.out.println("Type 1" + type1);
                // If type 1 is connect or quit or initdb modify appropriately
                str = str.substring(index2 + 1);
                str = str.toUpperCase();

                this.checkForSqlInjection(str, var);
                if (var == QueryType.SQLI)
                    this.showDialog();
                var=this.statementParser(str, var);

                this.storeInSet(date, time, var, type1);
                this.writeToFile();
        //    }
        }

    }

    private void showDialog()
    {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("img.png");
        TrayIcon trayIcon=new TrayIcon(image, "SQLI Alert");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        trayIcon.displayMessage("SQLI Alert", "Someone is trying to perform SQLI on your database", TrayIcon.MessageType.WARNING);

    }

    private void storeInSet(String date, String time, QueryType qtype, String type1) {
        int flag = 0;
        Time t = null;
        if (Integer.parseInt(time.substring(0, 2)) >= 12)
            t = Time.PM;
        else
            t = Time.AM;

        if (type1.equalsIgnoreCase("Connect")) {
            for (LogStructure lg : obj) {
                if (lg.date.equalsIgnoreCase(date) && lg.time == t && lg.queryType == QueryType.CONNECTION) {
                    lg.count++;
                    flag = 1;
                }
                if (flag == 0) {
                    obj.add(new LogStructure(date, QueryType.CONNECTION, 1, t));
                }
            }
        }

        else
        {
            for (LogStructure lg : obj) {
                if (lg.date.equalsIgnoreCase(date) && lg.time == t && lg.queryType == qtype) {
                    lg.count++;
                    flag = 1;
                }
            }
            if (flag == 0) {
                obj.add(new LogStructure(date, qtype, 1, t));
                //System.out.println(qtype);
            }
        }
    }
    private QueryType statementParser(String stri, QueryType var)
    {

        Pattern ptn=Pattern.compile("Select Database()",Pattern.CASE_INSENSITIVE);  //Matching for Select Database Statement
        Matcher mtch= ptn.matcher(stri);         //Matching the string
        if(mtch.find())
            var=QueryType.INITDB;
        if(stri.startsWith("INSERT INTO ") || stri.startsWith("UPDATE") || stri.startsWith("DELETE FROM"))
        {
            var=QueryType.DML;
            System.out.println("DML");
        }
        else if (stri.startsWith("CREATE TABLE") || stri.startsWith("DROP TABLE")||stri.startsWith("TRUNCATE TABLE")||stri.startsWith("ALTER TABLE"))
        {
            var=QueryType.DDL;
            System.out.println("DDL");
        }
        return var;
    }

    private void checkForSqlInjection(String string, QueryType qtype)
    {
        if(string.startsWith("SELECT"))
        {
            int var=string.indexOf("OR");
            if(var!=-1)
            {
                //System.out.println("Checking for SQL Injection");
                String temp=string.substring(var+3);
                //System.out.println(temp);
                String arr[]=temp.split(" ");
                //System.out.println(arr[0]+" "+arr[1]+" "+arr[2]);
                if(arr[0].compareToIgnoreCase(arr[2])==0)
                {

                    //generate event that possible sqlinjection has occured
                    this.showDialog();
                    System.out.println("This is a case of SQL Injection");
                    qtype=QueryType.SQLI;
                }

            }
        }
    }
    public void writeToFile()
    {
        String time= new Date().toString().substring(11,13);
        if(laststore==Time.AM)
        {
            //11,13
            if(Integer.parseInt(time)>=12)
            {
                Path path=Paths.get("\\ProgramData","MySQL","MySQL Server 5.7","Data","analysis.txt");
                //Written to file
                for(LogStructure ls:obj)
                {
                    //System.out.println(ls);
                    String str= "\n"+ls.date+"\t"+ls.time+"\t"+ls.queryType+"\t"+ls.count+"\n";
                    try {
                        Files.write(path, str.getBytes(), StandardOpenOption.APPEND);
                    }catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                }
                this.clearSet();
                this.laststore=Time.PM;
            }
        }

        if(laststore==Time.PM)
        {
            //11,13
            if(Integer.parseInt(time)<12)
            {
                Path path=Paths.get("\\ProgramData","MySQL","MySQL Server 5.7","Data","analysis.txt");
                for(LogStructure ls:obj)
                {
                    String str= "\n"+ls.date+"\t"+ls.time+"\t"+ls.queryType.toString()+"\t"+ls.count+"\n";
                    try {
                        Files.write(path, str.getBytes(), StandardOpenOption.APPEND);
                    }catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                }
                //Written to file
                this.clearSet();
                this.laststore=Time.AM;
            }
        }
    }

    private void clearSet()
    {
        int time= Integer.parseInt(new Date().toString().substring(11,13));

        for(LogStructure lg: obj)
        {
            if(lg.time==laststore)
            {
                obj.remove(lg);
            }
        }
    }
}
