package com.robert.chapter07;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> src = new ArrayList<>();
        src.add("msgpack");
        src.add("kumofs");
        src.add("viver");
        MessagePack pack = new MessagePack();
        try {
            byte[] write = pack.write(src);
            Value read = pack.read(write);
            System.out.println(read.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
