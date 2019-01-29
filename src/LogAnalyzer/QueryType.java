package LogAnalyzer;

public enum QueryType {
    DML,DDL,CONNECTION, SQLI,INITDB
    //INITDB includes show tables, select database for simplification purposes
    // DML Contains Select, Insert, Update, Delete
    //DDL Contains Create, Drop, Alter, Truncate
}
