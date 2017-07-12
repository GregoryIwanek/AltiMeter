package pl.gregoryiwanek.altimeter.app.utils.databaseexporter;

import java.util.List;

import pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablerecords.SQLSessionRecords;
import pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablesession.SQLSessionInfo;

public class SQLSessionContent {

    private SQLSessionInfo sessionInfo;
    private SQLSessionRecords records;

    public SQLSessionContent(String[] sessionInfo, List<String[]> recordRows) {
        setSessionInfo(sessionInfo);
        setRecords(recordRows);
    }

    public SQLSessionContent(SQLSessionInfo sessionInfo, SQLSessionRecords records) {
        this.sessionInfo = sessionInfo;
        this.records = records;
    }

    private void setSessionInfo(String[] args) {
        sessionInfo = new SQLSessionInfo(args);
    }

    private void setRecords(List<String[]> recordRows) {
        records = new SQLSessionRecords(recordRows);
    }

    public SQLSessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public SQLSessionRecords getRecords() {
        return records;
    }
}
