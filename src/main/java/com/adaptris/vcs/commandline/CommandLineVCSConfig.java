package com.adaptris.vcs.commandline;

import com.adaptris.core.management.vcs.VcsException;
import com.adaptris.core.util.PropertyHelper;

import java.io.File;
import java.util.*;

import static com.adaptris.core.management.vcs.VcsConstants.*;
import static com.adaptris.vcs.commandline.CommandLineVCSConstants.VCS_COMMAND_LINE_TIMEOUT;

class CommandLineVCSConfig {
  private String localRepo;
  private String remoteRepo;
  private String revision;
  private Properties bootstrapProperties;
  private Long timeout;

  private static final String DEFAULT_TIMEOUT = "60000";

  CommandLineVCSConfig(Properties properties) {
    localRepo = properties.getProperty(VCS_LOCAL_URL_KEY);
    remoteRepo = properties.getProperty(VCS_REMOTE_REPO_URL_KEY);
    revision = properties.getProperty(VCS_REVISION_KEY);
    timeout = Long.parseLong(properties.getProperty(VCS_COMMAND_LINE_TIMEOUT, DEFAULT_TIMEOUT));
    bootstrapProperties = properties;
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

  long getTimeout(){
    return timeout;
  }

  List<String> getCommands(String filterKey){
    SortedSet<String> keys = new TreeSet<>(PropertyHelper.getPropertySubset(bootstrapProperties, filterKey, true).stringPropertyNames());
    List<String> commands = new ArrayList<>();
    if (keys.size() == 0) {
      return commands;
    }
    for (String key : keys) {
      String command = bootstrapProperties.getProperty(key);
      commands.add(command);
    }
    return commands;
  }



}