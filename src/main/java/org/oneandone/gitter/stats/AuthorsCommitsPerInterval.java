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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.oneandone.gitter.CliOptions;
import org.oneandone.gitter.gitio.Commit;

/**
 * Statistics for commiters (author names) and commit counts per time interval.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class AuthorsCommitsPerInterval extends IntervalMap<Map<String,Integer>> {
    
    public AuthorsCommitsPerInterval(CliOptions cliOptions) {
        super(cliOptions);
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
    public String toString(Map<String,Integer> in) {
        return in.entrySet().stream().
            sorted((l,r) -> -Integer.compare(l.getValue(), r.getValue())).
            map(e -> e.getKey()+":"+e.getValue()).
            collect(Collectors.joining(","));
    }

    @Override
    protected Map<String,Integer> getNullEntry() {
        return new HashMap<>();
    }
}
