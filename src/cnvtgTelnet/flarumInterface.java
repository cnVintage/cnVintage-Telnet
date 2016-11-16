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

public class flarumInterface {
    private Connection dbConnect = null;
    private Statement dbStatement = null;
    private ResultSet dbResultSet = null;
            
    public flarumInterface() {
        
    }
    
    public void readDataBase() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnect = DriverManager.getConnection(
                    "jdbc:mysql://localhost/flarum", 
                    "root",
                    "password"
                    );
            dbStatement = dbConnect.createStatement();
            
            dbResultSet = dbStatement.executeQuery("select * from fl_users");
            displayResultSet(dbResultSet);
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }
    
    private void displayResultSet(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            Date jointime = resultSet.getDate("join_time");
            System.out.println("Id: " + id);
            System.out.println("Username: " + username);
            System.out.println("Email" + email);
            System.out.println("Join time " + jointime);
        }
    }
    
    private void close() {
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
}
