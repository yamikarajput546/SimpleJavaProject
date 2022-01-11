/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2020, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.git;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

/**
 * @author Sebastian Staudt
 */
@DisplayName("CommitWalkAction")
class CommitWalkActionTest {

    static class GenericCommitWalkAction extends CommitWalkAction {
        protected void run() {}
    }

    @DisplayName("should track the current commit while executing")
    @Test
    void testExecute() throws Exception {
        CommitWalkAction action = spy(new GenericCommitWalkAction());
        GitCommit commit = mock(GitCommit.class);

        action.execute(commit);

        assertThat(action.currentCommit, is(commit));
        verify(action).run();
    }

}
