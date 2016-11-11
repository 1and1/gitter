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
package org.oneandone.gitter.out;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Consumes statistics and prints CSV.
 * @author Stephan Fuhrmann
 */
public class CSVConsumer {

    private final PrintStream p;

    public CSVConsumer(PrintStream p) {
        this.p = Objects.requireNonNull(p);
    }

    public void consume(Map<String, Map<?, ?>> perProjectResults, 
            Function<Object, String> keyFormatter, 
            Function<Object, String> valueFormatter,
            Supplier<Object> nullValue) throws IOException {
        List<String> projects = perProjectResults.keySet().stream().sorted().collect(Collectors.toList());
        List<String> headers = new ArrayList<>(projects);
        headers.add(0, "Key");
        CSVPrinter printer = CSVFormat.EXCEL.withHeader(headers.toArray(new String[0])).print(p);

        Set<Object> keys = perProjectResults
                .values()
                .stream()
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toSet());
        TreeSet<Object> sortedKeys = new TreeSet<>(keys);

        sortedKeys.stream().forEachOrdered(key -> {
            List<String> values = new ArrayList<>();
            values.add(keyFormatter.apply(key));
            projects.forEach(project -> {
                Object obj = perProjectResults.get(project).get(key);
                if (obj == null) {
                    obj = nullValue.get();
                }
                Objects.requireNonNull(obj, () -> "Object at key "+keyFormatter.apply(key)+" for project "+project+" is null");
                values.add(obj != null ? valueFormatter.apply(obj) : "<null>");
            });
            try {

                printer.printRecord(values);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
