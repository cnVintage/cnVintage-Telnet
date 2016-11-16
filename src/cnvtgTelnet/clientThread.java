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

/**
 *
 * @author zephray
 */
public class clientThread extends Thread {
    private final TelnetTerminal terminal;
    
    public clientThread(TelnetTerminal terminal) {
        this.terminal = terminal;
    }
    
    @Override
    public void run() {
        try{
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();
        
            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(2));
        
            panel.addComponent(new Label("用户名"));
            panel.addComponent(new TextBox());
        
            panel.addComponent(new Label("密码"));
            panel.addComponent(new TextBox());
        
            panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
            panel.addComponent(new Button("Log In 登录"));
        
            BasicWindow window = new BasicWindow();
            window.setComponent(panel);
        
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
            gui.addWindowAndWait(window);
        }catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }finally {
            try {
                terminal.close();
            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}