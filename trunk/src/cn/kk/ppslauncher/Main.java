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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    private final static String PPS_CMD = "PPStream.exe";

    private final static String APPDATA_DIR = System.getenv("APPDATA");

    private final static String PPS_DATA_DIR = APPDATA_DIR + "\\PPStream";

    private final static String BANNER_DIR = APPDATA_DIR + "\\PPStream\\banner";

    private final static String ADSYS_DIR = APPDATA_DIR + "\\PPStream\\adsys";

    private static final String TITLE = "GreenPPS（没有广告的PPS）";

    private void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // silent
        }

        RuntimeHelper.SILENT = true;
        RuntimeHelper.execAndWaitCommand("taskkill /f /im \"" + PPS_CMD + "\" /t");

        try {
            // System.out.println(PPS_DATA_DIR);
            if (new File(PPS_DATA_DIR).exists()) {
                RuntimeHelper.execAndWaitCommand("cmd /c rmdir /s /q \"" + BANNER_DIR + "\"");
                new RuntimeHelper.StreamRedirector(System.in, new FileOutputStream(BANNER_DIR)).start();
                RuntimeHelper.execAndWaitCommand("cmd /c rmdir /s /q \"" + ADSYS_DIR + "\"");
                new RuntimeHelper.StreamRedirector(System.in, new FileOutputStream(ADSYS_DIR)).start();

                File cmd = RuntimeHelper.findExecutable(PPS_CMD, "PPStream");
                if (cmd == null) {
                    cmd = RuntimeHelper.findExecutable(PPS_CMD, "PPS.tv");
                }
                if (cmd != null) {
                    RuntimeHelper.execAndWaitCommand(cmd.getAbsolutePath());
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(null, "没有找到PPS主程序！请上PPS官方网站下载并重新安装PPS！", TITLE,
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-5);
                }
            } else {
                JOptionPane.showMessageDialog(null, "PPS没有安装！请先上PPS官方网站下载并安装PPS！", TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(-9);
            }
        } catch (Exception e) {
            System.err.println("用户没有足够的权限（请使用高级用户运行本软件！）：" + e.toString());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }
}
