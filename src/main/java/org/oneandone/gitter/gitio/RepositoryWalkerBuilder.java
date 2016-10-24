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
import java.nio.file.Path;
import java.util.Objects;
import org.oneandone.gitter.ReportSetup;

/**
 * Builds a new {@link RepositoryWalker}.
 * @author Stephan Fuhrmann
 */
public class RepositoryWalkerBuilder {
    private boolean patchScriptSize = false;
    private Path gitDirectory = null;
    private ReportSetup reportSetup = null;
    
    /** Builds a new {@link RepositoryWalker} instance. */
    public RepositoryWalker build() throws IOException {
        Objects.requireNonNull(gitDirectory, "Need git directory in builder");
        
        GitDirectory directory = new GitDirectory(gitDirectory, patchScriptSize, reportSetup);
        return directory;
    }

    /** Set the Git repository directory.
     * @return {@code this} instance.
     */
    public RepositoryWalkerBuilder setGitDirectory(Path directory) {
        this.gitDirectory = directory;
        return this;
    }
    
    /** Set the option to calculate the patch script size. 
     * @return {@code this} instance.
     * @see Commit#patchScriptSize
     */
    public RepositoryWalkerBuilder setPatchScriptSize(boolean set) {
        this.patchScriptSize = set;
        return this;
    }
    
    /** Set the option to calculate the patch script size. 
     * @return {@code this} instance.
     * @see Commit#patchScriptSize
     */
    public RepositoryWalkerBuilder setReportSetup(ReportSetup set) {
        this.reportSetup = set;
        return this;
    }
}
