/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oneandone.gitter.gitio;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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
 * @author stephan
 */
@Slf4j
public class GitDirectory implements RepositoryWalker, Closeable {
    
    private final Repository repository;
    private final File dir;
    
    public GitDirectory(File dir) throws IOException {
        this.dir = Objects.requireNonNull(dir);
        File gitDir = guessGitDir(dir);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        this.repository = builder.setGitDir(gitDir)
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
    
    private class MyIterator implements Iterator<Commit> {

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
    
    private static File guessGitDir(File in) throws IOException {
        if (in.getName().equals(".git")) {
            return in;
        }
        
        File gitDir = new File(in, ".git");
        if (gitDir.exists()) {
            return gitDir;
        }
        
        throw new IOException("Not containing a .git directory: "+in.getAbsolutePath());
    }
}
