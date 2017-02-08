package cnvtgTelnet;

/*
 * Copyright (C) 2017 ZephRay
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
import java.io.*;
/**
 *
 * @author ZephRay
 */
public class ForwardThread extends Thread{
    private static final int BUFFER_SIZE = 8192;
    
    InputStream mInputStream;
    OutputStream mOutputStream;
    ClientThread mParent;
    
    public ForwardThread(ClientThread aParent, InputStream aInputStream, 
            OutputStream aOutputStream) {
        mParent = aParent;
        mInputStream = aInputStream;
        mOutputStream = aOutputStream;
    }
    
    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            while(true) {
                int bytesRead = mInputStream.read(buffer);
                if (bytesRead != -1)
                {
                    mOutputStream.write(buffer, 0, bytesRead);
                    mOutputStream.flush();
                }
            }
        } catch (IOException e) {
            
        }
        mParent.connectionBroken();
    }
}
