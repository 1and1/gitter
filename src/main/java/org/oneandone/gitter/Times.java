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
import java.time.LocalTime;

/**
 * Time functions.
 * @author Stephan Fuhrmann
 */
public interface Times {
    public static LocalDate toLocalDate(int commitTime) {
        LocalDate localDate = LocalDate.ofEpochDay(commitTime / (24*60*60));        
        return localDate;
    }
    
    public static LocalTime toLocalTime(int commitTime) {
        LocalTime localTime = LocalTime.ofSecondOfDay(commitTime % (24*60*60));
        return localTime;
    }
}
