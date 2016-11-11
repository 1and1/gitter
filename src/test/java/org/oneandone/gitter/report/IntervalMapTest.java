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

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.oneandone.gitter.ReportSetup;
import org.oneandone.gitter.TimeInterval;
import org.oneandone.gitter.gitio.Commit;

/**
 * Test for {@link IntervalMap}.
 * @author Stephan Fuhrmann
 */
public class IntervalMapTest {
    ReportSetup reportSetup;
    IntervalMap<Integer> instance;
    
    @Before
    public void init() {
         reportSetup = new ReportSetup(TimeInterval.MONTHS, 
            LocalDate.of(2016, Month.FEBRUARY, 1), LocalDate.of(2016, Month.APRIL, 30), 
            Optional.empty());
        instance = new IntervalMap<Integer>(reportSetup) {
            @Override
            public Integer getNullEntry() {
                return 0;
            }

            @Override
            public void receive(Commit rc, LocalDate truncStart) {
                Integer cur = getMap().get(truncStart);
                getMap().put(truncStart, cur + 1);
            }
        };
    }
    
    @Test
    public void testReportSetupFields() {
        assertEquals(reportSetup.getFrom(), instance.getFrom());
        assertEquals(reportSetup.getTo(), instance.getTo());
        assertEquals(reportSetup.getInterval(), instance.getTimeInterval());
    }
    
    @Test
    public void testGetMapWithMonths() {
        Map<LocalDate, Integer> map = instance.getMap();

        Map<LocalDate, Integer> expected = new HashMap<>();
        expected.put(LocalDate.of(2016, Month.FEBRUARY, 1), 0);
        expected.put(LocalDate.of(2016, Month.MARCH, 1), 0);
        expected.put(LocalDate.of(2016, Month.APRIL, 1), 0);
        
        assertEquals(expected, map);
    }
    
    @Test
    public void testGetMapWithAfterReceive() {
        
        instance.receive(null, reportSetup.getInterval().truncate(LocalDate.of(2016, Month.MARCH, 15)));
        
        Map<LocalDate, Integer> map = instance.getMap();

        Map<LocalDate, Integer> expected = new HashMap<>();
        expected.put(LocalDate.of(2016, Month.FEBRUARY, 1), 0);
        expected.put(LocalDate.of(2016, Month.MARCH, 1), 1);
        expected.put(LocalDate.of(2016, Month.APRIL, 1), 0);
        
        assertEquals(expected, map);
    }
    
    @Test
    public void testGetMapWithAfterClear() {
        
        instance.receive(null, reportSetup.getInterval().truncate(LocalDate.of(2016, Month.MARCH, 15)));
        instance.clear();
        
        Map<LocalDate, Integer> map = instance.getMap();

        Map<LocalDate, Integer> expected = new HashMap<>();
        expected.put(LocalDate.of(2016, Month.FEBRUARY, 1), 0);
        expected.put(LocalDate.of(2016, Month.MARCH, 1), 0);
        expected.put(LocalDate.of(2016, Month.APRIL, 1), 0);
        
        assertEquals(expected, map);
    }
}
