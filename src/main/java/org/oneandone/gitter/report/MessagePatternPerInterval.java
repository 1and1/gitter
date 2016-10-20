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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.gitio.Commit;

/**
 * Statistics for commit short message pattern matches per time interval.
 * Can be used to track project identifiers or ticket numbers.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class MessagePatternPerInterval extends IntervalMap<Set<String>> {

    /** The regex to match the shortmsg with. */
    private final Pattern regex;
    
    public MessagePatternPerInterval(ReportSetup setup) {
        super(setup);
        regex = Objects.requireNonNull(setup.getShortMessageRegex(), "Short message regex parameter required");
    }
    
    @Override
    public void receive(Commit rc, LocalDate truncStart) {
        String message = rc.getShortMessage();
        
        Matcher m = regex.matcher(message);
        
        while (m.find()) {
            String match = m.group();
            Set<String> matches = getMap().get(truncStart);
            matches.add(match);
        }
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
