/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oneandone.gitter.gitio;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Walks along the commits in a Git repository.
 * @author stephan
 */
public interface RepositoryWalker {
    /** Gets the repository name. */
    public String getName();
    
    /** Gets a stream of commits. */
    public Stream<Commit> readRepository() throws IOException;
}
