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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.oneandone.gitter.ReportSetup;

/**
 * Reads commits from a local git directory.
 *
 * @author Stephan Fuhrmann
 */
@Slf4j
public class GitDirectory implements RepositoryWalker, Closeable {

    private final Repository repository;
    private final Path dir;
    private final boolean patchScriptSize;
    private final ReportSetup reportSetup;

    GitDirectory(Path dir, boolean patchScriptSize, ReportSetup reportSetup) throws IOException {
        this.dir = Objects.requireNonNull(dir);
        this.patchScriptSize = patchScriptSize;
        this.reportSetup = Objects.requireNonNull(reportSetup);
        
        Path gitDir = guessGitDir(dir);
        log.debug("Dir is {}, guessed is {}", dir, gitDir);
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

    private class MyIterator implements Iterator<Commit> {

        private Iterator<RevCommit> iterator;

        public MyIterator(Git git) {
            try {
                long from = reportSetup.getFrom().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
                long to = reportSetup.getTo().atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
                RevFilter filter = CommitTimeRevFilter.between(from, to);
                iterator = git.log().setRevFilter(filter).call().iterator();
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

            log.debug("Commit {}", dir, ObjectId.toString(revCommit.getId()));
            commit.setId(revCommit.getId().getName());
            commit.setAuthorEmail(revCommit.getAuthorIdent().getEmailAddress());
            commit.setAuthorName(revCommit.getAuthorIdent().getName());
            commit.setWhen(LocalDateTime.ofInstant(revCommit.getAuthorIdent().getWhen().toInstant(), ZoneId.systemDefault()));
            commit.setShortMessage(revCommit.getShortMessage());

            if (patchScriptSize) {
                try {
                    commit.setPatchScriptSize(Optional.of(diffSize(revCommit.getId().getName())));
                } catch (IOException ex) {
                    log.error("Unexpected exception", ex);
                    throw new RuntimeException(ex);
                }
            } else {
                commit.setPatchScriptSize(Optional.empty());
            }
            
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

        throw new IOException("Not containing a .git directory: "
                + in.toAbsolutePath().toString());
    }

    private int diffSize(String newCommit) throws IOException {
        int size = 0;
        try {
            ObjectReader reader = repository.newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = repository.resolve(newCommit+"~1^{tree}"); // equals newCommit.getTree()
            // TODO the first commit has no precedessor .. we have no diff :-(
            if (oldTree == null) {
                log.error("No precedessor object for "+newCommit+" returning default zero (which is not correct)");
                return 0;
            }
            Objects.requireNonNull(oldTree, "oldTree is null for "+newCommit);
            oldTreeIter.reset(reader, oldTree);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = repository.resolve(newCommit+"^{tree}"); // equals oldCommit.getTree()
            Objects.requireNonNull(newTree, "newTree is null for "+newCommit);
            newTreeIter.reset(reader, newTree);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DiffFormatter df = new DiffFormatter(baos);
            df.setRepository(repository);
            List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);
            
            for (DiffEntry entry : entries) {
                FileHeader fileHeader = df.toFileHeader(entry);
                
                log.debug("Fileheader for {} -> {}", fileHeader.getPath(DiffEntry.Side.OLD), fileHeader.getPath(DiffEntry.Side.NEW));
                
                log.debug(" length in bytes is {}", fileHeader.getBuffer().length);

                size += fileHeader.getBuffer().length;
                
                size += fileHeader.getBuffer().length;
                baos.reset();
            }
            //System.out.println("baos"+baos.toString());
        } catch (RevisionSyntaxException | IncorrectObjectTypeException ex) {
            throw new IOException(ex);
        }
        return size;
    }
}
