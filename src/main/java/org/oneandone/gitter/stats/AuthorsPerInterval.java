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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.oneandone.gitter.CliOptions;
import org.oneandone.gitter.gitio.Commit;

/**
 * Statistics for commiters (author names) per time interval.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class AuthorsPerInterval extends IntervalMap<Set<String>> {

    public AuthorsPerInterval(CliOptions cliOptions) {
        super(cliOptions);
    }
    
    @Override
    public void receive(Commit rc, LocalDate truncStart) {
        String author = rc.getAuthorName();
        Set<String> authors = getMap().get(truncStart);
        authors.add(AuthorNames.shortenName(author));
    }

    @Override
    public String toString(Set<String> in) {
        return in.stream().
            sorted(String.CASE_INSENSITIVE_ORDER).
            collect(Collectors.joining(","));
    }

    @Override
    protected Set<String> getNullEntry() {
        return new TreeSet<>();
    }
}
