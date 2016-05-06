/*
 * Copyright (c) 2009 - 2016 groshev.net
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the groshev.net.
 * 4. Neither the name of the groshev.net nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY groshev.net ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL groshev.net BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.groshev.rest.beans;

/**
 * A spring controlled bean that will be injected
 * with properties about the repository state at build time.
 * This information is supplied by <b>pl.project13.maven.git-commit-id-plugin</b>
 */

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize
public class GitRepositoryStateBean {
    private static final String NEW_LINE = System.getProperty("line.separator");

    String branch;                  // =${git.branch}
    String dirty;                   // =${git.dirty}
    String tags;                    // =${git.tags} // comma separated tag names
    String describe;                // =${git.commit.id.describe}
    String shortDescribe;           // =${git.commit.id.describe-short}
    String commitId;                // =${git.commit.id}
    String commitIdAbbrev;          // =${git.commit.id.abbrev}
    String buildUserName;           // =${git.build.user.name}
    String buildUserEmail;          // =${git.build.user.email}
    String buildTime;               // =${git.build.time}
    String commitUserName;          // =${git.commit.user.name}
    String commitUserEmail;         // =${git.commit.user.email}
    String commitMessageFull;       // =${git.commit.message.full}
    String commitMessageShort;      // =${git.commit.message.short}
    String commitTime;              // =${git.commit.time}

    public GitRepositoryStateBean() {
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Branch :").append(getBranch()).append(NEW_LINE);
        text.append("Dirty :").append(getDirty()).append(NEW_LINE);
        text.append("Tags :").append(getTags()).append(NEW_LINE);
        text.append("Describe :").append(getDescribe()).append(NEW_LINE);
        text.append("Short describe :").append(getShortDescribe()).append(NEW_LINE);
        text.append("CommitId :").append(getCommitId()).append(NEW_LINE);
        text.append("CommitIdAbbrev :").append(getCommitIdAbbrev()).append(NEW_LINE);
        text.append("BuildUserName :").append(getBuildUserName()).append(NEW_LINE);
        text.append("BuildUserEmail :").append(getBuildUserEmail()).append(NEW_LINE);
        text.append("BuildTime :").append(getBuildTime()).append(NEW_LINE);
        text.append("CommitUserName :").append(getCommitUserName()).append(NEW_LINE);
        text.append("CommitUserEmail :").append(getCommitUserEmail()).append(NEW_LINE);
        text.append("CommitMessageFull :").append(getCommitMessageFull()).append(NEW_LINE);
        text.append("CommitMessageShort :").append(getCommitMessageShort()).append(NEW_LINE);
        text.append("CommitTime :").append(getCommitTime()).append(NEW_LINE);
        return text.toString();
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDirty() {
        return dirty;
    }

    public void setDirty(String dirty) {
        this.dirty = dirty;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getShortDescribe() {
        return shortDescribe;
    }

    public void setShortDescribe(String shortDescribe) {
        this.shortDescribe = shortDescribe;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitIdAbbrev() {
        return commitIdAbbrev;
    }

    public void setCommitIdAbbrev(String commitIdAbbrev) {
        this.commitIdAbbrev = commitIdAbbrev;
    }

    public String getBuildUserName() {
        return buildUserName;
    }

    public void setBuildUserName(String buildUserName) {
        this.buildUserName = buildUserName;
    }

    public String getBuildUserEmail() {
        return buildUserEmail;
    }

    public void setBuildUserEmail(String buildUserEmail) {
        this.buildUserEmail = buildUserEmail;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getCommitUserName() {
        return commitUserName;
    }

    public void setCommitUserName(String commitUserName) {
        this.commitUserName = commitUserName;
    }

    public String getCommitUserEmail() {
        return commitUserEmail;
    }

    public void setCommitUserEmail(String commitUserEmail) {
        this.commitUserEmail = commitUserEmail;
    }

    public String getCommitMessageFull() {
        return commitMessageFull;
    }

    public void setCommitMessageFull(String commitMessageFull) {
        this.commitMessageFull = commitMessageFull;
    }

    public String getCommitMessageShort() {
        return commitMessageShort;
    }

    public void setCommitMessageShort(String commitMessageShort) {
        this.commitMessageShort = commitMessageShort;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<>();
        result.put("Branch", getBranch());
        result.put("Dirty", getDirty());
        result.put("Tags", getTags());
        result.put("Describe", getDescribe());
        result.put("Short describe", getShortDescribe());
        result.put("CommitId", getCommitId());
        result.put("CommitIdAbbrev", getCommitIdAbbrev());
        result.put("BuildUserName", getBuildUserName());
        result.put("BuildUserEmail", getBuildUserEmail());
        result.put("BuildTime", getBuildTime());
        result.put("CommitUserName", getCommitUserName());
        result.put("CommitUserEmail", getCommitUserEmail());
        result.put("CommitMessageFull", getCommitMessageFull());
        result.put("CommitMessageShort", getCommitMessageShort());
        result.put("CommitTime", getCommitTime());

        return result;
    }
}