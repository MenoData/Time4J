/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AndroidTickerSPI.java) is part of project Time4J.
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

package net.time4j.scale.spi;

import android.os.SystemClock;
import net.time4j.base.MathUtils;
import net.time4j.scale.TickProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * <p>{@code ServiceProvider}-implementation for accessing the non-freezing clock of Android. </p>
 *
 * @author  Meno Hochschild
 */
public class AndroidTickerSPI
    implements TickProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Class[] EMPTY_PARAMS = new Class[0];
    private static final Object[] EMPTY_ARGS = new Object[0];

    private static final Method ANDROID;

    static {
        Method method;
        try {
            method = SystemClock.class.getMethod("elapsedRealtimeNanos", EMPTY_PARAMS);
            method.invoke(null, EMPTY_ARGS); // test
        } catch (NoSuchMethodException e) {
            method = null;
        } catch (InvocationTargetException e) {
            method = null;
        } catch (IllegalAccessException e) {
            method = null;
        } catch (RuntimeException e) {
            method = null;
        }
        ANDROID = method;
    }

        //~ Methoden ----------------------------------------------------------

    @Override
    public String getPlatform() {

        return "Dalvik";

    }

    @Override
    public long getNanos() {

        if (ANDROID != null) {
            try {
                return (Long) ANDROID.invoke(null, EMPTY_ARGS);
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.err);
            } catch (InvocationTargetException e) {
                e.printStackTrace(System.err);
            }
        }

        return MathUtils.safeMultiply(SystemClock.elapsedRealtime(), 1000000L);

    }

}
