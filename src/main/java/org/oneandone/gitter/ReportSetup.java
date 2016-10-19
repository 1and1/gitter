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

import java.time.LocalDate;
import java.util.regex.Pattern;
import lombok.Getter;

/**
 * Settings for a report.
 * @author Stephan Fuhrmann
 */
public class ReportSetup {
    @Getter
    private final TimeInterval interval;
    @Getter
    private final LocalDate from; 
    @Getter
    private final LocalDate to;
    @Getter
    private final Pattern shortMessageRegex;

    public ReportSetup(TimeInterval interval, LocalDate from, LocalDate to, Pattern shortMessageRegex) {
        this.interval = interval;
        this.from = from;
        this.to = to;
        this.shortMessageRegex = shortMessageRegex;
    }
}

