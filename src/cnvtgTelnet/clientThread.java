/*
 * Copyright (C) 2016 zephray
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cnvtgTelnet;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author zephray
 */
public class ClientThread extends Thread {
    private final TelnetTerminal terminal;
    
    public ClientThread(TelnetTerminal terminal) {
        this.terminal = terminal;
    }
    
    @Override
    public void run() {
        FrontEnd fend;
        FlarumInterface fif;
        boolean continueLoop = true;
        boolean fallbackGBK = false;
        int lastSelection;
        try{
            fend = new FrontEnd(terminal);
            fif = new FlarumInterface("localhost", "root", "123456", "flarum");
            fif.connectDB();
            
            while (continueLoop) {
                lastSelection = fend.doMenu();
                switch (lastSelection) {
                    case 0: continueLoop = false;
                            break;
                    case 1: String[] cred = fend.doLogin();
                            Boolean success = fif.verifyCred(cred);
                            fend.showMsg("提示", success?"登录成功":"登录失败");
                            break;
                    case 2: while ((lastSelection = fend.doDiscussionList(fif.getDiscussions()))!= -1) {
                                fend.doPostView(fif.getPosts(lastSelection));
                            }
                            break;
                    case 3: if (fallbackGBK) {
                                terminal.setCharset(Charset.forName("utf-8"));
                                fallbackGBK = false;
                            } else {
                                terminal.setCharset(Charset.forName("gbk"));
                                fallbackGBK = true;
                            }
                            break;
                }
            }
            
            fif.closeDB();
        }catch (Exception e) {
            //System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }finally {
            try {
                terminal.close();
            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}
