/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeApplication.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */
package net.time4j.android;

import android.app.Application;


/**
 * <p>Serves as super class for any time-based android application using Time4J. </p>
 *
 * @author      Meno Hochschild
 * @since       3.2
 */
/*[deutsch]
 * <p>Dient als Superklasse f&uuml;r eine beliebige zeitbasierte Android-App, die Time4J nutzt. </p>
 *
 * @author      Meno Hochschild
 * @since       3.2
 */
public abstract class TimeApplication
    extends Application {

    //~ Methoden ----------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationStarter.registerReceiver(this);

    }

}
