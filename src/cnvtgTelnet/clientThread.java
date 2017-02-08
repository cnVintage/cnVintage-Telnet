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
import java.io.*;
import java.nio.charset.Charset;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import jpty.*;

/**
 *
 * @author zephray
 */
public class ClientThread extends Thread {
    private final TelnetTerminal terminal;
    private Pty pty;
    
    public ClientThread(TelnetTerminal terminal) {
        this.terminal = terminal;
    }
    
    @Override
    public void run() {
        try{
            String[] cmd = { "/bin/sh", "-i"};
            String[] env = { "TERM=xterm" };
            
            pty = JPty.execInPTY(cmd[0], cmd, env);
            
            OutputStream oPty = pty.getOutputStream();
            InputStream iPty = pty.getInputStream();
            OutputStream oTelnet = terminal.getTerminalOutput();
            InputStream iTelnet = terminal.getTerminalInput();
            
            ForwardThread clientForward = new ForwardThread(this, iPty, oTelnet);
            clientForward.start();
            ForwardThread serverForward = new ForwardThread(this, iTelnet, oPty);
            serverForward.start();
            //fif.closeDB();
        } catch (java.lang.IllegalStateException e) {
            System.out.println("One fucking bot.");
        } catch (Exception e) {
            //System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        while (true) {
            
        }
    }
    
    public synchronized void connectionBroken() {
        try {
            terminal.close();
        } catch (Exception e) {}
        try {
            pty.close();
        } catch (Exception e) {}
        System.out.println("Disconnected.");
    }
}
