/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2019, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.mojo;

import java.util.Date;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.koraktor.mavanagaiata.git.GitCommit;
import com.github.koraktor.mavanagaiata.git.MailMap;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Sebastian Staudt
 */
@DisplayName("ContributorsMojo")
class ContributorsMojoTest extends GitOutputMojoAbstractTest<ContributorsMojo> {

    @BeforeEach
    @Override
    public void setup() throws Exception {
        super.setup();

        this.mojo.contributorPrefix = " * ";
        this.mojo.header            = "Contributors\n============\n";
        this.mojo.showCounts        = true;
        this.mojo.showEmail         = false;
        this.mojo.sort              = "count";

        MailMap mailMap = mock(MailMap.class);
        doAnswer(invocationOnMock -> {
            GitCommit commit = invocationOnMock.getArgument(0, GitCommit.class);

            return commit.getAuthorEmailAddress();
        }).when(mailMap).getCanonicalAuthorEmailAddress(any(GitCommit.class));
        when(repository.getMailMap()).thenReturn(mailMap);

        doAnswer(new Answer<ContributorsMojo.ContributorsWalkAction>() {
            long dateCounter = new Date().getTime();

            public ContributorsMojo.ContributorsWalkAction answer(InvocationOnMock invocation) throws Throwable {
                ContributorsMojo.ContributorsWalkAction walkAction = ((ContributorsMojo.ContributorsWalkAction) invocation.getArguments()[0]);
                walkAction.execute(this.mockCommit("Sebastian Staudt", "koraktor@gmail.com"));
                walkAction.execute(this.mockCommit("John Doe", "john.doe@example.com"));
                walkAction.execute(this.mockCommit("Joe Average", "joe.average@example.com"));
                walkAction.execute(this.mockCommit("Joe Average", "joe.average@example.com"));
                walkAction.execute(this.mockCommit("Sebastian Staudt", "koraktor@gmail.com"));
                walkAction.execute(this.mockCommit("Sebastian Staudt", "koraktor@gmail.com"));
                walkAction.execute(this.mockCommit("Markdown [Breaker]", "markdown.breaker@example.com"));
                walkAction.execute(this.mockCommit("HTML <Breaker>", "html.breaker@example.com"));
                return walkAction;
            }

            private GitCommit mockCommit(String authorName, String authorEmail) {
                GitCommit commit = mock(GitCommit.class);
                when(commit.getAuthorEmailAddress()).thenReturn(authorEmail);
                when(commit.getAuthorName()).thenReturn(authorName);
                when(commit.getAuthorDate()).thenReturn(new Date(dateCounter ++));
                return commit;
            }
        }).when(this.repository).walkCommits(any(ContributorsMojo.ContributorsWalkAction.class));
    }

    @DisplayName("should handle errors")
    @Test
    void testError() {
        super.testError("Unable to read contributors from Git");
    }

    @DisplayName("should allow configuration of the sort order")
    @Test
    void testInitConfiguration() {
        this.mojo.sort = null;
        this.mojo.initConfiguration();
        assertThat(this.mojo.sort, is(equalTo("count")));

        this.mojo.sort = "count";
        this.mojo.initConfiguration();
        assertThat(this.mojo.sort, is(equalTo("count")));

        this.mojo.sort = "date";
        this.mojo.initConfiguration();
        assertThat(this.mojo.sort, is(equalTo("date")));

        this.mojo.sort = "name";
        this.mojo.initConfiguration();
        assertThat(this.mojo.sort, is(equalTo("name")));

        this.mojo.sort = "unknown";
        this.mojo.initConfiguration();
        assertThat(this.mojo.sort, is(equalTo("count")));
    }

    @DisplayName("should allow configuration of the sort order")
    @Test
    void testCustomization() throws Exception {
        this.mojo.contributorPrefix = "- ";
        this.mojo.header            = "Authors\\n-------\\n";
        this.mojo.showCounts        = false;
        this.mojo.showEmail         = true;
        this.mojo.sort              = "count";
        this.mojo.initConfiguration();
        mojo.generateOutput(repository);

        this.assertOutputLine("Authors");
        this.assertOutputLine("-------");
        this.assertOutputLine("");
        this.assertOutputLine("- Sebastian Staudt (koraktor@gmail.com)");
        this.assertOutputLine("- Joe Average (joe.average@example.com)");
        this.assertOutputLine("- John Doe (john.doe@example.com)");
        this.assertOutputLine("- Markdown [Breaker] (markdown.breaker@example.com)");
        this.assertOutputLine("- HTML <Breaker> (html.breaker@example.com)");
        this.assertOutputLine("Footer");
        this.assertOutputLine(null);
    }

    @DisplayName("should be able to sort by count")
    @Test
    void testSortCount() throws Exception {
        this.mojo.sort = "count";
        this.mojo.initConfiguration();
        mojo.generateOutput(repository);

        this.assertOutputLine("Contributors");
        this.assertOutputLine("============");
        this.assertOutputLine("");
        this.assertOutputLine(" * Sebastian Staudt (3)");
        this.assertOutputLine(" * Joe Average (2)");
        this.assertOutputLine(" * John Doe (1)");
        this.assertOutputLine(" * Markdown [Breaker] (1)");
        this.assertOutputLine(" * HTML <Breaker> (1)");
        this.assertOutputLine("Footer");
        this.assertOutputLine(null);
    }

    @DisplayName("should be able to sort by date")
    @Test
    void testSortDate() throws Exception {
        this.mojo.sort = "date";
        this.mojo.initConfiguration();
        mojo.generateOutput(repository);

        this.assertOutputLine("Contributors");
        this.assertOutputLine("============");
        this.assertOutputLine("");
        this.assertOutputLine(" * Sebastian Staudt (3)");
        this.assertOutputLine(" * John Doe (1)");
        this.assertOutputLine(" * Joe Average (2)");
        this.assertOutputLine(" * Markdown [Breaker] (1)");
        this.assertOutputLine(" * HTML <Breaker> (1)");
        this.assertOutputLine("Footer");
        this.assertOutputLine(null);
    }

    @DisplayName("should be able to sort by name")
    @Test
    void testSortName() throws Exception {
        this.mojo.sort = "name";
        this.mojo.initConfiguration();
        mojo.generateOutput(repository);

        this.assertOutputLine("Contributors");
        this.assertOutputLine("============");
        this.assertOutputLine("");
        this.assertOutputLine(" * HTML <Breaker> (1)");
        this.assertOutputLine(" * Joe Average (2)");
        this.assertOutputLine(" * John Doe (1)");
        this.assertOutputLine(" * Markdown [Breaker] (1)");
        this.assertOutputLine(" * Sebastian Staudt (3)");
        this.assertOutputLine("Footer");
        this.assertOutputLine(null);
    }

    @DisplayName("should be able to escape HTML tags")
    @Test
    void testEscapeHtml() throws Exception {
        mojo.escapeHtml = true;
        mojo.initConfiguration();
        mojo.generateOutput(repository);

        assertOutputLine("Contributors");
        assertOutputLine("============");
        assertOutputLine("");
        assertOutputLine(" * Sebastian Staudt (3)");
        assertOutputLine(" * Joe Average (2)");
        assertOutputLine(" * John Doe (1)");
        assertOutputLine(" * Markdown [Breaker] (1)");
        assertOutputLine(" * HTML &lt;Breaker&gt; (1)");
        assertOutputLine("Footer");
        assertOutputLine(null);
    }

    @DisplayName("should be able to escape Markdown links")
    @Test
    void testEscapeMarkdown() throws Exception {
        mojo.escapeMarkdown = true;
        mojo.initConfiguration();
        mojo.generateOutput(repository);

        assertOutputLine("Contributors");
        assertOutputLine("============");
        assertOutputLine("");
        assertOutputLine(" * Sebastian Staudt (3)");
        assertOutputLine(" * Joe Average (2)");
        assertOutputLine(" * John Doe (1)");
        assertOutputLine(" * Markdown \\[Breaker\\] (1)");
        assertOutputLine(" * HTML <Breaker> (1)");
        assertOutputLine("Footer");
        assertOutputLine(null);
    }

    @DisplayName("should be able to escape both HTML tags and Markdown links")
    @Test
    void testEscapeHtmlAndMarkdown() throws Exception {
        mojo.escapeHtml = true;
        mojo.escapeMarkdown = true;
        mojo.initConfiguration();
        mojo.generateOutput(repository);

        assertOutputLine("Contributors");
        assertOutputLine("============");
        assertOutputLine("");
        assertOutputLine(" * Sebastian Staudt (3)");
        assertOutputLine(" * Joe Average (2)");
        assertOutputLine(" * John Doe (1)");
        assertOutputLine(" * Markdown \\[Breaker\\] (1)");
        assertOutputLine(" * HTML &lt;Breaker&gt; (1)");
        assertOutputLine("Footer");
        assertOutputLine(null);
    }
}
