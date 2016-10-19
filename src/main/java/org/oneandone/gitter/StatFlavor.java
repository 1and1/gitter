/*
* Copyright 2016 1&1 Internet SE
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.oneandone.gitter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.oneandone.gitter.stats.CommitsPerInterval;
import org.oneandone.gitter.stats.AuthorsCommitsPerInterval;
import org.oneandone.gitter.stats.AuthorsPerInterval;
import org.oneandone.gitter.stats.DayTimesPerInterval;
import org.oneandone.gitter.stats.IntervalMap;

/**
 * Enum of different statistics that are possible.
 * @author Stephan Fuhrmann
 */
public enum StatFlavor {
    COMMITS_PER_INTERVAL(CommitsPerInterval.class),
    AUTHORS_PER_INTERVAL(AuthorsPerInterval.class),
    AUTHORS_COMMITS_PER_INTERVAL(AuthorsCommitsPerInterval.class),
    DAYTIMES_PER_INTERVAL(DayTimesPerInterval.class);
    
    private final Class<? extends IntervalMap> intervalMap;

    private StatFlavor(Class<? extends IntervalMap> intervalMap) {
        this.intervalMap = intervalMap;
    }

    public IntervalMap getInstance(CliOptions cliOptions) throws IllegalAccessException, InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        Constructor<IntervalMap> ctor = (Constructor<IntervalMap>) intervalMap.getConstructor(CliOptions.class);
        return ctor.newInstance(cliOptions); 
   }
}
