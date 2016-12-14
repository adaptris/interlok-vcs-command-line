package com.adaptris.vcs.commandline;

import com.adaptris.core.management.vcs.VcsConstants;
import com.adaptris.core.management.vcs.VersionControlSystem;
import com.adaptris.core.stubs.JunitBootstrapProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Properties;

import static com.adaptris.core.management.vcs.VcsConstants.VCS_LOCAL_URL_KEY;
import static com.adaptris.core.management.vcs.VcsConstants.VCS_REMOTE_REPO_URL_KEY;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CommandLineRVCTest extends CommandLineVCSCase {

  @Mock
  private CommandLineVCS mockApi;

  @Before
  public void setUp() throws Exception{
    String tempDir = System.getProperty(TEMP_DIR_PROP);
    temporaryDir = new File(tempDir);

    properties = new Properties();
    properties.put(VCS_LOCAL_URL_KEY, temporaryDir.toURI().toURL().toString());
    properties.put(VCS_REMOTE_REPO_URL_KEY, REMOTE_REPO);

    MockitoAnnotations.initMocks(this);
  }


  @Test
  public void getImplementationName() throws   Exception {
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    assertEquals("CommandLine",rvc.getImplementationName());
  }

  @Test
  public void updateWithNoConfiguration() throws Exception {
    Properties emptyProperties = new Properties();
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(emptyProperties));
    rvc.setApi(mockApi);
    rvc.update();
    verify(mockApi, never()).update(any(File.class));
    verify(mockApi, never()).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void updateWithNoLocalDirectory() throws Exception {
    File tempFile = new File(temporaryDir, "temp" + Long.toString(System.nanoTime()));
    properties.put(VcsConstants.VCS_LOCAL_URL_KEY, tempFile.toURI().toURL().toString());
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.update();
    verify(mockApi, times(1)).update(any(File.class));
    verify(mockApi, never()).update(any(File.class), any(String.class));
    verify(mockApi, times(1)).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void updateWithNoLocalDirectoryAndRevision() throws Exception {
    File tempFile = new File(temporaryDir, "temp" + Long.toString(System.nanoTime()));
    properties.put(VcsConstants.VCS_LOCAL_URL_KEY, tempFile.toURI().toURL().toString());
    properties.put(VcsConstants.VCS_REVISION_KEY, "revision");
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.update();
    verify(mockApi, never()).update(any(File.class));
    verify(mockApi, times(1)).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, times(1)).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void updateWithLocalDirectory() throws Exception {
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.update();
    verify(mockApi, times(1)).update(any(File.class));
    verify(mockApi, never()).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void updateWithLocalDirectoryAndRevision() throws Exception {
    properties.put(VcsConstants.VCS_REVISION_KEY, "revision");
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.update();
    verify(mockApi, never()).update(any(File.class));
    verify(mockApi, times(1)).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }


  @Test
  public void checkoutWithNoConfiguration() throws Exception {
    Properties emptyProperties = new Properties();
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(emptyProperties));
    rvc.setApi(mockApi);
    rvc.checkout();
    verify(mockApi, never()).update(any(File.class));
    verify(mockApi, never()).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void checkoutWithNoLocalDirectory() throws Exception {
    File tempFile = new File(temporaryDir, "temp" + Long.toString(System.nanoTime()));
    properties.put(VcsConstants.VCS_LOCAL_URL_KEY, tempFile.toURI().toURL().toString());
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.checkout();
    verify(mockApi, times(1)).update(any(File.class));
    verify(mockApi, never()).update(any(File.class), any(String.class));
    verify(mockApi, times(1)).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void checkoutWithNoLocalDirectoryAndRevision() throws Exception {
    File tempFile = new File(temporaryDir, "temp" + Long.toString(System.nanoTime()));
    properties.put(VcsConstants.VCS_LOCAL_URL_KEY, tempFile.toURI().toURL().toString());
    properties.put(VcsConstants.VCS_REVISION_KEY, "revision");
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.update();
    verify(mockApi, never()).update(any(File.class));
    verify(mockApi, times(1)).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, times(1)).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void checkoutWithLocalDirectory() throws Exception {
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.checkout();
    verify(mockApi, times(1)).update(any(File.class));
    verify(mockApi, never()).update(any(File.class), any(String.class));
    verify(mockApi, times(1)).checkout(anyString(), any(File.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class), any(String.class));
  }

  @Test
  public void checkoutWithLocalDirectoryAndRevision() throws Exception {
    properties.put(VcsConstants.VCS_REVISION_KEY, "revision");
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    rvc.setApi(mockApi);
    rvc.checkout();
    verify(mockApi, never()).update(any(File.class));
    verify(mockApi, times(1)).update(any(File.class), any(String.class));
    verify(mockApi, never()).checkout(anyString(), any(File.class));
    verify(mockApi, times(1)).checkout(anyString(), any(File.class), any(String.class));
  }


  @Test
  public void apiWithSetBootstrapProperties() throws Exception {
    CommandLineRVC rvc = new CommandLineRVC();
    rvc.setBootstrapProperties(new JunitBootstrapProperties(properties));
    VersionControlSystem vcs =  rvc.api();
    assertNotNull(vcs);
    assertTrue(vcs instanceof CommandLineVCS);
  }


}