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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.TimeInterval;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.gitio.Commit;

/**
 * Receives commit messages and does some fancy statistics mapping
 * with them.
 * @author Stephan Fuhrmann
 * @param <V> the values that are managed by the map. 
 */
@Slf4j
public abstract class CommitReceiverMap<K,V> {
    
    @Getter(AccessLevel.PROTECTED)
    private final Map<K, V> map;

    @Getter
    private final TimeInterval timeInterval;
    
    @Getter
    private final LocalDate from;
    
    @Getter
    private final LocalDate to;
    
    /** Builds an interval map. 
     * @param interval the interval to truncate to.
     * @param from start date (inclusive).
     * @param to end date (inclusive).
     */
    private CommitReceiverMap(TimeInterval interval, LocalDate from, LocalDate to) {
        map = new HashMap<>();
        this.timeInterval = Objects.requireNonNull(interval);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        clear();
    }
    
    /** Gets the interval map data from the given CliOptions object.
     * @param setup the initial setup for filtering.
     * @see #IntervalMap(org.oneandone.gitter.TimeInterval, java.time.LocalDate, java.time.LocalDate) 
     */
    public CommitReceiverMap(ReportSetup setup) {
        this(setup.getInterval(), setup.getFrom(), setup.getTo());
    }

    
    /** Creates a copy of the map data for further processing.
     * @return a copy that belongs to the caller.
     */
    public final Map<K, V> copy() {
        Map<K, V> result = new HashMap<>(getMap());
        return result;
    }
    
    /** Resets the map and initializes the map entries with empty
     * data.
     */
    public void clear() {
        map.clear();
    }
    
    /** Empty data, for example an empty array.
     * @return the empty-data element, for example 0 for an empty number or "" for an empty string.
     */
    public abstract V getNullEntry();
    
    /** Process one commit entry and update the internal {@link #map data}.
     * @param rc the Git commit info
     * @param truncStart the truncated start date. For example 2016-04-01 if truncating to months.
     */
    public abstract void receive(Commit rc, LocalDate truncStart);

    /** Convert one map key to a String. Can be overridden to customize the
     * output.
     * @param entry a map key to format.
     * @return String representation of a table entry.
     */
    public String keyToString(K entry) {
        return entry.toString();
    }
    
    /** Convert one map value to a String. Can be overridden to customize the
     * output.
     * @param entry a map value to format.
     * @return String representation of a table entry.
     */
    public String valueToString(V entry) {
        return entry.toString();
    }
}
