package com.centraldesktop.servlet;

import com.atlassian.stash.project.Project;
import com.atlassian.stash.project.ProjectService;
import com.atlassian.stash.repository.RepositoryMetadataService;
import com.atlassian.stash.scm.CommandOutputHandler;
import com.atlassian.stash.scm.ScmCommandFactory;
import com.atlassian.stash.scm.ScmService;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequest;
import com.atlassian.stash.util.PageRequestImpl;
import com.atlassian.utils.process.CopyOutputHandler;
import com.atlassian.utils.process.OutputHandler;
import com.atlassian.utils.process.StringOutputHandler;
import com.centraldesktop.stash.PathAtCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.scm.git.GitScm;


public class Raw extends HttpServlet {
    private static final String SERVLET_LOCATION = "/plugins/servlet/raw";
    private static final Pattern PATH_RX = Pattern.compile("^/([^/]+)/([^/]+)/(.*)$");
    private static final MimetypesFileTypeMap MIMES = new MimetypesFileTypeMap();

    private final ProjectService projectService;
    private final RepositoryService repositoryService;
    private final RepositoryMetadataService repositoryMetadataService;
    private final GitScm gitScm;

    private static final int RAW_OFFSET = SERVLET_LOCATION.length();

    private static final Logger log = LoggerFactory.getLogger(Raw.class);

    public Raw(ProjectService projectService, RepositoryService repositoryService, RepositoryMetadataService repositoryMetadataService, GitScm gitScm) {
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.repositoryMetadataService = repositoryMetadataService;
        this.gitScm = gitScm;


        setupMime();
        System.out.println("Project service " + projectService);
    }

    /**
     * I hate that I have to do this.  There is no reliable, free, mime map?
     */
    private void setupMime() {
        MIMES.addMimeTypes("image/jpg jpg");
        MIMES.addMimeTypes("image/png png");
        MIMES.addMimeTypes("image/gif gif");
        MIMES.addMimeTypes("text/html html htm");
        MIMES.addMimeTypes("text/plain txt");
        MIMES.addMimeTypes("text/css css");
        MIMES.addMimeTypes("text/javascript js jsonp");
        MIMES.addMimeTypes("application/json json");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpServletRequest hr = (HttpServletRequest) request;
        String context = hr.getContextPath();

        System.out.println("s translated " + hr.getPathTranslated());
        System.out.println("s pathinfo " + hr.getPathInfo());
        System.out.println("s context " + context);
        System.out.println("s qs " + hr.getQueryString());


        PathAtCommit p = resolveRepo(hr.getPathInfo());

        String mime = MIMES.getContentType(p.getPath());
        // temp hack, remove charset
        if (mime.indexOf(";") > 0) {
            mime = mime.substring(0, mime.indexOf(";"));
        }

        response.setContentType(mime);
        response.setHeader("Content-Disposition", "inline");

        CopyCommandOutputHandler outputhandler = new CopyCommandOutputHandler(response.getOutputStream());
        gitScm.getCommandBuilderFactory()
                .builder(p.getRepository())
                .catFile()
                .pretty()
                .object(p.toObjectName())
                .build(outputhandler)
                .call();
    }


    /**
     * In a very unsafe way, grab the first repo that has this key.
     * Why do you need to page on something that has a unique key?
     *
     * @param pathInfo
     * @return
     */
    private PathAtCommit resolveRepo(String pathInfo) {
        Matcher m = PATH_RX.matcher(pathInfo);
        m.find();

//        System.out.println("Matching " + pathInfo);
//        System.out.println("--- to " + PATH_RX);

        PageRequest pr = new PageRequestImpl(0, 1000);


        Repository r = repositoryService.getBySlug(m.group(1), m.group(2));

        String at = repositoryMetadataService.getDefaultBranch(r).getLatestChangeset();

        return new PathAtCommit(r, m.group(3), at);
    }


    public class CopyCommandOutputHandler extends CopyOutputHandler implements CommandOutputHandler {
        public CopyCommandOutputHandler(OutputStream dest) {
            super(dest);
        }

        @Nullable
        @Override
        public Object getOutput() {
            return null;
        }
    }
}
