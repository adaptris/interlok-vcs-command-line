package com.adaptris.vcs.commandline;

import com.adaptris.core.management.BootstrapProperties;
import com.adaptris.core.management.vcs.RuntimeVersionControl;
import com.adaptris.core.management.vcs.VcsException;
import com.adaptris.core.management.vcs.VersionControlSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

import static com.adaptris.core.management.vcs.VcsConstants.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class CommandLineRVC implements RuntimeVersionControl {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  private static final String VCS_NAME = "CommandLine";

  private BootstrapProperties bootstrapProperties;

  private transient VersionControlSystem api;

  public CommandLineRVC(){

  }

  public CommandLineRVC(BootstrapProperties bootstrapProperties){
    setBootstrapProperties(bootstrapProperties);
  }


  @Override
  public String getImplementationName() {
    return VCS_NAME;
  }

  @Override
  public void update() throws VcsException {
    CommandLineVCSConfig config = new CommandLineVCSConfig(getBootstrapProperties());
    if (!config.isConfigured()) {
      log.info("{}: [{}] not configured skipping repository update.", getImplementationName(),  VCS_LOCAL_URL_KEY);
      return;
    }
    log.info("{}: Checking local repository [{}] ", getImplementationName(), CommandLineVCSUtils.fullpath(config.getLocalRepo()));
    if (!config.getLocalRepo().exists()) {
      log.info("{}: [{}] does not exist, performing fresh checkout.", getImplementationName(),  CommandLineVCSUtils.fullpath(config.getLocalRepo()));
      commandLineCheckout(config);
    }
    commandLineUpdate(config);
  }

  @Override
  public void checkout() throws VcsException {
    CommandLineVCSConfig config = new CommandLineVCSConfig(getBootstrapProperties());
    commandLineCheckout(config);
    commandLineUpdate(config);
  }

  private void commandLineCheckout(CommandLineVCSConfig config) throws VcsException {
    if (!config.isConfigured()) {
      log.info("{}: [{}] or [{}] not configured, skipping checkout.", getImplementationName(), VCS_LOCAL_URL_KEY, VCS_REMOTE_REPO_URL_KEY);
      return;
    }
    log.info("{}: Performing checkout to [{}] ", getImplementationName(), CommandLineVCSUtils.fullpath(config.getLocalRepo()));
    if (!config.hasRevision()) {
      this.api().checkout(config.getRemoteRepo(), config.getLocalRepo());
    } else {
      this.api().checkout(config.getRemoteRepo(), config.getLocalRepo(), config.getRevision());
    }

  }

  private void commandLineUpdate(CommandLineVCSConfig config) throws VcsException {
    if (!config.isConfigured()) {
      log.info("{}: [{}] not configured, skipping update.", getImplementationName(), VCS_LOCAL_URL_KEY);
      return;
    }
    log.info("{}: Performing update to [{}] ", getImplementationName(), CommandLineVCSUtils.fullpath(config.getLocalRepo()));
    if (isEmpty(config.getRevision())) {
      this.api().update(config.getLocalRepo());
    } else {
      this.api().update(config.getLocalRepo(), config.getRevision());
    }
  }

  @Override
  public void setBootstrapProperties(BootstrapProperties bootstrapProperties) {
    this.bootstrapProperties = bootstrapProperties;
  }

  @Override
  public VersionControlSystem getApi(Properties properties) throws VcsException {
    return new CommandLineVCS(properties);
  }

  public BootstrapProperties getBootstrapProperties() {
    return this.bootstrapProperties;
  }

  protected VersionControlSystem api() throws VcsException {
    if (this.getApi() == null) {
      this.setApi(this.getApi(getBootstrapProperties()));
    }
    return this.getApi();
  }

  VersionControlSystem getApi() {
    return api;
  }

  void setApi(VersionControlSystem api) {
    this.api = api;
  }

  private class CommandLineVCSConfig {
    private String localRepo;
    private String remoteRepo;
    private String revision;

    CommandLineVCSConfig(Properties properties) {
      localRepo = properties.getProperty(VCS_LOCAL_URL_KEY);
      remoteRepo = properties.getProperty(VCS_REMOTE_REPO_URL_KEY);
      revision = properties.getProperty(VCS_REVISION_KEY);

    }

    boolean isConfigured() {
      return localRepo != null && remoteRepo != null;
    }

    boolean hasRevision() {
      return revision != null;
    }

    String getRemoteRepo() {
      return remoteRepo;
    }

    String getRevision() {
      return revision;
    }

    File getLocalRepo() throws VcsException {
      return CommandLineVCSUtils.urlToFile(localRepo);
    }
  }
}
