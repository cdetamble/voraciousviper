/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Voracious Viper.
 *
 * Voracious Viper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Voracious Viper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Voracious Viper.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bplaced.therefactory.voraciousviper.core.misc;

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
     * If that fails the GAME_VERSION_NAME is returned.
     * @return
     */
    String getVersionName();

    /**
     * Tries to return the version code set by the build.gradle.
     * 
     * If that fails the GAME_VERSION_CODE is returned.
     * @return
     */
	int getVersionCode();

}
