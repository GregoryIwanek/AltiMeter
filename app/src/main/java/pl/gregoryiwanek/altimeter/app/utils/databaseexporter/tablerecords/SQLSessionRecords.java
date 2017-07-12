package pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablerecords;

import java.util.ArrayList;
import java.util.List;

public class SQLSessionRecords {

    private final List<SQLRecordRow> recordRows;

    public SQLSessionRecords(List<String[]> tableRows) {
        recordRows = new ArrayList<>();
        populateRecordRows(tableRows);
    }

    private void populateRecordRows(List<String[]> tableRows) {
        for (String[] rowArgs : tableRows) {
            recordRows.add(buildRowInfo(rowArgs));
        }
    }

    private SQLRecordRow buildRowInfo(String[] rowArgs) {
        System.out.println("Creating a row to export");
        System.out.println("id: " + rowArgs[0]);
        System.out.println("latitude: " + rowArgs[1]);
        System.out.println("longitude: " + rowArgs[2]);
        System.out.println("altitude: " + rowArgs[3]);
        System.out.println("date: " + rowArgs[4]);
        System.out.println("address: " + rowArgs[5]);
        System.out.println("distance: " + rowArgs[6]);

        return new SQLRecordRow.Builder(
                rowArgs[0])
                .setLatitude(rowArgs[1])
                .setLongitude(rowArgs[2])
                .setAltitude(rowArgs[3])
                .setDate(rowArgs[4])
                .setAddress(rowArgs[5])
                .setDistance(rowArgs[6])
                .build();
    }

    public List<SQLRecordRow> getRecordRows() {
        return recordRows;
    }
}