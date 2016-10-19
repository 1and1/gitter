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

import java.time.LocalDate;
import java.util.function.Function;

/**
 * Time intervals for different statistics.
 * @author Stephan Fuhrmann
 */
public enum TimeInterval {
    YEARS(ld -> ld.minusDays(ld.getDayOfYear()-1), ld -> ld.plusYears(1)),
    MONTHS(ld -> ld.minusDays(ld.getDayOfMonth()-1), ld -> ld.plusMonths(1)),
    DAYS(ld -> ld, ld -> ld.plusDays(1));
    
    /** Function to truncate a local date to the given time interval.
     * Example: For years this would map 2015-05-20 to 2015-01-01.
     */
    private final Function<LocalDate,LocalDate> truncateFunction;
    
    /** Function to increment a local date to the next time interval.
     * Example: For years this would map 2015-01-01 to 2016-01-01.
     */
    private final Function<LocalDate,LocalDate> incrementFunction;
    
    private TimeInterval(Function<LocalDate,LocalDate> trunc, Function<LocalDate,LocalDate> inc) {
        truncateFunction = trunc;
        incrementFunction = inc;
    }
    
    /** Truncate a local date to the given time interval.
     * Example: For years this would map 2015-05-20 to 2015-01-01.
     * @param in the date to truncate.
     * @return the truncated date.
     */
    public LocalDate truncate(LocalDate in) {
        return truncateFunction.apply(in);
    }
    
    /** Increment a local date to the next time interval.
     * Example: For years this would map 2015-06-15 to 2016-06-15.
     * @param in the date to increment.
     * @return the incremented date.
     */
    public LocalDate increment(LocalDate in) {
        return incrementFunction.apply(in);        
    }
}
