package com.centraldesktop.stash;


import com.atlassian.stash.repository.Repository;

import static com.google.common.base.Preconditions.checkNotNull;

public class PathAtCommit {

    private final Repository repository;
    private final String path;
    private final String commit;
    private final String parentDirectory;
    private final String filename;

    public PathAtCommit(Repository repository, String path, String commit) {
        checkNotNull(repository, "repository");
        checkNotNull(path, "path");
        checkNotNull(commit, "commit");
        this.repository = repository;
        this.path = path;
        this.commit = commit;

        int lastSlash = path.lastIndexOf("/");
        if (lastSlash > -1) {
            filename = path.substring(lastSlash + 1);
            parentDirectory = path.substring(0, lastSlash);
        } else {
            filename = path;
            parentDirectory = "";
        }
    }

    public Repository getRepository() {
        return repository;
    }

    public String getPath() {
        return path;
    }

    /**
     * @return the empty string for files at the root of the repository, otherwise a path to the directory containing
     * the file
     */
    public String getParentDirectory() {
        return parentDirectory;
    }

    public String getFilename() {
        return filename;
    }

    public String getCommit() {
        return commit;
    }

    public String toObjectName() {
        return commit + ":" + path;
    }

}