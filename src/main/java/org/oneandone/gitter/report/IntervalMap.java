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
package org.oneandone.gitter.report;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.ReportSetup;

/**
 * Receives commit messages and does some fancy statistics mapping
 * with them.
 * @author Stephan Fuhrmann
 * @param <V> the values that are managed by the map. 
 */
@Slf4j
public abstract class IntervalMap<V> extends CommitReceiverMap<LocalDate, V> {
    
    /** Gets the interval map data from the given CliOptions object.
     * @param setup the initial setup for filtering.
     * @see #IntervalMap(org.oneandone.gitter.TimeInterval, java.time.LocalDate, java.time.LocalDate) 
     */
    public IntervalMap(ReportSetup setup) {
        super(setup);
        clear();
    }
        
    /** Resets the map and initializes the map entries with empty
     * data.
     */
    @Override
    public final void clear() {
        super.clear();
        initEntries();
    }
    
    /** Initialize time interval slots. */
    private void initEntries() {
        LocalDate cur = getTimeInterval().truncate(getFrom());
        LocalDate realTo = getTimeInterval().truncate(getTo());
        while (cur.isBefore(realTo) || cur.isEqual(realTo)) {
            getMap().put(cur, getNullEntry());
            cur = getTimeInterval().increment(cur);
        }
    }

    @Override
    public String keyToString(LocalDate entry) {
        return getTimeInterval().formatTruncated(entry);
    }
}
