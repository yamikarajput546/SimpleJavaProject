/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2018, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.mojo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;

import com.github.koraktor.mavanagaiata.git.GitRepositoryException;
import com.github.koraktor.mavanagaiata.git.jgit.JGitRepository;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author Sebastian Staudt
 */
abstract class GitOutputMojoAbstractTest<T extends AbstractGitOutputMojo> extends MojoAbstractTest<T> {

    private ByteArrayOutputStream outputStream;

    private BufferedReader reader;

    @Override
    public void setup() throws Exception {
        super.setup();

        outputStream = new ByteArrayOutputStream();

        mojo.footer = "Footer";
        mojo.printStream = new PrintStream(outputStream);
    }

    void assertOutputLine(String line) throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new StringReader(this.outputStream.toString()));
        }

        assertThat(this.reader.readLine(), is(equalTo(line)));
    }

    @Override
    protected void testError(String errorMessage) {
        repository = mock(JGitRepository.class, invocationOnMock -> {
            throw new GitRepositoryException("");
        });

        MavanagaiataMojoException e = assertThrows(MavanagaiataMojoException.class,
            () -> mojo.generateOutput(repository));
        assertThat(e.getMessage(), is(equalTo(errorMessage)));
        assertThat(e.getCause(), is(instanceOf(GitRepositoryException.class)));
    }
}
