# interlok-vcs-command-line

[![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-vcs-command-line.svg)](https://github.com/adaptris/interlok-vcs-command-line/tags) [![codecov](https://codecov.io/gh/adaptris/interlok-vcs-command-line/branch/develop/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-vcs-command-line) [![Total alerts](https://img.shields.io/lgtm/alerts/g/adaptris/interlok-vcs-command-line.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/adaptris/interlok-vcs-command-line/alerts/)

Checking out interlok configuration from a commandline based VCS on startup.

## Canonical Reference Documentation

[https://interlok.adaptris.net/interlok-docs/advanced-vcs-command-line.html](https://interlok.adaptris.net/interlok-docs/advanced-vcs-command-line.html)

## Quickstart ##

This illustrates doing a sparse git checkout on startup; in your bootstrap.properties

```
# The adapter configuration file is VCS managed; so we refer to the local working copy.
adapterConfigUrl=file://localhost/./config/interlok-config-example/adapter.xml

# Our Log4j is VCS managed; so we can refer to the local working copy.
loggingConfigUrl=file://localhost/./config/interlok-config-example/log4j2.xml

# Again, the jetty.xml is checked in, so let's refer to the local working copy.
webServerConfigUrl=./config/interlok-config-example/jetty.xml

vcs.workingcopy.url=file://localhost/./config/interlok-config-example
vcs.remote.repo.url=https://github.com/adaptris/interlok-config-example.git
vcs.revision=master

# Variable substitution can be used.
# Note how we have multiple checkout commands that are executed in sequence
vcs.command.line.checkout.0=git init ${vcs.workingcopy.url}
vcs.command.line.checkout.1=git remote add --no-tags origin ${vcs.remote.repo.url}
vcs.command.line.checkout.2=git config core.sparseCheckout true
vcs.command.line.checkout.3=cp ../scm-sparse-checkout .git/info/sparse-checkout

vcs.command.line.update.0=git pull origin ${vcs.revision}
vcs.command.line.update.1=git read-tree -mu HEAD
```

The referenced file scm-sparse-checkout simply contains

```
/*
!README.md
```
