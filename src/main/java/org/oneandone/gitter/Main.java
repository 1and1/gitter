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

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.gitio.GitDirectory;
import org.oneandone.gitter.out.CSVConsumer;
import org.oneandone.gitter.report.IntervalMap;

/**
 * Main class for starting the statistic evaluation.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class Main {

    /** Command line options. */
    private final CliOptions cliOptions;
    
    /** Per project map of histograms. */
    private final Map<String, Map<LocalDate, ?>> perProjectResults;
    
    Main(CliOptions cliOptions) {
        this.cliOptions = Objects.requireNonNull(cliOptions);
        this.perProjectResults = new HashMap<>();
    }

    private void output() throws IOException {
        PrintStream out = System.out;
        if (cliOptions.getOutput() != null) {
            out = new PrintStream(Files.newOutputStream(cliOptions.getOutput()));
        }
        new CSVConsumer(out).consume(perProjectResults, 
                (LocalDate d) -> cliOptions.getTimeInterval().formatTruncated(d),
                (o) -> cliOptions.getFlavor().getInstance(cliOptions).toString(o));
        out.close();
    }

    private void processRepository(GitDirectory directory) throws IOException {
        IntervalMap receiver = cliOptions.getFlavor().getInstance(cliOptions);
        receiver.clear();
        directory.readRepository()
                .filter(c -> cliOptions.getFrom() != null ? c.getWhen().toLocalDate().toEpochDay() >= cliOptions.getFrom().toEpochDay() : true)
                .filter(c -> cliOptions.getTo() != null ? c.getWhen().toLocalDate().toEpochDay() <= cliOptions.getTo().toEpochDay() : true)
                .forEach(c -> receiver.receive(c, cliOptions.getTimeInterval().truncate(c.getWhen().toLocalDate())));
        perProjectResults.put(directory.getName(), receiver.copy());
    }
    
    public static void main(String ...args) throws IOException {
        CliOptions cliOptions = CliOptions.create(args);
        if (cliOptions == null) {
            return;
        }
        Main main = new Main(cliOptions);

        for (Path dir : cliOptions.getFiles()) {
            try (GitDirectory directory = new GitDirectory(dir)) {
                main.processRepository(directory);
            }
        }
        
        main.output();
    }
}
