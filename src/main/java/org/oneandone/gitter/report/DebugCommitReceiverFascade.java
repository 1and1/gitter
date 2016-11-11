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
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.gitio.Commit;

/**
 * Fascade for debugging incoming data.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class DebugCommitReceiverFascade<K,V> implements CommitReceiver<K,V> {

    private final CommitReceiver<K,V> inner;

    public DebugCommitReceiverFascade(CommitReceiver<K, V> inner) {
        this.inner = Objects.requireNonNull(inner);
    }
    
    @Override
    public ReportSetup getReportSetup() {
        return inner.getReportSetup();
    }

    @Override
    public Map<K, V> copy() {
        return inner.copy();
    }

    @Override
    public void clear() {
        inner.clear();
    }

    @Override
    public V getNullEntry() {
        return inner.getNullEntry();
    }

    @Override
    public void receive(Commit rc, LocalDate truncStart) {
        log.debug("Commit {}", rc);
        inner.receive(rc, truncStart);
    }

    @Override
    public String keyToString(K entry) {
        return inner.keyToString(entry);
    }

    @Override
    public String valueToString(V entry) {
        return inner.valueToString(entry);
    }
}
