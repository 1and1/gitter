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
import java.util.Arrays;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Test;
import org.oneandone.gitter.Main;

/**
 * Tests {@link org.oneandone.gitter.stats.AuthorsPerInterval}.
 * @author Stephan Fuhrmann
 */
public class AuthorsPerInterval extends Integration {
    
    @Test
    public void testMonths() throws GitAPIException, IOException {

        of( "Commit Message,John Doe,doe@test.com,2016-01-02T12:00:00",
            "Commit Message,John Doe,doe@test.com,2016-02-01T12:00:00",
            "Commit Message,John Doe,doe@test.com,2016-04-04T12:00:00",
            "Commit Message,Fred Perry,perry@test.com,2016-04-07T12:00:00",
            "Commit Message,John Doe,doe@test.com,2016-08-04T12:00:00",
            "Commit Message,John Doe,doe@test.com,2016-08-07T12:00:00"
            ).forEach(c -> c.commit(git));
        
        
        Main.main(
            "--from", "2016-01-01",
            "--to", "2016-04-30",
            "--interval", "MONTHS",
            "--flavor", "AUTHORS_PER_INTERVAL",
            "--output", tmpOutput.toAbsolutePath().toString(),
            tmpRepo.toAbsolutePath().toString()
        );
        
        List<String> actual = Files.readAllLines(tmpOutput);
        List<String> expected = Arrays.asList(
"Date,"+tmpRepo.getFileName().toString(),
"2016-01-01,JD",
"2016-02-01,JD",
"2016-03-01,",
"2016-04-01,\"FP,JD\"");
        Assert.assertEquals(expected, actual);
    }
}
