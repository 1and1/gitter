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

import java.util.Date;
import java.util.TimeZone;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;

/**
 * Contains a GIT commit.
 * @author Stephan Fuhrmann
 */
class JGitCommitData {
    
    private final String message;
    private final String user;
    private final String email;
    private final Date date;

    public JGitCommitData(String message, String user, String email, Date date) {
        this.message = message;
        this.user = user;
        this.email = email;
        this.date = date;
    }

    public void commit(Git git) {
        try {
            PersonIdent ident = new PersonIdent(user, email, date, TimeZone.getTimeZone("UTC"));
            git.commit().setAuthor(ident).setMessage(message).call();
        } catch (GitAPIException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
