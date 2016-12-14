package com.adaptris.vcs.commandline;

import com.adaptris.core.management.vcs.RevisionHistoryItem;
import com.adaptris.core.management.vcs.VcsException;
import com.adaptris.core.management.vcs.VersionControlSystem;
import com.adaptris.core.util.PropertyHelper;
import org.apache.commons.exec.*;
import org.apache.commons.exec.util.StringUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static com.adaptris.core.management.vcs.VcsConstants.*;
import static com.adaptris.vcs.commandline.CommandLineVCSConstants.*;
import static com.adaptris.vcs.commandline.CommandLineVCSUtils.fullpath;

public class CommandLineVCS implements VersionControlSystem {

  private static final String VCS_NAME = "CommandLine";
  private static final String DEFAULT_TIMEOUT = "60000";
  private static final String COMMAND_PROPERTIES = "vcs-command-line.properties";

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  private Properties commandProperties;

  public CommandLineVCS(){
  }

  @Override
  public String testConnection(String remoteRepoUrl, File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_REMOTE_REPO_URL_KEY, remoteRepoUrl);
    if(workingCopyUrl != null) {
      substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    }
    return commandLineAction(VCS_COMMAND_LINE_TEST_CONNECTION, substitutionMap, workingCopyUrl);
  }

  @Override
  public String checkout(String remoteRepoUrl, File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_REMOTE_REPO_URL_KEY, remoteRepoUrl);
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_CHECKOUT, substitutionMap, workingCopyUrl);
  }

  @Override
  public String checkout(String remoteRepoUrl, File workingCopyUrl, String revision) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_REMOTE_REPO_URL_KEY, remoteRepoUrl);
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_REVISION_KEY, revision);
    return commandLineAction(VCS_COMMAND_LINE_CHECKOUT, substitutionMap, workingCopyUrl);
  }

  @Override
  public String update(File workingCopyUrl, String revision) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_REVISION_KEY, revision);
    return commandLineAction(VCS_COMMAND_LINE_UPDATE, substitutionMap, workingCopyUrl);
  }

  @Override
  public String update(File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_UPDATE, substitutionMap, workingCopyUrl);
  }

  @Override
  public void commit(File workingCopyUrl, String commitMessage) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_COMMIT_MESSAGE_KEY, commitMessage);
    commandLineAction(VCS_COMMAND_LINE_COMMIT, substitutionMap, workingCopyUrl);
  }

  @Override
  public void recursiveAdd(File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    commandLineAction(VCS_COMMAND_LINE_RECURSIVE_ADD, substitutionMap, workingCopyUrl);
  }

  @Override
  public void addAndCommit(File workingCopyUrl, String commitMessage, String... fileNames) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_COMMIT_MESSAGE_KEY, commitMessage);
    int i = 0;
    for (String fileName : fileNames){
      substitutionMap.put(VCS_LOCAL_FILE_KEY + "." + i, fileName);
      i++;
    }
    commandLineAction(VCS_COMMAND_LINE_ADD_AND_COMMIT, substitutionMap, workingCopyUrl, VCS_LOCAL_FILE_KEY);
  }

  @Override
  public String getImplementationName() {
    return VCS_NAME;
  }

  @Override
  public String getRemoteRevision(String remoteRepoUrl, File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_REMOTE_REPO_URL_KEY, remoteRepoUrl);
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_REMOTE_REVISION, substitutionMap, workingCopyUrl);
  }

  @Override
  public String getLocalRevision(File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_LOCAL_REVISION, substitutionMap, workingCopyUrl);
  }

  @Override
  public List<RevisionHistoryItem> getRemoteRevisionHistory(String remoteRepoUrl, File workingCopyUrl, int limit) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_REMOTE_REPO_URL_KEY, remoteRepoUrl);
    substitutionMap.put(VCS_LOCAL_URL_KEY, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_LIMIT_KEY, String.valueOf(limit));
    final String result = commandLineAction(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, substitutionMap, workingCopyUrl);
    final String lines[] = result.split("\\r?\\n");
    List<RevisionHistoryItem> history = new ArrayList<>();
    for(String line : lines){
      String historyItem[] = line.split("\\s+", 2);
      if(historyItem.length < 2){
        log.warn("{}: History item returned less that 2 items ignoring.", getImplementationName());
        continue;
      }
      history.add(new RevisionHistoryItem(historyItem[0],historyItem[1]));
    }
    return history;
  }

  Properties getCommandProperties() throws VcsException{
    if(commandProperties == null) {
      try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(COMMAND_PROPERTIES)){
        commandProperties = new Properties();
        commandProperties.load(in);
      } catch (IOException e){
        throw new VcsException(String.format("Failed to load command properties from [%s]", COMMAND_PROPERTIES), e);
      }
    }
    return commandProperties;
  }

  String commandLineAction(String filterKey, Map<String, String> substitutionMap, File workingDirectory) throws VcsException{
    return commandLineAction(filterKey, substitutionMap, workingDirectory, null);
  }

  String commandLineAction(String filterKey, Map<String, String> substitutionMap, File workingDirectory, final String repeatedKey) throws VcsException{
    List<String> commands = getCommands(filterKey);
    if (commands.size() == 0) {
      final String message = String.format("%s: Commands for [%s] not configured.", getImplementationName(), filterKey);
      log.error(message);
      throw new VcsException(message);
    }

    String result;
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
      DefaultExecutor executor = new DefaultExecutor();
      PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
      executor.setStreamHandler(streamHandler);
      ExecuteWatchdog watchdog = new ExecuteWatchdog(Long.valueOf(getCommandProperties().getProperty(VCS_COMMAND_LINE_TIMEOUT, DEFAULT_TIMEOUT)));
      executor.setWatchdog(watchdog);

      List<String> repeatedKeys = getRepeatedKeys(substitutionMap, repeatedKey);
      for (String command : commands) {
        if(workingDirectory != null && workingDirectory.exists()) {
          executor.setWorkingDirectory(workingDirectory);
        }
        if (repeatedKeys.size() == 0){
          executeCommand(executor, command, substitutionMap);
        } else {
          for (String key : repeatedKeys){
            executeCommand(executor, command.replaceAll(repeatedKey, key), substitutionMap);
          }
        }
        log.trace("Command output:\n" + outputStream.toString());
      }
      result = outputStream.toString();
    } catch (IOException e) {
      throw new VcsException(e);
    }
    return result;
  }

  void executeCommand(Executor executor, final String command, final Map<String, String> substitutionMap) throws VcsException {
    CommandLine cmdLine = CommandLine.parse(command);
    cmdLine.setSubstitutionMap(substitutionMap);
    log.debug("Executing command [" + StringUtils.toString(cmdLine.toStrings(), " ") + "]");
    try {
      executor.execute(cmdLine);
    } catch (IOException e) {
      final String message = String.format("Command [%s] failed.", StringUtils.toString(cmdLine.toStrings(), " "));
      log.error(message, e);
      throw new VcsException(message, e);
    }
  }

  List<String> getRepeatedKeys(final Map<String, String> substitutionMap, final String repeatedKey){
    List<String> results = new ArrayList<>();
    if (repeatedKey != null){
      for (Map.Entry<String, String> entry : substitutionMap.entrySet()) {
        if (entry.getKey().startsWith(repeatedKey)) {
          results.add(entry.getKey());
        }
      }
    }
    return results;
  }


  private List<String> getCommands(String filterKey) throws VcsException{
    SortedSet<String> keys = new TreeSet<>(PropertyHelper.getPropertySubset(getCommandProperties(), filterKey, true).stringPropertyNames());
    List<String> commands = new ArrayList<>();
    if (keys.size() == 0) {
      return commands;
    }
    for (String key : keys) {
      String command = getCommandProperties().getProperty(key);
      commands.add(command);
    }
    return commands;
  }
}
