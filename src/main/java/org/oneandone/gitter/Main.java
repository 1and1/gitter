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

import org.oneandone.gitter.report.ReportFlavor;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.gitio.RepositoryWalker;
import org.oneandone.gitter.gitio.RepositoryWalkerBuilder;
import org.oneandone.gitter.out.CSVConsumer;
import org.oneandone.gitter.report.CommitReceiver;

/**
 * Main class for starting the statistic evaluation.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class Main {

    /** Command line options. */
    private final CliOptions cliOptions;
    
    /** Per project map of histograms. */
    private final Map<String, Map<?, ?>> perProjectResults;
    
    Main(CliOptions cliOptions) {
        this.cliOptions = Objects.requireNonNull(cliOptions);
        this.perProjectResults = new HashMap<>();
    }

    private void output() throws IOException {
        PrintStream out = System.out;
        if (cliOptions.getOutput() != null) {
            out = new PrintStream(Files.newOutputStream(cliOptions.getOutput()));
        }
        CommitReceiver<Object,Object> template = cliOptions.getFlavor().newInstance(cliOptions.getReportSetup());
        new CSVConsumer(out).consume(perProjectResults, 
                (o) -> template.keyToString(o),
                (o) -> template.valueToString(o),
                () -> template.getNullEntry());
        out.close();
    }

    private void processRepository(RepositoryWalker walker) throws IOException {
        CommitReceiver receiver = cliOptions.getFlavor().newInstance(cliOptions.getReportSetup());
        receiver.clear();
        walker.readRepository()
                .filter(c -> cliOptions.getFrom() != null ? c.getWhen().toLocalDate().toEpochDay() >= cliOptions.getFrom().toEpochDay() : true)
                .filter(c -> cliOptions.getTo() != null ? c.getWhen().toLocalDate().toEpochDay() <= cliOptions.getTo().toEpochDay() : true)
                .forEach(c -> receiver.receive(c, cliOptions.getTimeInterval().truncate(c.getWhen().toLocalDate())));
        perProjectResults.put(walker.getName(), receiver.copy());
    }
    
    public static void main(String ...args) throws IOException {
        CliOptions cliOptions = CliOptions.create(args);
        if (cliOptions == null) {
            return;
        }
        Main main = new Main(cliOptions);

        for (Path dir : cliOptions.getFiles()) {
            RepositoryWalkerBuilder builder = new RepositoryWalkerBuilder();
            
            // TODO: this is ugly and should be done better: cliOptions.getFlavor() == ReportFlavor.PATCH_SCRIPT_SIZE_PER_INTERVAL
            RepositoryWalker directory = builder
                    .setGitDirectory(dir)
                    .setPatchScriptSize(cliOptions.getFlavor() == ReportFlavor.PATCH_SCRIPT_SIZE_PER_INTERVAL)
                    .setReportSetup(cliOptions.getReportSetup()).build();
            main.processRepository(directory);
        }
        
        main.output();
    }
}
