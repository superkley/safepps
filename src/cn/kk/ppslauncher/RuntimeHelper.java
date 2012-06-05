package cn.kk.ppslauncher;

/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class RuntimeHelper {
    private static final String[] PATHS;

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    private static final Runtime RT = Runtime.getRuntime();

    public static boolean SILENT;

    static {
        String path = System.getenv("PATH");
        if (path == null)
            path = System.getenv("path");
        if (path == null) {
            PATHS = new String[0];
        } else {
            PATHS = path.split(File.pathSeparator);
            int i = 0;
            for (String p : PATHS) {
                PATHS[i++] = p.trim() + File.separator;
            }
        }
    }

    public static File findExecutable(final String executable, final String folder) {
        File result = null;
        if (null != (result = getExecutable(executable, folder))) {
            return result;
        }
        for (String p : PATHS) {
            if (null != (result = getExecutable(executable, p + folder))) {
                return result;
            }
        }
        final String pfDirX86 = System.getenv("ProgramFiles(x86)");
        final String pfDir = System.getenv("ProgramFiles");
        final String pfDirW = System.getenv("ProgramW6432");
        if (pfDirX86 != null && result != (result = getExecutable(executable, pfDirX86 + folder))) {
            return result;
        }
        if (pfDir != null && result != (result = getExecutable(executable, pfDir + folder))) {
            return result;
        }
        if (pfDirW != null && result != (result = getExecutable(executable, pfDirW + folder))) {
            return result;
        }
        if (result != (result = getExecutable(executable, System.getenv("SystemDrive") + folder))) {
            return result;
        }
        for (File f : File.listRoots()) {
            final String root = f.getAbsolutePath();
            String path;
            if (pfDirX86 != null) {
                path = root.charAt(0) + pfDirX86.substring(1);
                if (null != (result = getExecutable(executable, path + File.separator + folder))) {
                    return result;
                }
            }
            if (pfDir != null) {
                path = root.charAt(0) + pfDir.substring(1);
                if (null != (result = getExecutable(executable, path + File.separator + folder))) {
                    return result;
                }
            }
            if (pfDirW != null) {
                path = root.charAt(0) + pfDirW.substring(1);
                if (null != (result = getExecutable(executable, path + File.separator + folder))) {
                    return result;
                }
            }
            path = root + "Program Files";
            if (null != (result = getExecutable(executable, path + File.separator + folder))) {
                return result;
            }
            path = root + "Programme";
            if (null != (result = getExecutable(executable, path + File.separator + folder))) {
                return result;
            }
            if (null != (result = getExecutable(executable, root + File.separator + folder))) {
                return result;
            }
        }
        if (result == null && folder.length() != 0) {
            return findExecutable(executable, "");
        } else {
            return result;
        }
    }

    private static final File getExecutable(final String executable, final String path) {
    	System.out.println(path + File.separator+executable);
        if (path == null) {
            return null;
        }
        File file = new File(path, executable);
        if (file.isFile()) {
            return file.getAbsoluteFile();
        } else if ((file = new File(path, executable + ".exe")).isFile()) {
            return file.getAbsoluteFile();
        } else if ((file = new File(path, executable + ".cmd")).isFile()) {
            return file.getAbsoluteFile();
        } else if ((file = new File(path, executable + ".bat")).isFile()) {
            return file.getAbsoluteFile();
        }
        return null;
    }

    public static boolean isWindows() {
        return OS_NAME.startsWith("windows");
    }

    public static boolean isMac() {
        return OS_NAME.startsWith("mac");
    }

    public static boolean isLinux() {
        return OS_NAME.startsWith("linux");
    }

    public static final int execAndWaitCommand(final String cmd) {
        if (!SILENT) {
            System.out.println(cmd);
        }
        try {
            final Process proc = RT.exec(cmd);
            if (!SILENT) {
                new StreamRedirector(proc.getErrorStream(), System.err).start();
                new StreamRedirector(proc.getInputStream(), System.out).start();
                new StreamRedirector(System.in, proc.getOutputStream()).start();
            } else {
                new StreamRedirector(proc.getErrorStream(), null).start();
                new StreamRedirector(proc.getInputStream(), null).start();
            }
            return proc.waitFor();
        } catch (Exception e) {
            if (!SILENT) {
                System.err.println(e.toString());
            }
            return -1;
        }
    }

    public static class StreamRedirector extends Thread {
        private final InputStream from;

        private final OutputStream to;

        public StreamRedirector(InputStream from, OutputStream to) {
            super();
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = from.read(buffer)) != -1) {
                    // System.out.println(new String(buffer, 0, len));
                    if (this.to != null) {
                        this.to.write(buffer, 0, len);
                        if (buffer[len - 1] == '\n') {
                            this.to.flush();
                        }
                    }
                }
            } catch (IOException e) {
                // silent
            }
        }
    }
}
