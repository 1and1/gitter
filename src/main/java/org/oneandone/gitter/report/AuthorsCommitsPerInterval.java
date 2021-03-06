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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.gitio.Commit;

/**
 * Statistics for commiters (author names) and commit counts per time interval.
 * @author Stephan Fuhrmann
 */
@Slf4j
class AuthorsCommitsPerInterval extends IntervalMap<Map<String,Integer>> {
    
    public AuthorsCommitsPerInterval(ReportSetup setup) {
        super(setup);
    }
    
    @Override
    public void receive(Commit rc, LocalDate truncStart) {
        String author = rc.getAuthorName();
        
        String shortName = AuthorNames.shortenName(author);
        Map<String,Integer> map = getMap().get(truncStart);
        if (map == null) {
            map = new HashMap<>();
            getMap().put(truncStart, map);
        }
        Integer old = map.getOrDefault(shortName, 0);
        map.put(shortName, old + 1);
    }

    @Override
    public String valueToString(Map<String,Integer> in) {
        return in.entrySet().stream().
            sorted((l,r) -> -Integer.compare(l.getValue(), r.getValue())).
            map(e -> e.getKey()+":"+e.getValue()).
            collect(Collectors.joining(","));
    }

    @Override
    public Map<String,Integer> getNullEntry() {
        return new HashMap<>();
    }
}
