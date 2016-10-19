package net.bplaced.therefactory.voraciousviper.core.misc;

/**
 * Created by Christian on 09.06.2016.
 */
public interface IAndroidInterface {

    /**
     * Shows a toast message on Android devices.
     * 
     * @param message The message to display.
     * @param longDuration If true message is shown for a long time, else a shorter time.
     */
    void toast(String message, boolean longDuration);

    /**
     * Tries to return the version name set by the build.gradle.
     * 
     * If that fails the VERSION_NAME is returned.
     * @return
     */
    String getVersionName();

    /**
     * Tries to return the version code set by the build.gradle.
     * 
     * If that fails the VERSION_CODE is returned.
     * @return
     */
	int getVersionCode();

}
