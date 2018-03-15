package com.lumr.test;

import java.io.*;
import java.nio.CharBuffer;

/**
 * Created by work on 2018/3/14.
 *
 * @author lumr
 */
public class FileRead {
    public static void main(String[] args) {
        File file = new File("/Users/work/num.txt");
        System.out.println(file.length());
        byte[] bytes = new byte[(int) file.length()];
        try(Reader reader = new FileReader(file)){
            BufferedReader bufferedReader = new BufferedReader(reader);
            int num;
            int i = 0;
            while (true){
                num = bufferedReader.read();
                if (num == -1)
                    break;
                bytes[i] = (byte) num;
                i++;
            }
            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
