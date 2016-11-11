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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.oneandone.gitter.report.CommitsPerInterval;
import org.oneandone.gitter.report.AuthorsCommitsPerInterval;
import org.oneandone.gitter.report.AuthorsPerInterval;
import org.oneandone.gitter.report.CommitReceiver;
import org.oneandone.gitter.report.CommitsPerAuthor;
import org.oneandone.gitter.report.DayTimesPerInterval;
import org.oneandone.gitter.report.DebugCommitReceiverFascade;
import org.oneandone.gitter.report.MessagePatternPerAuthor;
import org.oneandone.gitter.report.MessagePatternPerInterval;
import org.oneandone.gitter.report.PatchScriptSizePerInterval;

/**
 * Enum of different statistics that are possible.
 * @author Stephan Fuhrmann
 */
public enum ReportFlavor {
    COMMITS_PER_AUTHOR(CommitsPerAuthor.class),
    COMMITS_PER_INTERVAL(CommitsPerInterval.class),
    AUTHORS_PER_INTERVAL(AuthorsPerInterval.class),
    AUTHORS_COMMITS_PER_INTERVAL(AuthorsCommitsPerInterval.class),
    DAYTIMES_PER_INTERVAL(DayTimesPerInterval.class),
    MESSAGE_PATTERN_PER_AUTHOR(MessagePatternPerAuthor.class),
    MESSAGE_PATTERN_PER_INTERVAL(MessagePatternPerInterval.class),
    PATCH_SCRIPT_SIZE_PER_INTERVAL(PatchScriptSizePerInterval.class);
    
    private final Class<? extends CommitReceiver> commitReceiverMap;

    private ReportFlavor(Class<? extends CommitReceiver> intervalMap) {
        this.commitReceiverMap = intervalMap;
    }

    CommitReceiver getInstance(CliOptions cliOptions) {
        try {
            Constructor<CommitReceiver> ctor = (Constructor<CommitReceiver>) commitReceiverMap.getConstructor(ReportSetup.class); 
            CommitReceiver result = ctor.newInstance(cliOptions.getReportSetup());
            return new DebugCommitReceiverFascade(result);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException | InstantiationException | SecurityException | NoSuchMethodException ex) {
            throw new RuntimeException(ex); // no exception pollution
        }
   }
}
