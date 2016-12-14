package com.adaptris.vcs.commandline;

import com.adaptris.core.management.vcs.RevisionHistoryItem;
import com.adaptris.core.management.vcs.VcsException;
import org.apache.commons.exec.Executor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.adaptris.core.management.vcs.VcsConstants.VCS_LOCAL_URL_KEY;
import static com.adaptris.core.management.vcs.VcsConstants.VCS_REMOTE_REPO_URL_KEY;
import static com.adaptris.core.management.vcs.VcsConstants.VCS_REVISION_KEY;
import static com.adaptris.vcs.commandline.CommandLineVCSConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommandLineVCSTest extends CommandLineVCSCase {

  @Before
  public void setUp() throws Exception{
    String tempDir = System.getProperty(TEMP_DIR_PROP);
    temporaryDir = new File(tempDir);

    properties = new Properties();
    properties.put(VCS_LOCAL_URL_KEY, temporaryDir.toURI().toURL().toString());
    properties.put(VCS_REMOTE_REPO_URL_KEY, REMOTE_REPO);

    properties.put(VCS_COMMAND_LINE_TEST_CONNECTION, "echo -n \"test.connection\"");
    properties.put(VCS_COMMAND_LINE_CHECKOUT, "echo -n \"checkout\"");
    properties.put(VCS_COMMAND_LINE_UPDATE, "echo -n \"update\"");
    properties.put(VCS_COMMAND_LINE_COMMIT, "echo -n \"commit\"");
    properties.put(VCS_COMMAND_LINE_RECURSIVE_ADD, "echo -n \"recursive.add\"");
    properties.put(VCS_COMMAND_LINE_ADD_AND_COMMIT, "echo -n \"add.and.commit\"");
    properties.put(VCS_COMMAND_LINE_REMOTE_REVISION, "echo -n \"remote.revision\"");
    properties.put(VCS_COMMAND_LINE_LOCAL_REVISION, "echo -n \"local.revision\"");
    properties.put(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, "echo -n \"revision comment\"");

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testConnection() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.testConnection(REMOTE_REPO, temporaryDir);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(), captureWorkingDir.capture(), captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("test.connection", result);
    assertEquals(VCS_COMMAND_LINE_TEST_CONNECTION, captorFilterKey.getValue());
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void checkout() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.checkout(REMOTE_REPO, temporaryDir);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("checkout", result);
    assertEquals(VCS_COMMAND_LINE_CHECKOUT, captorFilterKey.getValue());
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void checkoutWithRevision() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.checkout(REMOTE_REPO, temporaryDir, REVISION);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("checkout", result);
    assertEquals(VCS_COMMAND_LINE_CHECKOUT, captorFilterKey.getValue());
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REVISION, captorSubMap.getValue().get(VCS_REVISION_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void update() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.update(temporaryDir);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("update", result);
    assertEquals(VCS_COMMAND_LINE_UPDATE, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void updateWithRevision() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.update(temporaryDir, REVISION);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("update", result);
    assertEquals(VCS_COMMAND_LINE_UPDATE, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REVISION, captorSubMap.getValue().get(VCS_REVISION_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void commit() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    vcs.commit(temporaryDir, COMMIT_MESSAGE);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_COMMIT, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(COMMIT_MESSAGE, captorSubMap.getValue().get(VCS_COMMIT_MESSAGE_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void recursiveAdd() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    vcs.recursiveAdd(temporaryDir);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_RECURSIVE_ADD, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void addAndCommit() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    vcs.addAndCommit(temporaryDir, COMMIT_MESSAGE, "file1");
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_ADD_AND_COMMIT, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(COMMIT_MESSAGE, captorSubMap.getValue().get(VCS_COMMIT_MESSAGE_KEY));
    assertEquals("file1", captorSubMap.getValue().get(VCS_LOCAL_FILE_KEY + ".0"));
    assertEquals(VCS_LOCAL_FILE_KEY, captureRepKey.getValue());
  }

  @Test
  public void addAndCommitMultipleFiles() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    vcs.addAndCommit(temporaryDir, COMMIT_MESSAGE, "file1", "file2");
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(2)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_ADD_AND_COMMIT, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(COMMIT_MESSAGE, captorSubMap.getValue().get(VCS_COMMIT_MESSAGE_KEY));
    assertEquals("file1", captorSubMap.getValue().get(VCS_LOCAL_FILE_KEY + ".0"));
    assertEquals("file2", captorSubMap.getValue().get(VCS_LOCAL_FILE_KEY + ".1"));
    assertEquals(VCS_LOCAL_FILE_KEY, captureRepKey.getValue());
  }

  @Test
  public void getRemoteRevision() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.getRemoteRevision(REMOTE_REPO, temporaryDir);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("remote.revision", result);
    assertEquals(VCS_COMMAND_LINE_REMOTE_REVISION, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void getLocalRevision() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.getLocalRevision(temporaryDir);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("local.revision", result);
    assertEquals(VCS_COMMAND_LINE_LOCAL_REVISION, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertNull(captureRepKey.getValue());
  }

  @Test
  public void getRemoteRevisionHistoryNotEnoughItems() throws Exception {
    properties.put(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, "echo -n \"revisioncomment\"");
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    List<RevisionHistoryItem> result = vcs.getRemoteRevisionHistory(REMOTE_REPO, temporaryDir, 1);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals("1", captorSubMap.getValue().get(VCS_LIMIT_KEY));
    assertNull(captureRepKey.getValue());
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void getRemoteRevisionHistory() throws Exception {
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    List<RevisionHistoryItem> result = vcs.getRemoteRevisionHistory(REMOTE_REPO, temporaryDir, 1);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals("1", captorSubMap.getValue().get(VCS_LIMIT_KEY));
    assertNull(captureRepKey.getValue());
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("revision", result.get(0).getRevision());
    assertEquals("comment", result.get(0).getComment());
  }

  @Test
  public void getRemoteRevisionHistoryWithSpaces() throws Exception {
    properties.put(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, "echo -n \"revision comment something else\"");
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    List<RevisionHistoryItem> result = vcs.getRemoteRevisionHistory(REMOTE_REPO, temporaryDir, 1);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals("1", captorSubMap.getValue().get(VCS_LIMIT_KEY));
    assertNull(captureRepKey.getValue());
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("revision", result.get(0).getRevision());
    assertEquals("comment something else", result.get(0).getComment());
  }

  @Test
  public void getRemoteRevisionHistoryMultiLine() throws Exception {
    properties.put(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, "echo -n \"revision1 comment1 something else\nrevision2 comment2 something else\"");
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    List<RevisionHistoryItem> result = vcs.getRemoteRevisionHistory(REMOTE_REPO, temporaryDir, 1);
    verify(vcs, times(1)).commandLineAction(captorFilterKey.capture(),captorSubMap.capture(),captureWorkingDir.capture(),captureRepKey.capture());
    verify(vcs, times(1)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals(VCS_COMMAND_LINE_REMOTE_REVISION_HISTORY, captorFilterKey.getValue());
    assertEquals(temporaryDir.getAbsolutePath(), captorSubMap.getValue().get(VCS_LOCAL_URL_KEY));
    assertEquals(REMOTE_REPO, captorSubMap.getValue().get(VCS_REMOTE_REPO_URL_KEY));
    assertEquals("1", captorSubMap.getValue().get(VCS_LIMIT_KEY));
    assertNull(captureRepKey.getValue());
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("revision1", result.get(0).getRevision());
    assertEquals("comment1 something else", result.get(0).getComment());
    assertEquals("revision2", result.get(1).getRevision());
    assertEquals("comment2 something else", result.get(1).getComment());
  }

  @Test
  public void commandLineActionNoCommands() throws Exception {
    Properties properties = new Properties();
    properties.put(VCS_LOCAL_URL_KEY, temporaryDir.toURI().toURL().toString());
    properties.put(VCS_REMOTE_REPO_URL_KEY, REMOTE_REPO);
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    try {
      String result = vcs.commandLineAction("no.command", new HashMap<String, String>(), null);
      fail();
    } catch (VcsException expected){
      assertEquals("CommandLine: Commands for [no.command] not configured.", expected.getMessage());
    }
    verify(vcs, never()).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
  }

  @Test
  public void commandLineActionMultipleCommands() throws Exception {
    Properties properties = new Properties();
    properties.put(VCS_LOCAL_URL_KEY, temporaryDir.toURI().toURL().toString());
    properties.put(VCS_REMOTE_REPO_URL_KEY, REMOTE_REPO);
    properties.put("multi.command.0", "echo -n \"multi.command.0...\"");
    properties.put("multi.command.1", "echo -n \"multi.command.1\"");
    CommandLineVCS vcs = Mockito.spy(new CommandLineVCS(properties));
    String result = vcs.commandLineAction("multi.command",new HashMap<String, String>(), temporaryDir);
    verify(vcs, times(2)).executeCommand(any(Executor.class),anyString(),anyMapOf(String.class, String.class));
    assertEquals("multi.command.0...multi.command.1", result);
  }

  @Test
  public void getImplementationName() throws Exception {
    CommandLineVCS vcs = new CommandLineVCS(properties);
    assertEquals("CommandLine",vcs.getImplementationName());
  }

  @Test
  public void getRepeatedKeys() throws Exception {
    CommandLineVCS vcs = new CommandLineVCS(properties);
    Map<String, String> map = new HashMap<>();
    map.put("file.0", "file0");
    map.put("file.1", "file1");
    map.put("file.2", "file2");
    map.put("notfile", "notfile");
    List<String> keys = vcs.getRepeatedKeys(map,"file");
    assertEquals(3, keys.size());
    assertTrue(keys.contains("file.0"));
    assertTrue(keys.contains("file.1"));
    assertTrue(keys.contains("file.2"));
  }

}