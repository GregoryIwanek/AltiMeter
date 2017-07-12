package pl.gregoryiwanek.altimeter.app.utils.databaseexporter;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablerecords.SQLRecordRow;

public class DatabaseExporter {

    private static final DatabaseExporter INSTANCE = new DatabaseExporter();

    private DatabaseExporter() {}

    public static DatabaseExporter getInstance() {
        return INSTANCE;
    }

    public void exportSessionDataAsFile(SQLSessionContent sessionContent) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "myfiletessst.txt");
        FileOutputStream stream = null;

        try {
            System.out.println("trying");
            StringBuilder builder = new StringBuilder();
            builder.append("THIS IS TEST STRING" + "\n");

            for (SQLRecordRow row : sessionContent.getRecords().getRecordRows()) {
                builder.append("id: ").append(row.getId()).append(";")
                        .append("latitude: ").append(row.getLatitude()).append(";")
                        .append("longitude: ").append(row.getLongitude()).append(";")
                        .append("altitude: ").append(row.getAltitude()).append(";")
                        .append("date: ").append(row.getDate()).append(";")
                        .append("address: ").append(row.getAddress()).append(";")
                        .append("distance: ").append(row.getDistance()).append(";")
                        .append("\n");
            }

            System.out.println("trying2");

            String testString = builder.toString();
            file.createNewFile();
            stream = new FileOutputStream(file);
            stream.write(testString.getBytes());

            System.out.println("trying3");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//    private void writeToFile(String data,Context context) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }

//    FileOutputStream fop = null;
//    File file;
//    String content = "This is the text content";
//
//		try {
//
//                file = new File("c:/newfile.txt");
//                fop = new FileOutputStream(file);
//
//                // if file doesnt exists, then create it
//                if (!file.exists()) {
//                file.createNewFile();
//                }
//
//                // get the content in bytes
//                byte[] contentInBytes = content.getBytes();
//
//                fop.write(contentInBytes);
//                fop.flush();
//                fop.close();
//
//                System.out.println("Done");
//
//                } catch (IOException e) {
//                e.printStackTrace();
//                } finally {
//                try {
//                if (fop != null) {
//                fop.close();
//                }
//                } catch (IOException e) {
//                e.printStackTrace();
//                }
//                }