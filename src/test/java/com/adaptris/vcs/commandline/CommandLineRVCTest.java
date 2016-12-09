package com.adaptris.vcs.commandline;

import com.adaptris.core.management.vcs.VcsConstants;
import com.adaptris.core.management.vcs.VersionControlSystem;
import com.adaptris.core.stubs.JunitBootstrapProperties;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CommandLineRVCTest extends CommandLineVCSCase {

  @Mock
  private CommandLineVCS mockApi;


  @Test
  public void getImplementationName() throws Exception {
    CommandLineRVC rvc = new CommandLineRVC(new JunitBootstrapProperties(properties));
    assertEquals("CommandLineVCS",rvc.getImplementationName());
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