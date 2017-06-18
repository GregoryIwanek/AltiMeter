package pl.gregoryiwanek.altimeter.app.utils;

public abstract class VersionController {

    public static boolean isFreeVersion(String packageName) {
        return packageName.contains(Constants.FREE_VERSION);
    }
}
