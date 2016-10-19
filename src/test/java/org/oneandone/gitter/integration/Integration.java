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
package org.oneandone.gitter.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for integration tests with commit fixture support.
 * @see #of(java.lang.String...) 
 * @author Stephan Fuhrmann
 */
public class Integration {

    /** The output tmp file. */
    protected Path tmpOutput;
    /** Temporary Git repo. */
    protected Path tmpRepo;
    
    /** The temporary JGit repo. */
    protected Git git;
    
    private static void deleteAll(Path f) {
        try {
            if (Files.isDirectory(f)) {
                Files.list(f).forEach(c -> deleteAll(c));
            }

            Files.delete(f);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @After
    public void after() throws IOException, GitAPIException {
        git.close();
        deleteAll(tmpRepo);
        deleteAll(tmpOutput);
    }
    
    @Before
    public void setup() throws IOException, GitAPIException {
        tmpRepo = Files.createTempDirectory("gitter-int");
        git = Git.init().setDirectory(tmpRepo.toFile()).call();
        tmpOutput = Files.createTempFile("gitter-int", "txt");
    }
    
    protected Date dateOf(int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDateTime time = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        Date date = new Date(time.toInstant(ZoneOffset.UTC).toEpochMilli());
        return date;
    }
    
    
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss");
    
    /** Creates a commit of a single commit descriptor line.
     * @param in a line of the format "Commit Message,John Doe,doe@test.com,2016-01-01T12:00:00"
     * @return the commit data object that can create a commit.
     */
    protected static JGitCommitData of(String in) {
        try {
            String parts[] = in.split(",");
            return new JGitCommitData(parts[0], parts[1], parts[2], dateFormat.parse(parts[3]));
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /** Creates commits of a commit descriptor lines.
     * @param in lines of the format "Commit Message,John Doe,doe@test.com,2016-01-01T12:00:00"
     * @return the commit data stream that can create commits.
     * @see #of(java.lang.String) 
     */
    protected static Stream<JGitCommitData> of(String ...in) {
        return Arrays.asList(in).stream().map(s -> of(s));
    }
}
