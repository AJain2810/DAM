package LogAnalyzer;


public class LogStructure {
    public String date;
    public LogAnalyzer.QueryType queryType;
    public int count=0;
    public LogAnalyzer.Time time;

    public LogStructure(String date, QueryType qtype, int count, Time time )
    {
        this.date=date;
        this.queryType=qtype;
        this.time=time;
        this.count=count;
    }

    public LogStructure()
    {
        this.date="";
        this.count=0;
    }
}
