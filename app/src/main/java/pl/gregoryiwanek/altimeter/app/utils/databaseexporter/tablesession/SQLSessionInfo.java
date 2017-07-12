package pl.gregoryiwanek.altimeter.app.utils.databaseexporter.tablesession;

public class SQLSessionInfo {

    private final SQLInfo info;

    public SQLSessionInfo(String[] infoArgs) {
        info = populateInfo(infoArgs);
    }

    private SQLInfo populateInfo(String[] args) {
        System.out.println("Creating a session info to export");
        System.out.println("id: " + args[0]);
        System.out.println("title: " + args[1]);
        System.out.println("description: " + args[2]);
        System.out.println("altitude: " + args[3]);
        System.out.println("maxheight: " + args[4]);
        System.out.println("minheight: " + args[5]);
        System.out.println("address: " + args[6]);
        System.out.println("distance: " + args[7]);

        return new SQLInfo.Builder(args[0])
                .title(args[1])
                .description(args[2])
                .altitude(args[3])
                .maxheight(args[4])
                .minheight(args[5])
                .address(args[6])
                .distance(args[7])
                .build();
    }

    public SQLInfo getInfo() {
        return info;
    }
}
