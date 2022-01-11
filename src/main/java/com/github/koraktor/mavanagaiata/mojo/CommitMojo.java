/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2018, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.mojo;

import java.text.SimpleDateFormat;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import org.eclipse.jgit.revwalk.RevCommit;

import com.github.koraktor.mavanagaiata.git.GitCommit;
import com.github.koraktor.mavanagaiata.git.GitRepository;
import com.github.koraktor.mavanagaiata.git.GitRepositoryException;

/**
 * This goal provides the full ID of the current Git commit in the
 * "mavanagaiata.commit.id", "mavanagaiata.commit.sha", "mvngit.commit.id",
 * "mvngit.commit.sha" properties. The abbreviated commit ID is stored in the
 * "mavanagaiata.commit.abbrev" and "mvngit.commit.abbrev" properties.
 * Additionally the author's and committer's name and email address are stored
 * in the properties "mavanagaiata.commit.author.name",
 * "mavanagaiata.commit.author.email", "mvngit.commit.author.name" and
 * "mvngit.commit.author.email", and "mavanagaiata.commit.committer.name",
 * "mavanagaiata.commit.committer.email", "mvngit.commit.committer.name" and
 * "mvngit.commit.committer.email" respectively.
 *
 * @author Sebastian Staudt
 * @since 0.1.0
 */
@Mojo(name = "commit",
      defaultPhase = LifecyclePhase.INITIALIZE,
      threadSafe = true)
public class CommitMojo extends AbstractGitMojo {

    /**
     * The ID (full and abbreviated) of the current Git commit out Git branch
     * is retrieved using a JGit Repository instance
     *
     * @see RevCommit#getName()
     * @see org.eclipse.jgit.lib.ObjectReader#abbreviate(org.eclipse.jgit.lib.AnyObjectId, int)
     * @throws MavanagaiataMojoException if retrieving information from the Git
     *         repository fails
     */
    public void run(GitRepository repository) throws MavanagaiataMojoException {
        try {
            GitCommit commit = repository.getHeadCommit();
            String abbrevId  = repository.getAbbreviatedCommitId();
            String shaId     = commit.getId();
            boolean isDirty  = false;

            SimpleDateFormat dateFormat = new SimpleDateFormat(this.dateFormat);
            dateFormat.setTimeZone(commit.getAuthorTimeZone());
            String authorDate = dateFormat.format(commit.getAuthorDate());
            dateFormat.setTimeZone(commit.getCommitterTimeZone());
            String commitDate = dateFormat.format(commit.getCommitterDate());

            if (repository.isDirty(dirtyIgnoreUntracked)) {
                isDirty = true;

                if (dirtyFlag != null) {
                    abbrevId += dirtyFlag;
                    shaId    += dirtyFlag;
                }
            }

            addProperty("commit.abbrev", abbrevId);
            addProperty("commit.author.date", authorDate);
            addProperty("commit.author.name", commit.getAuthorName());
            addProperty("commit.author.email", commit.getAuthorEmailAddress());
            addProperty("commit.committer.date", commitDate);
            addProperty("commit.committer.name", commit.getCommitterName());
            addProperty("commit.committer.email", commit.getCommitterEmailAddress());
            addProperty("commit.id", shaId);
            addProperty("commit.sha", shaId);
            addProperty("commit.dirty", String.valueOf(isDirty));
        } catch (GitRepositoryException e) {
            throw MavanagaiataMojoException.create("Unable to read Git commit information", e);
        }
    }
}
