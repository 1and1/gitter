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

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Walks along the commits in a Git repository.
 * @author stephan
 */
public interface RepositoryWalker {
    /** Gets the repository name.
     * @return the human readable name of the repository.
     */
    public String getName();
    
    /** Gets a stream of commits.
     * @return a stream of Commit objects for further processing.
     * @throws java.io.IOException in case of an error with the backing IO or repository.
     */
    public Stream<Commit> readRepository() throws IOException;
}
