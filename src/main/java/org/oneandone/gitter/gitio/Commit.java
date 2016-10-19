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
package org.oneandone.gitter.gitio;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * A Git commit POJO.
 * @author Stephan Fuhrmann
 */
public class Commit {
    @Getter @Setter
    private String authorName;
    @Getter @Setter
    private String authorEmail;
    @Getter @Setter
    private LocalDateTime when;
}
