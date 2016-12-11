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
import static com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText;
import static com.googlecode.lanterna.TerminalTextUtils.isCharDoubleWidth;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
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
import java.nio.charset.Charset;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import net.htmlparser.jericho.*;
/**
 *
 * @author zephray
 */
public class FrontEnd {
    private final TelnetTerminal terminal;
    private final Screen screen;
    private final MultiWindowTextGUI gui;
    private int selection = 0;
    private ResourceBundle lang;
    
    private final String mainGreet = 
            "          __     ___       _                   \n" +
            "   ___ _ _\\ \\   / (_)_ __ | |_ __ _  __ _  ___ \n" +
            "  / __| '_ \\ \\ / /| | '_ \\| __/ _` |/ _` |/ _ \\\n" +
            " | (__| | | \\ V / | | | | | || (_| | (_| |  __/\n" +
            "  \\___|_| |_|\\_/  |_|_| |_|\\__\\__,_|\\__, |\\___|\n" +
            "                                    |___/      ";
    
    public FrontEnd(TelnetTerminal terminal) throws IOException {
        System.out.println("Creating a new frontEnd");
        this.terminal = terminal;
        this.screen = new TerminalScreen(terminal);
        screen.startScreen();
        this.gui = new MultiWindowTextGUI(this.screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        lang = ResourceBundle.getBundle("cnvtgTelnet/zh_CN");
    }
    
    public void doCharsetSet() throws IOException {
        BasicWindow window = new BasicWindow();
        Panel panel = new Panel();
        Label lbl1 = new Label("如果你能读懂这句话，请选择UTF-8");
        Label lbl2 = new Label("If it is gibberish, choose GBK.");
        Label lbl3 = new Label("Please choose your charset:");
        Button buttonUnicode = new Button("UTF-8", new Runnable() {
            @Override
            public void run() {
                terminal.setCharset(Charset.forName("utf8"));
                window.close();
            }
        });
        Button buttonGBK = new Button("GBK", new Runnable() {
            @Override
            public void run() {
                terminal.setCharset(Charset.forName("gbk"));
                window.close();
            }
        });
        panel.addComponent(lbl1);
        panel.addComponent(lbl2);
        panel.addComponent(lbl3);
        panel.addComponent(buttonUnicode);
        panel.addComponent(buttonGBK);
        window.setComponent(panel);
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        this.gui.addWindowAndWait(window);
    }
    
    public void showMsg(String title, String msg) throws IOException {
        MessageDialog.showMessageDialog(this.gui, title, msg);
    }
    
    public int doMenu() throws IOException {
        BasicWindow window = new BasicWindow();
        Panel panel = new Panel();
        ActionListBox actionListBox = new ActionListBox(new TerminalSize(50, 4));
        Label lblGreet = new Label(mainGreet);
        
        /*actionListBox.addItem(lang.getString("login"), () -> {
            selection = 1;
            window.close();
        });*/
        
        actionListBox.addItem(lang.getString("discussionList"), () -> {
            selection = 2;
            window.close();
        });
        
        actionListBox.addItem(lang.getString("exit"), () -> {
            selection = 0;
            window.close();
        });
        
        panel.addComponent(lblGreet);
        panel.addComponent(actionListBox);
        
        window.setComponent(panel);
        window.setTitle(lang.getString("mainMenu"));
        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        
        this.gui.addWindowAndWait(window);
        
        return this.selection;
    }
    
    public boolean doLogin(String[] cred) throws IOException
    {
        BasicWindow window = new BasicWindow();

        System.out.println("Creating a new window");
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));
        
        TextBox txtPass = new TextBox();
        txtPass.setMask('*');

        panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
            
        panel.addComponent(new Label(lang.getString("welcomeBack")));
        panel.addComponent(new Label(cred[0]));
            
        panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        
        panel.addComponent(new Label(lang.getString("token")));
        panel.addComponent(txtPass);
            
        panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        panel.addComponent(new EmptySpace(new TerminalSize(0,1)));
        
        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        panel.addComponent(new Button(lang.getString("loginButton"), window::close));  

        window.setComponent(panel);
        window.setTitle(lang.getString("welcome"));
        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        
        this.gui.addWindowAndWait(window);
            
        return (cred[1].equals(txtPass.getText()));
    }
    
    public int doDiscussionList(Discussion[] discussions) {
        BasicWindow window = new BasicWindow();
        Panel panel = new Panel();
        
        Table<String> table = new Table<String>(lang.getString("title"), lang.getString("author")) {
            @Override
            public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.equals(new KeyStroke(KeyType.Escape, false, false))) {
                    selection = -1;
                    window.close();
                    return Interactable.Result.HANDLED;
                } else
                    return super.handleKeyStroke(keyStroke);
            }
        };
        
        for (Discussion discussion : discussions) {
            table.getTableModel().addRow(discussion.title, discussion.startUserName);
        }
        
        table.setSelectAction(new Runnable() {
            @Override
            public void run() {
                selection = discussions[table.getSelectedRow()].id;
                window.close();
            }
        });
        
        WindowListenerAdapter listener = new WindowListenerAdapter() {
            @Override
            public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
                table.setVisibleColumns(newSize.getColumns()-1);
                table.setVisibleRows(newSize.getRows()-1);
                super.onResized(window, oldSize, newSize);
            }
        };
        
        panel.addComponent(table);

        window.setComponent(panel);
        window.setTitle(lang.getString("discussionList"));
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        window.addWindowListener(listener);
        
        this.gui.addWindowAndWait(window);
        
        return selection;
    }
    
    private String formatLines(String target, int maxLength) {

        List<String> result = getWordWrappedText(maxLength, target.split("\n"));
        
        return String.join("\n", result);
    }
    
    private String cleanHtml(String source) {
        Source htmlSource = new Source(source);
        Segment htmlSeg = new Segment(htmlSource, 0, htmlSource.length());
        Renderer htmlRend = new Renderer(htmlSeg);
        return htmlRend.toString();
    }
    
    public void doPostView(Post[] posts) {
        BasicWindow window = new BasicWindow();
        Panel panel = new Panel();
        Label title = new Label("");
        //-1 for Scrollbar fix
        TextBox textBox = new TextBox(screen.getTerminalSize().withRelative(-1, -2)) {
            //Little bit of hack, after create, handleKeyStroke would be called to
            //set this to 0 and display content.
            private int currentPostPos = 1; 

            public void updatePost() {
                this.setText(formatLines(cleanHtml(posts[currentPostPos].content), this.getSize().getColumns()-4));
                title.setText(posts[0].title + " " +
                        posts[currentPostPos].userName + lang.getString("postIn") + 
                        posts[currentPostPos].date.toString() + 
                        " (" + Integer.toString(currentPostPos + 1) + "/" +
                        Integer.toString(posts.length) + ")");
            }
            
            @Override
            public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.equals(new KeyStroke(KeyType.Escape, false, false))) {
                    window.close();
                    return Interactable.Result.HANDLED;
                } else if (keyStroke.equals(new KeyStroke('-', false, false))) {
                    if (currentPostPos > 0) {
                        currentPostPos --;
                        this.updatePost();
                    }   
                    return Interactable.Result.HANDLED;
                } else if (keyStroke.equals(new KeyStroke('=', false, false))) {
                    if (currentPostPos < posts.length - 1) {
                        currentPostPos ++;
                        this.updatePost();
                    }   
                    return Interactable.Result.HANDLED;
                } else
                    return super.handleKeyStroke(keyStroke);
            }
        };
        WindowListenerAdapter listener = new WindowListenerAdapter() {
            @Override
            public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
                textBox.setSize(newSize.withRelative(-1, -2));
                super.onResized(window, oldSize, newSize);
            }
        };
        window.addWindowListener(listener);
        SimpleTheme theme = new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK);
        textBox.setSize(screen.getTerminalSize().withRelative(-1, -2));
        textBox.setTheme(theme);
        textBox.setReadOnly(true);
        textBox.handleKeyStroke(new KeyStroke('-', false, false));
        panel.addComponent(title);
        panel.addComponent(textBox);
        panel.addComponent(new Label(lang.getString("postHint")));
        
        window.setComponent(panel);
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        
        this.gui.addWindowAndWait(window);
    }
}
