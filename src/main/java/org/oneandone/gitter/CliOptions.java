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


import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerRegistry;

/**
 * Command line options.
 * @see #create(java.lang.String[]) 
 * @author Stephan Fuhrmann
 */
class CliOptions {

    @Option(name = "-h", aliases = {"--help"}, usage = "Show this help message.", help = true)
    private boolean help;
    
    @Option(name = "-f", aliases = {"--from"}, metaVar = "DATE", required = true, usage = "From timestamp to use for filtering, for example 2015-12-31.")
    @Getter @Setter
    private LocalDate from;
    
    @Option(name = "-t", aliases = {"--to"}, metaVar = "DATE", required = true, usage = "To timestamp to use for filtering, for example 2015-12-31.")
    @Getter @Setter
    private LocalDate to;
    
    @Option(name = "-r", aliases = {"--report"}, metaVar = "REPORT", usage = "The type of report to calculate.")
    @Getter @Setter
    private ReportFlavor flavor = ReportFlavor.COMMITS_PER_INTERVAL;
    
    @Option(name = "-o", aliases = {"--output"}, metaVar = "FILE", usage = "The output file.")
    @Getter @Setter
    private Path output;
    
    @Option(name = "-I", aliases = {"--interval"}, metaVar = "INTERVAL", usage = "The time interval to use.")
    @Getter @Setter
    private TimeInterval timeInterval = TimeInterval.MONTHS;
  
    @Option(name = "-p", aliases = {"--pattern"}, metaVar = "REGEX", usage = "Short message pattern.")
    @Getter @Setter
    private Pattern shortMessageRegex;
    
    @Argument(metaVar = "DIR", usage = "GIT repository dirs.")
    @Getter @Setter
    private List<Path> files = new ArrayList<>();

    public boolean isHelp() {
        return help;
    }

    /** Parse the command line options and return the
     * object view of the command line options.
     * @param args the command line options of the main method.
     * @return the parsed CLI options or {@code null} if 
     * options lead to a program exit.
     */
    public static CliOptions create(String[] args) {
        Objects.requireNonNull(args);
        OptionHandlerRegistry.getRegistry().registerHandler(LocalDate.class, LocalDateOptionHandler.class);
        try {
            CliOptions result = new CliOptions();
            CmdLineParser parser = new CmdLineParser(result);
            parser.parseArgument(args);

            if (result.isHelp()) {
                parser.printUsage(System.err);
                return null;
            }

            return result;
        } catch (CmdLineException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }
    
    public ReportSetup getReportSetup() {
        return new ReportSetup(timeInterval, from, to, Optional.ofNullable(shortMessageRegex));
    }
}
