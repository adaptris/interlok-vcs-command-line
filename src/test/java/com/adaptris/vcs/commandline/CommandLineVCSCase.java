package com.adaptris.vcs.commandline;

import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import static com.adaptris.core.management.vcs.VcsConstants.VCS_LOCAL_URL_KEY;
import static com.adaptris.core.management.vcs.VcsConstants.VCS_REMOTE_REPO_URL_KEY;
import static com.adaptris.vcs.commandline.CommandLineVCSConstants.*;

abstract class CommandLineVCSCase {

  private static String TEMP_DIR_PROP = "java.io.tmpdir";

  Properties properties;
  File temporaryDir;
  static final String REVISION = "branch";
  static final String COMMIT_MESSAGE = "commit message";
  static final String REMOTE_REPO = "vcs://remote-repo/path";


  @Captor
  ArgumentCaptor<String> captorFilterKey;
  @Captor
  ArgumentCaptor<Map<String, String>> captorSubMap;
  @Captor
  ArgumentCaptor<String> captureRepKey;

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

}