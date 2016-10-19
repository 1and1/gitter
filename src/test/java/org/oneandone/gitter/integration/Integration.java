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
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oneandone.gitter.Main;

/**
 *
 * @author stephan
 */
public class Integration {

    /** The output tmp file. */
    private Path tmpOutput;
    /** Temporary Git repo. */
    private Path tmpRepo;
    
    /** The temporary JGit repo. */
    private Git git;
    
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
    
    private Date dateOf(int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDateTime time = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        Date date = new Date(time.toInstant(ZoneOffset.UTC).toEpochMilli());
        return date;
    }
    
    @Test
    public void testOne() throws GitAPIException, IOException {
                
        PersonIdent ident = new PersonIdent("John Doe", "doe@test.com", dateOf(2016, Month.JANUARY, 12, 11, 0), TimeZone.getTimeZone("UTC"));
        git.commit().setAuthor(ident).setMessage("My Message").call();
        ident = new PersonIdent("John Doe", "doe@test.com", dateOf(2016, Month.FEBRUARY, 1, 11, 0), TimeZone.getTimeZone("UTC"));
        git.commit().setAuthor(ident).setMessage("My Message 2").call();
        ident = new PersonIdent("John Doe", "doe@test.com", dateOf(2016, Month.APRIL, 1, 12, 0), TimeZone.getTimeZone("UTC"));
        git.commit().setAuthor(ident).setMessage("My Message 3").call();
        ident = new PersonIdent("John Doe", "doe@test.com", dateOf(2016, Month.APRIL, 1, 14, 0), TimeZone.getTimeZone("UTC"));
        git.commit().setAuthor(ident).setMessage("My Message 4").call();
        ident = new PersonIdent("John Doe", "doe@test.com", dateOf(2016, Month.AUGUST, 1, 14, 0), TimeZone.getTimeZone("UTC"));
        git.commit().setAuthor(ident).setMessage("My Message 4").call();
        
        Main.main(new String[] {
            "--from", "2016-01-01",
            "--to", "2016-04-01",
            "--output", tmpOutput.toAbsolutePath().toString(),
            tmpRepo.toAbsolutePath().toString(),
        });
        
        List<String> actual = Files.readAllLines(tmpOutput);
        List<String> expected = Arrays.asList(
"Date,"+tmpRepo.getFileName().toString(),
"2016-01-01,1",
"2016-02-01,1",
"2016-03-01,0",
"2016-04-01,2");
        Assert.assertEquals(expected, actual);
    }
}
