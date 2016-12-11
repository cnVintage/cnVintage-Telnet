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

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;

import java.nio.charset.Charset;

/**
 * @author zephray
 */

public class CnvtgTelnet {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        TelnetTerminalServer server = new TelnetTerminalServer(23, Charset.forName("utf-8"));
        //TelnetTerminalServer server = new TelnetTerminalServer(23);
        System.out.println("Waiting for connection");
        
        
        while (true) {
            TelnetTerminal telnetTerminal = server.acceptConnection();
            if (telnetTerminal != null) {
                System.out.print("Connected");
                new ClientThread(telnetTerminal).start();
            }
        }
    }
    
}
