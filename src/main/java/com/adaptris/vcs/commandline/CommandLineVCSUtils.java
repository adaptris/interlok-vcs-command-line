package com.adaptris.vcs.commandline;

import com.adaptris.core.fs.FsHelper;
import com.adaptris.core.management.vcs.VcsException;

import java.io.File;
import java.io.IOException;

class CommandLineVCSUtils {

  private CommandLineVCSUtils(){

  }

  static String fullpath(File file) {
    String result = file.getAbsolutePath();
    try {
      result = file.getCanonicalPath();
    } catch(IOException ignored) {

    }
    return result;
  }

  static File urlToFile(String url) throws VcsException {
    try {
      return FsHelper.createFileReference(FsHelper.createUrlFromString(url, true));
    } catch (Exception e) {
      throw new VcsException(e);
    }
  }
}
