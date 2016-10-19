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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Reads commits from a local git directory.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class GitDirectory implements RepositoryWalker, Closeable {
    
    private final Repository repository;
    private final Path dir;
    
    public GitDirectory(Path dir) throws IOException {
        this.dir = Objects.requireNonNull(dir);
        Path gitDir = guessGitDir(dir);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        this.repository = builder.setGitDir(gitDir.toFile())
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
    }

    @Override
    public String getName() {
        return repository.getDirectory().getParentFile().getName();
    }

    @Override
    public void close() throws IOException {
        repository.close();
    }
    
    private static class MyIterator implements Iterator<Commit> {

        private Iterator<RevCommit> iterator;

        public MyIterator(Git git) {
            try {
                iterator = git.log().call().iterator();
            } catch (GitAPIException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Commit next() {
            RevCommit revCommit = iterator.next();
            Commit commit = new Commit();
            
            commit.setAuthorEmail(revCommit.getAuthorIdent().getEmailAddress());
            commit.setAuthorName(revCommit.getAuthorIdent().getName());
            commit.setWhen(LocalDateTime.ofInstant(revCommit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault()));
            
            return commit;
        }
    }

    /**
     * Read repository contents for stat creation.
     */
    @Override
    public Stream<Commit> readRepository() throws IOException {

        Git git = new Git(repository);

        Iterable<Commit> iterable = () -> new MyIterator(git);
        Stream<Commit> targetStream = StreamSupport.stream(iterable.spliterator(), false);

        return targetStream;
    }
    
    private static Path guessGitDir(Path in) throws IOException {
        if (in.getName(in.getNameCount() - 1).toString().equals(".git")) {
            return in;
        }
        
        Path gitDir = in.resolve(".git");
        if (Files.isDirectory(gitDir)) {
            return gitDir;
        }
        
        throw new IOException("Not containing a .git directory: "+
                in.toAbsolutePath().toString());
    }
}
