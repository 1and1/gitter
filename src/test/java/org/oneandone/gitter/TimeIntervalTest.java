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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for {@link TimeInterval}.
 * @author Stephan Fuhrmann
 */
public class TimeIntervalTest {
    @Test
    public void dayTruncateSame() {
        LocalDate in = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.DAYS.truncate(in);
        assertEquals(in, actual);
    }
    
    @Test
    public void dayIncrementEasy() {
        LocalDate in = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.DAYS.increment(in);
        LocalDate expect = LocalDate.of(2016, 01, 2);
        assertEquals(expect, actual);
    }
    
    @Test
    public void dayIncrementBorder() {
        LocalDate in = LocalDate.of(2016, 12, 31);
        LocalDate actual = TimeInterval.DAYS.increment(in);
        LocalDate expect = LocalDate.of(2017, 01, 1);
        assertEquals(expect, actual);
    }
    
    @Test
    public void monthTruncateSame() {
        LocalDate in = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.MONTHS.truncate(in);
        assertEquals(in, actual);
    }
    
    @Test
    public void monthTruncateOther() {
        LocalDate in = LocalDate.of(2016, 01, 2);
        LocalDate expect = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.MONTHS.truncate(in);
        assertEquals(expect, actual);
    }
    
    @Test
    public void monthTruncateBorder() {
        LocalDate in = LocalDate.of(2016, 12, 31);
        LocalDate expect = LocalDate.of(2016, 12, 1);
        LocalDate actual = TimeInterval.MONTHS.truncate(in);
        assertEquals(expect, actual);
    }
    
    @Test
    public void monthIncrementEasy() {
        LocalDate in = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.MONTHS.increment(in);
        LocalDate expect = LocalDate.of(2016, 02, 1);
        assertEquals(expect, actual);
    }
    
    @Test
    public void monthIncrementBorder() {
        LocalDate in = LocalDate.of(2016, 12, 31);
        LocalDate actual = TimeInterval.MONTHS.increment(in);
        LocalDate expect = LocalDate.of(2017, 01, 31);
        assertEquals(expect, actual);
    }
    
    @Test
    public void yearTruncateSame() {
        LocalDate in = LocalDate.of(2016, 1, 1);
        LocalDate actual = TimeInterval.YEARS.truncate(in);
        assertEquals(in, actual);
    }
    
    @Test
    public void yearTruncateOther() {
        LocalDate in = LocalDate.of(2016, 6, 5);
        LocalDate expect = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.YEARS.truncate(in);
        assertEquals(expect, actual);
    }
    
    @Test
    public void yearTruncateBorder() {
        LocalDate in = LocalDate.of(2016, 12, 31);
        LocalDate expect = LocalDate.of(2016, 1, 1);
        LocalDate actual = TimeInterval.YEARS.truncate(in);
        assertEquals(expect, actual);
    }
    
    @Test
    public void yearIncrementEasy() {
        LocalDate in = LocalDate.of(2016, 01, 1);
        LocalDate actual = TimeInterval.YEARS.increment(in);
        LocalDate expect = LocalDate.of(2017, 01, 1);
        assertEquals(expect, actual);
    }
    
    @Test
    public void yearIncrementBorder() {
        LocalDate in = LocalDate.of(2016, 12, 31);
        LocalDate actual = TimeInterval.YEARS.increment(in);
        LocalDate expect = LocalDate.of(2017, 12, 31);
        assertEquals(expect, actual);
    }
}
