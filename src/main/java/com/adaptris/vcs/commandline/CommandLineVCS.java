package com.adaptris.vcs.commandline;

import com.adaptris.core.management.vcs.RevisionHistoryItem;
import com.adaptris.core.management.vcs.VcsException;
import com.adaptris.core.management.vcs.VersionControlSystem;
import org.apache.commons.exec.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static com.adaptris.vcs.commandline.CommandLineVCSConstants.*;
import static com.adaptris.vcs.commandline.CommandLineVCSUtils.fullpath;

public class CommandLineVCS implements VersionControlSystem {

  private static final String VCS_NAME = "CommandLineVCS";

  protected transient Logger log = LoggerFactory.getLogger(this.getClass());

  private CommandLineVCSConfig config;

  public CommandLineVCS(Properties properties){
    config = new CommandLineVCSConfig(properties);
  }

  @Override
  public String testConnection(String remoteRepoUrl, File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_REMOTE_REPO, remoteRepoUrl);
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
      return commandLineAction(VCS_COMMAND_LINE_TEST_CONNECTION,substitutionMap);
  }

  @Override
  public String checkout(String remoteRepoUrl, File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_REMOTE_REPO, remoteRepoUrl);
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_CHECKOUT,substitutionMap);
  }

  @Override
  public String checkout(String remoteRepoUrl, File workingCopyUrl, String revision) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_REMOTE_REPO, remoteRepoUrl);
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_SUBSTITUTION_REVISION, revision);
    return commandLineAction(VCS_COMMAND_LINE_CHECKOUT, substitutionMap);
  }

  @Override
  public String update(File workingCopyUrl, String revision) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_SUBSTITUTION_REVISION, revision);
    return commandLineAction(VCS_COMMAND_LINE_UPDATE, substitutionMap);
  }

  @Override
  public String update(File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_UPDATE, substitutionMap);
  }

  @Override
  public void commit(File workingCopyUrl, String commitMessage) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_SUBSTITUTION_COMMIT_MESSAGE, commitMessage);
    commandLineAction(VCS_COMMAND_LINE_COMMIT, substitutionMap);
  }

  @Override
  public void recursiveAdd(File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    commandLineAction(VCS_COMMAND_LINE_RECURSIVE_ADD, substitutionMap);
  }

  @Override
  public void addAndCommit(File workingCopyUrl, String commitMessage, String... fileNames) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_SUBSTITUTION_COMMIT_MESSAGE, commitMessage);
    int i = 0;
    for (String fileName : fileNames){
      substitutionMap.put(VCS_SUBSTITUTION_LOCAL_FILE + "." + i, fileName);
      i++;
    }
    commandLineAction(VCS_COMMAND_LINE_ADD_AND_COMMIT, substitutionMap, VCS_SUBSTITUTION_LOCAL_FILE);
  }

  @Override
  public String getImplementationName() {
    return VCS_NAME;
  }

  @Override
  public String getRemoteRevision(String remoteRepoUrl, File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_REMOTE_REPO, remoteRepoUrl);
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_REMOTE_REVISION, substitutionMap);
  }

  @Override
  public String getLocalRevision(File workingCopyUrl) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    return commandLineAction(VCS_COMMAND_LINE_LOCAL_REVISION, substitutionMap);
  }

  @Override
  public List<RevisionHistoryItem> getRemoteRevisionHistory(String remoteRepoUrl, File workingCopyUrl, int limit) throws VcsException {
    Map<String, String> substitutionMap = new HashMap<>();
    substitutionMap.put(VCS_SUBSTITUTION_REMOTE_REPO, remoteRepoUrl);
    substitutionMap.put(VCS_SUBSTITUTION_LOCAL_REPO, fullpath(workingCopyUrl));
    substitutionMap.put(VCS_SUBSTITUTION_LIMIT, String.valueOf(limit));
    final String result = commandLineAction(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, substitutionMap);
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

  private CommandLineVCSConfig getConfig(){
    return this.config;
  }



  String commandLineAction(String filterKey, Map<String, String> substitutionMap) throws VcsException{
    return commandLineAction(filterKey, substitutionMap, null);
  }

  String commandLineAction(String filterKey, Map<String, String> substitutionMap, final String repeatedKey) throws VcsException{
    List<String> commands = getConfig().getCommands(filterKey);
    if (commands.size() == 0) {
      log.warn("{}: [{}] not configured, skipping checkout.", getImplementationName(), filterKey);
      return null;
    }

    String result;
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
      DefaultExecutor executor = new DefaultExecutor();

      PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
      executor.setStreamHandler(streamHandler);
      ExecuteWatchdog watchdog = new ExecuteWatchdog(getConfig().getTimeout());
      executor.setWatchdog(watchdog);

      List<String> repeatedKeys = getRepeatedKeys(substitutionMap, repeatedKey);
      for (String command : commands) {
        if(getConfig().getLocalRepo().exists()) {
          executor.setWorkingDirectory(getConfig().getLocalRepo());
        }
        if (repeatedKeys.size() == 0){
          executeCommand(executor, command, substitutionMap);
        } else {
          for (String key : repeatedKeys){
            executeCommand(executor, command.replaceAll(repeatedKey, key), substitutionMap);
          }
        }
      }
      result = outputStream.toString();
    } catch (IOException e) {
      throw new VcsException(e);
    }
    return result;
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

  void executeCommand(Executor executor, final String command, final Map<String, String> substitutionMap) throws VcsException {
    CommandLine cmdLine = CommandLine.parse(command);
    cmdLine.setSubstitutionMap(substitutionMap);
    try {
      executor.execute(cmdLine);
    } catch (IOException e) {
      final String message = String.format("Command [%s] failed.", command);
      log.error(message, e);
      throw new VcsException(message, e);
    }
  }
}
