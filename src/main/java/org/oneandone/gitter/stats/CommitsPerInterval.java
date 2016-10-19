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
package org.oneandone.gitter.stats;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.gitio.Commit;

/**
 * Statistics for commit counts per time interval.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class CommitsPerInterval extends IntervalMap<Long> {

    public CommitsPerInterval(ReportSetup setup) {
        super(setup);
    }
    
    @Override
    public void receive(Commit rc, LocalDate truncStart) {
        Long commits = getMap().getOrDefault(truncStart, 0L);
        getMap().put(truncStart, commits + 1);
    }

    @Override
    protected Long getNullEntry() {
        return 0L;
    }
}
