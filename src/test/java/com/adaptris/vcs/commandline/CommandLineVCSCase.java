package com.adaptris.vcs.commandline;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.io.File;
import java.util.Map;
import java.util.Properties;

abstract class CommandLineVCSCase {

  static String TEMP_DIR_PROP = "java.io.tmpdir";

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
  @Captor
  ArgumentCaptor<File> captureWorkingDir;



}