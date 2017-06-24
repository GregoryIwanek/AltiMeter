package pl.gregoryiwanek.altimeter.app.utils;

public abstract class VersionController {

    /**
     * Check if version of the app is freeware or upgraded, paid version.
     * @param packageName package name of the place this method is called from;
     *                    free version should contain "free" and pro version "pro"
     *                    in the package name;
     * @return if version of the app is "free" ( with ads) or "pro" ( upgraded features, without ads)
     */
    // TODO: 21.06.2017 remove "!" from return statement ( just for sake of working this at this stage of an app
    public static boolean isFreeVersion(String packageName) {
        return packageName.contains(Constants.FREE_VERSION);
    }
}
