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
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.gui2.table.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;
import java.util.Arrays;
import java.io.IOException;

/**
 *
 * @author zephray
 */
public class frontEnd {
    private final TelnetTerminal terminal;
    private final Screen screen;
    private final MultiWindowTextGUI gui;
    private int selection = 0;
    
    public frontEnd(TelnetTerminal terminal) throws IOException {
        System.out.println("Creating a new frontEnd");
        this.terminal = terminal;
        this.screen = new TerminalScreen(terminal);
        screen.startScreen();
        this.gui = new MultiWindowTextGUI(this.screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
    }
    
    public void showMsg(String title, String msg) throws IOException {
        MessageDialog.showMessageDialog(this.gui, title, msg);
    }
    
    public int doMenu() throws IOException {
        BasicWindow window = new BasicWindow();
        TerminalSize size = new TerminalSize(14, 10);
        ActionListBox actionListBox = new ActionListBox(size);
        
        actionListBox.addItem("登录", () -> {
            selection = 1;
            window.close();
        });
        
        actionListBox.addItem("浏览帖子列表", () -> {
            selection = 2;
            window.close();
        });
        
        actionListBox.addItem("退出", () -> {
            selection = 0;
            window.close();
        });
        
        window.setComponent(actionListBox);
        window.setTitle("主菜单");
        
        this.gui.addWindowAndWait(window);
        
        return this.selection;
    }
    
    public String[] doLogin() throws IOException
    {
        BasicWindow window = new BasicWindow();
        String[] resultSet = new String[2];
        
        System.out.println("Creating a new window");
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));
        
        TextBox txtUser = new TextBox();
        TextBox txtPass = new TextBox();
        txtPass.setMask('*');

        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
            
        panel.addComponent(new Label("用户名"));
        panel.addComponent(txtUser);
            
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        
        panel.addComponent(new Label("密码"));
        panel.addComponent(txtPass);
            
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        panel.addComponent(new Button("Log In 登录", window::close));  

        window.setComponent(panel);
        window.setTitle("欢迎访问cnVintage");
        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        
        this.gui.addWindowAndWait(window);
            
        resultSet[0] = txtUser.getText();
        resultSet[1] = txtPass.getText();
        
        return resultSet;
    }
    
    public int doDiscussionList(discussion[] discussions) {
        BasicWindow window = new BasicWindow();
        Table<String> table = new Table<>("标题", "发起人");
        
        for (discussion discussion : discussions) {
            table.getTableModel().addRow(discussion.title, discussion.startUserName);
            System.out.println(discussion.title);
            System.out.println(TerminalTextUtils.getColumnWidth(discussion.title));
        }
        
        table.setSelectAction(new Runnable() {
            @Override
            public void run() {
                selection = table.getSelectedRow();
                window.close();
            }
        });
        
        window.setComponent(table);
        window.setTitle("主题列表");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        
        this.gui.addWindowAndWait(window);
        
        return selection;
    }
}
