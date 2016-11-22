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

import cnvtgTelnet.discussion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.mindrot.BCrypt;

public class flarumInterface {
    private Connection dbConnect = null;
    private Statement dbStatement = null;
    private ResultSet dbResultSet = null;
    
    private final String serverAddr;
    private final String serverUser;
    private final String serverPass;
    private final String dbName;
    
    public flarumInterface(String serverAddr, String serverUser, String serverPass, String dbName) {
        this.serverAddr = serverAddr;
        this.serverUser = serverUser;
        this.serverPass = serverPass;
        this.dbName = dbName;
    }
    
    public void connectDB() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        dbConnect = DriverManager.getConnection(
                    "jdbc:mysql://"+serverAddr+"/"+dbName, 
                    serverUser,
                    serverPass
                    );
        dbStatement = dbConnect.createStatement();
    }
    
    public void closeDB() {
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
    
    public boolean verifyCred(String[] cred) {
        try {
            String sql = "SELECT password FROM fl_users WHERE username=\"" + cred[0] +"\"";
            dbResultSet = dbStatement.executeQuery(sql);
            if (dbResultSet.next()) {
                String hashed = "$2a" + dbResultSet.getString("password").substring(3);
                String pw = cred[1];
                System.out.println(hashed);
                System.out.println(pw);
                return BCrypt.checkpw(pw, hashed);
            } else
                return false; //No result
        } catch(SQLException se) {
            System.err.println(se.getClass().getName() + ": " + se.getMessage());
        }
        return false;
    }
    
    public discussion[] getDiscussions() {
        discussion[] discussionSet;
        try {
            String sql;
            int discussionCount;
            sql =   "SELECT fl_discussions.id, fl_discussions.title, " +
                    "       fl_discussions.comments_count, fl_discussions.last_time, " +
                    "       fl_discussions.start_user_id, fl_discussions.last_user_id, " +
                    "       fl_discussions.is_sticky, " +
                    "       user1.avatar_path, user1.username as start_user_name, " +
                    "       user2.username as last_user_name " +
                    "FROM  fl_discussions " +
                    "INNER JOIN fl_users user1 " +
                    "   ON user1.id = start_user_id " +
                    "INNER JOIN fl_users user2 " +
                    "   ON user2.id = last_user_id " +
                    "ORDER BY fl_discussions.last_time DESC";
            dbResultSet = dbStatement.executeQuery(sql);
            dbResultSet.last();
            discussionCount = dbResultSet.getRow();
            System.out.print(discussionCount); System.out.println(" discussions in total.");
            discussionSet = new discussion[discussionCount];
            dbResultSet.first();
            for (int i = 0; i < discussionCount; i++) {
                discussionSet[i] = new discussion();
                discussionSet[i].isSticky = dbResultSet.getBoolean("is_sticky");
                discussionSet[i].lastTime = dbResultSet.getDate("last_time");
                discussionSet[i].lastUserId = dbResultSet.getInt("last_user_id");
                discussionSet[i].lastUserName = dbResultSet.getString("last_user_name");
                discussionSet[i].startUserId = dbResultSet.getInt("start_user_id");
                discussionSet[i].startUserName = dbResultSet.getString("start_user_name");
                discussionSet[i].title = dbResultSet.getString("title");
                dbResultSet.next();
            }
            
            return discussionSet;
        } catch(SQLException se) {
            System.err.println(se.getClass().getName() + ": " + se.getMessage());
        }
        return null;
    }
}
