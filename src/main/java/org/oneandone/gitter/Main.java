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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.oneandone.gitter.gitio.GitDirectory;
import org.oneandone.gitter.out.CSVConsumer;
import org.oneandone.gitter.stats.IntervalMap;

/**
 * Main class for starting the statistic evaluation.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class Main {
    private final CliOptions cliOptions;
    
    private final IntervalMap receiver;
    private final Map<String, Map<LocalDate, ?>> perProjectResults;
    
    Main(CliOptions cliOptions) throws IllegalAccessException, InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        this.cliOptions = Objects.requireNonNull(cliOptions);
        this.receiver = cliOptions.getFlavor().getInstance(cliOptions);
        this.perProjectResults = new HashMap<>();
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        CliOptions cliOptions = CliOptions.create(args);
        if (cliOptions == null) {
            return;
        }
        Main main = new Main(cliOptions);

        for (File dir : cliOptions.getFiles()) {
            try (GitDirectory directory = new GitDirectory(dir)) {
                main.processRepository(directory);
            }
        }
        
        PrintStream out = System.out;
        if (cliOptions.getOutput() != null) {
            out = new PrintStream(new FileOutputStream(cliOptions.getOutput()));
        }
        new CSVConsumer(out).consume(main.perProjectResults, o -> main.receiver.toString(o));
        out.close();
    }

    private void processRepository(GitDirectory directory) throws IOException {
        receiver.clear();
        directory.readRepository()
                .filter((c) -> cliOptions.getFrom() != null ? c.getWhen().toLocalDate().toEpochDay() >= cliOptions.getFrom().toEpochDay() : true)
                .filter((c) -> cliOptions.getTo() != null ? c.getWhen().toLocalDate().toEpochDay() <= cliOptions.getTo().toEpochDay() : true)
                .forEach(c -> receiver.receive(c, cliOptions.getTimeInterval().truncate(c.getWhen().toLocalDate())));
        perProjectResults.put(directory.getName(), receiver.copy());
    }
}
