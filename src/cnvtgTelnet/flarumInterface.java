package cnvtgTelnet;

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

/**
 *
 * @author zephray
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class FlarumInterface {
    private Connection dbConnect = null;
    private Statement dbStatement = null;
    private ResultSet dbResultSet = null;
    
    private final String serverAddr;
    private final String serverUser;
    private final String serverPass;
    private final String dbName;
    
    public FlarumInterface(String serverAddr, String serverUser, String serverPass, String dbName) {
        this.serverAddr = serverAddr;
        this.serverUser = serverUser;
        this.serverPass = serverPass;
        this.dbName = dbName;
    }
    
    private void connectDB() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        dbConnect = DriverManager.getConnection(
                    "jdbc:mysql://"+serverAddr+"/"+dbName, 
                    serverUser,
                    serverPass
                    );
        dbStatement = dbConnect.createStatement();
    }
    
    private void closeDB() {
        try {
            if (dbResultSet != null) {
                dbResultSet.close();
            }
            if (dbStatement != null) {
                dbStatement.close();
            }
            if (dbConnect != null) {
                dbConnect.close();
            }
        } catch (Exception e) {
            
        }
    }
    
    public String[] verifyIP(String ip) throws Exception {
        if ((ip.indexOf(' ')!=-1) || 
            (ip.indexOf(';')!=-1) ||  
            (ip.indexOf('\"')!=-1) ||  
            (ip.indexOf('\'')!=-1))
            return null;
        this.connectDB();
        try {
            String sql = "SELECT fl_telnet_access_tokens.access_token, fl_users.username FROM fl_telnet_access_tokens " +
                        "INNER JOIN fl_users " +
                        "ON fl_users.id = fl_telnet_access_tokens.user_id " +
                        "WHERE fl_telnet_access_tokens.remote_addr = \"" + ip +"\"";
            dbResultSet = dbStatement.executeQuery(sql);
            if (dbResultSet.next()) {
                String token = dbResultSet.getString("access_token");
                String username = dbResultSet.getString("username");
                this.closeDB();
                String[] cred = new String[2];
                cred[0] = username; cred[1] = token;
                return cred;
            } else {
                this.closeDB();
                return null;
            }
        } catch(SQLException se) {
            this.closeDB();
            throw se;
        }
    }
    
    public Discussion[] getDiscussions() throws Exception {
        Discussion[] discussionSet;
        this.connectDB();
        try {
            String sql;
            int discussionCount;
            sql =   "SELECT fl_discussions.id, fl_discussions.title, " +
                    "       fl_discussions.comments_count, fl_discussions.last_time, " +
                    "       fl_discussions.start_user_id, fl_discussions.last_user_id, " +
                    "       fl_discussions.is_sticky, " +
                    "       user1.username as start_user_name, " +
                    "       user2.username as last_user_name " +
                    "FROM  fl_discussions " +
                    "INNER JOIN fl_users user1 " +
                    "   ON user1.id = start_user_id " +
                    "INNER JOIN fl_users user2 " +
                    "   ON user2.id = last_user_id " +
                    "WHERE fl_discussions.comments_count != 0 " +
                    "ORDER BY fl_discussions.last_time DESC";
            dbResultSet = dbStatement.executeQuery(sql);
            dbResultSet.last();
            discussionCount = dbResultSet.getRow();
            System.out.print(discussionCount); System.out.println(" discussions in total.");
            discussionSet = new Discussion[discussionCount];
            dbResultSet.first();
            for (int i = 0; i < discussionCount; i++) {
                discussionSet[i] = new Discussion();
                discussionSet[i].id = dbResultSet.getInt("id");
                discussionSet[i].isSticky = dbResultSet.getBoolean("is_sticky");
                discussionSet[i].lastTime = dbResultSet.getDate("last_time");
                discussionSet[i].lastUserId = dbResultSet.getInt("last_user_id");
                discussionSet[i].lastUserName = dbResultSet.getString("last_user_name");
                discussionSet[i].startUserId = dbResultSet.getInt("start_user_id");
                discussionSet[i].startUserName = dbResultSet.getString("start_user_name");
                discussionSet[i].title = dbResultSet.getString("title");
                dbResultSet.next();
            }
            this.closeDB();
            return discussionSet;
        } catch(SQLException se) {
            this.closeDB();
            throw se;
        }
    }
    
    public Post[] getPosts(int discussionId) throws Exception {
        Post[] postSet;
        this.connectDB();
        try {
            String sql;
            int postCount;
            sql =   "SELECT fl_posts.content, fl_posts.time, fl_posts.user_id, " +
                    "       fl_discussions.title, " + 
                    "       fl_users.username " + 
                    "FROM fl_posts " +
                    "INNER JOIN fl_users " +
                    "ON fl_users.id = fl_posts.user_id " +
                    "INNER JOIN fl_discussions " +
                    "ON fl_discussions.id = discussion_id " +
                    "WHERE fl_discussions.id = "+ discussionId +
                    " AND fl_posts.hide_user_id IS NULL AND fl_posts.type = 'comment';";
            dbResultSet = dbStatement.executeQuery(sql);
            dbResultSet.last();
            postCount = dbResultSet.getRow();
            System.out.print(postCount); System.out.println(" posts in total.");
            postSet = new Post[postCount];
            dbResultSet.first();
            for (int i = 0; i < postCount; i++) {
                postSet[i] = new Post();
                postSet[i].title = dbResultSet.getString("title");
                postSet[i].content = dbResultSet.getString("content");
                postSet[i].date = dbResultSet.getDate("time");
                postSet[i].userId = dbResultSet.getInt("user_id");
                postSet[i].userName = dbResultSet.getString("username");
                dbResultSet.next();
            }
            this.closeDB();
            return postSet;
        } catch(SQLException se) {
            this.closeDB();
            throw se;
        }
    }
}
