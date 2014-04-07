Add a URL under your Stash's root folder to handle serving repository files as web assets (supporting relative links, etc).  This largely addresses documentation serving use cases.

For examples.

/stash/plugins/servlet/raw/{PROJECT_KEY}/{REPOSITORY_KEY}/path/to/file.html

Here are the SDK commands you'll use immediately:

- atlas-package -- produce the plugin packaged as a jar suitable for uploading to a production instance of stash.
- atlas-run   -- installs this plugin into the product and starts it on localhost
- atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
- atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                - 'pi' reinstalls the plugin into the running product instance
- atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK
