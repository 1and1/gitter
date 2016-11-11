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
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.gitio.Commit;

/**
 * Statistics for commit times (min, max) per time interval.
 * @author Stephan Fuhrmann
 */
@Slf4j
class DayTimesPerInterval extends IntervalMap<DayTimesPerInterval.MinMax> {

    @EqualsAndHashCode
    public static class MinMax {
        @Getter
        Optional<LocalTime> min = Optional.empty();
        @Getter
        Optional<LocalTime> max = Optional.empty();
        
        private static void append(StringBuilder sb, Optional<LocalTime> in) {
            if (in.isPresent()) {
                sb.append(in.get().toString());
            } else {
                sb.append("?");
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            append(sb, min);
            sb.append(" - ");
            append(sb, max);
            return sb.toString();
        }
    }
    
    public DayTimesPerInterval(ReportSetup setup) {
        super(setup);
    }
    
    @Override
    public void receive(Commit rc, LocalDate truncStart) {
        MinMax cur = getMap().get(truncStart);
        Objects.requireNonNull(cur, truncStart.toString());
        update(cur, rc.getWhen().toLocalTime());
    }
    
    private void update(MinMax minMax, LocalTime localTime) {
        LocalTime curMin = minMax.min.orElse(localTime);
        LocalTime curMax = minMax.max.orElse(localTime);
        minMax.min = curMin.isBefore(localTime) ? Optional.of(curMin) : Optional.of(localTime);
        minMax.max = curMax.isAfter(localTime) ? Optional.of(curMax) : Optional.of(localTime);
    }

    @Override
    public MinMax getNullEntry() {
        return new MinMax();
    }
}
