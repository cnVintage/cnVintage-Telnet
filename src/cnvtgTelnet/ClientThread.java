/*
 * Copyright (C) 2017 zephray
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
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *
 * @author zephray
 */
public class ClientThread extends Thread {
    private final Terminal terminal;
    
    public ClientThread(Terminal terminal) {
        this.terminal = terminal;
    }
    
    @Override
    public void run() {
        FrontEnd fend;
        FlarumInterface fif;
        boolean continueLoop = true;
        int lastSelection;
        try{
            fend = new FrontEnd(terminal);
            fif = new FlarumInterface("localhost", "root", "123456", "flarum");
            continueLoop = true;
                
            while (continueLoop) {
                lastSelection = fend.doMenu();
                switch (lastSelection) {
                    case 0: continueLoop = false;
                            break;
                    case 2: while ((lastSelection = fend.doDiscussionList(fif.getDiscussions()))!= -1) {
                                fend.doPostView(fif.getPosts(lastSelection));
                            }
                            break;
                }
            }
        } catch (java.lang.IllegalStateException e) {
            System.out.print("One fucking bot.");//IllegalStateException is most likely caused by a bot
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                terminal.close();
                System.out.println("Disconnected.");
            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}
