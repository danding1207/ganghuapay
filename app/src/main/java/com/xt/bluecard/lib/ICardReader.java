//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib;

public interface ICardReader {
    boolean init();

    long sendApdu(byte[] var1, ByteResult var2, ByteResult var3, long var4);

    void close();
}
