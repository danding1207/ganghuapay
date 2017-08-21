//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xtkj.nfcjar;

import android.annotation.SuppressLint;

@SuppressLint({"DefaultLocale"})
public class DesEntry {
    static final int i_Con_Encrypt = 1;
    static final int i_Con_Decrypt = 0;
    byte[] BitIP = new byte[]{(byte)57, (byte)49, (byte)41, (byte)33, (byte)25, (byte)17, (byte)9, (byte)1, (byte)59, (byte)51, (byte)43, (byte)35, (byte)27, (byte)19, (byte)11, (byte)3, (byte)61, (byte)53, (byte)45, (byte)37, (byte)29, (byte)21, (byte)13, (byte)5, (byte)63, (byte)55, (byte)47, (byte)39, (byte)31, (byte)23, (byte)15, (byte)7, (byte)56, (byte)48, (byte)40, (byte)32, (byte)24, (byte)16, (byte)8, (byte)0, (byte)58, (byte)50, (byte)42, (byte)34, (byte)26, (byte)18, (byte)10, (byte)2, (byte)60, (byte)52, (byte)44, (byte)36, (byte)28, (byte)20, (byte)12, (byte)4, (byte)62, (byte)54, (byte)46, (byte)38, (byte)30, (byte)22, (byte)14, (byte)6};
    byte[] BitCP = new byte[]{(byte)39, (byte)7, (byte)47, (byte)15, (byte)55, (byte)23, (byte)63, (byte)31, (byte)38, (byte)6, (byte)46, (byte)14, (byte)54, (byte)22, (byte)62, (byte)30, (byte)37, (byte)5, (byte)45, (byte)13, (byte)53, (byte)21, (byte)61, (byte)29, (byte)36, (byte)4, (byte)44, (byte)12, (byte)52, (byte)20, (byte)60, (byte)28, (byte)35, (byte)3, (byte)43, (byte)11, (byte)51, (byte)19, (byte)59, (byte)27, (byte)34, (byte)2, (byte)42, (byte)10, (byte)50, (byte)18, (byte)58, (byte)26, (byte)33, (byte)1, (byte)41, (byte)9, (byte)49, (byte)17, (byte)57, (byte)25, (byte)32, (byte)0, (byte)40, (byte)8, (byte)48, (byte)16, (byte)56, (byte)24};
    int[] BitExp = new int[]{31, 0, 1, 2, 3, 4, 3, 4, 5, 6, 7, 8, 7, 8, 9, 10, 11, 12, 11, 12, 13, 14, 15, 16, 15, 16, 17, 18, 19, 20, 19, 20, 21, 22, 23, 24, 23, 24, 25, 26, 27, 28, 27, 28, 29, 30, 31, 0};
    byte[] BitPM = new byte[]{(byte)15, (byte)6, (byte)19, (byte)20, (byte)28, (byte)11, (byte)27, (byte)16, (byte)0, (byte)14, (byte)22, (byte)25, (byte)4, (byte)17, (byte)30, (byte)9, (byte)1, (byte)7, (byte)23, (byte)13, (byte)31, (byte)26, (byte)2, (byte)8, (byte)18, (byte)12, (byte)29, (byte)5, (byte)21, (byte)10, (byte)3, (byte)24};
    byte[][] sBox = new byte[][]{{(byte)14, (byte)4, (byte)13, (byte)1, (byte)2, (byte)15, (byte)11, (byte)8, (byte)3, (byte)10, (byte)6, (byte)12, (byte)5, (byte)9, (byte)0, (byte)7, (byte)0, (byte)15, (byte)7, (byte)4, (byte)14, (byte)2, (byte)13, (byte)1, (byte)10, (byte)6, (byte)12, (byte)11, (byte)9, (byte)5, (byte)3, (byte)8, (byte)4, (byte)1, (byte)14, (byte)8, (byte)13, (byte)6, (byte)2, (byte)11, (byte)15, (byte)12, (byte)9, (byte)7, (byte)3, (byte)10, (byte)5, (byte)0, (byte)15, (byte)12, (byte)8, (byte)2, (byte)4, (byte)9, (byte)1, (byte)7, (byte)5, (byte)11, (byte)3, (byte)14, (byte)10, (byte)0, (byte)6, (byte)13}, {(byte)15, (byte)1, (byte)8, (byte)14, (byte)6, (byte)11, (byte)3, (byte)4, (byte)9, (byte)7, (byte)2, (byte)13, (byte)12, (byte)0, (byte)5, (byte)10, (byte)3, (byte)13, (byte)4, (byte)7, (byte)15, (byte)2, (byte)8, (byte)14, (byte)12, (byte)0, (byte)1, (byte)10, (byte)6, (byte)9, (byte)11, (byte)5, (byte)0, (byte)14, (byte)7, (byte)11, (byte)10, (byte)4, (byte)13, (byte)1, (byte)5, (byte)8, (byte)12, (byte)6, (byte)9, (byte)3, (byte)2, (byte)15, (byte)13, (byte)8, (byte)10, (byte)1, (byte)3, (byte)15, (byte)4, (byte)2, (byte)11, (byte)6, (byte)7, (byte)12, (byte)0, (byte)5, (byte)14, (byte)9}, {(byte)10, (byte)0, (byte)9, (byte)14, (byte)6, (byte)3, (byte)15, (byte)5, (byte)1, (byte)13, (byte)12, (byte)7, (byte)11, (byte)4, (byte)2, (byte)8, (byte)13, (byte)7, (byte)0, (byte)9, (byte)3, (byte)4, (byte)6, (byte)10, (byte)2, (byte)8, (byte)5, (byte)14, (byte)12, (byte)11, (byte)15, (byte)1, (byte)13, (byte)6, (byte)4, (byte)9, (byte)8, (byte)15, (byte)3, (byte)0, (byte)11, (byte)1, (byte)2, (byte)12, (byte)5, (byte)10, (byte)14, (byte)7, (byte)1, (byte)10, (byte)13, (byte)0, (byte)6, (byte)9, (byte)8, (byte)7, (byte)4, (byte)15, (byte)14, (byte)3, (byte)11, (byte)5, (byte)2, (byte)12}, {(byte)7, (byte)13, (byte)14, (byte)3, (byte)0, (byte)6, (byte)9, (byte)10, (byte)1, (byte)2, (byte)8, (byte)5, (byte)11, (byte)12, (byte)4, (byte)15, (byte)13, (byte)8, (byte)11, (byte)5, (byte)6, (byte)15, (byte)0, (byte)3, (byte)4, (byte)7, (byte)2, (byte)12, (byte)1, (byte)10, (byte)14, (byte)9, (byte)10, (byte)6, (byte)9, (byte)0, (byte)12, (byte)11, (byte)7, (byte)13, (byte)15, (byte)1, (byte)3, (byte)14, (byte)5, (byte)2, (byte)8, (byte)4, (byte)3, (byte)15, (byte)0, (byte)6, (byte)10, (byte)1, (byte)13, (byte)8, (byte)9, (byte)4, (byte)5, (byte)11, (byte)12, (byte)7, (byte)2, (byte)14}, {(byte)2, (byte)12, (byte)4, (byte)1, (byte)7, (byte)10, (byte)11, (byte)6, (byte)8, (byte)5, (byte)3, (byte)15, (byte)13, (byte)0, (byte)14, (byte)9, (byte)14, (byte)11, (byte)2, (byte)12, (byte)4, (byte)7, (byte)13, (byte)1, (byte)5, (byte)0, (byte)15, (byte)10, (byte)3, (byte)9, (byte)8, (byte)6, (byte)4, (byte)2, (byte)1, (byte)11, (byte)10, (byte)13, (byte)7, (byte)8, (byte)15, (byte)9, (byte)12, (byte)5, (byte)6, (byte)3, (byte)0, (byte)14, (byte)11, (byte)8, (byte)12, (byte)7, (byte)1, (byte)14, (byte)2, (byte)13, (byte)6, (byte)15, (byte)0, (byte)9, (byte)10, (byte)4, (byte)5, (byte)3}, {(byte)12, (byte)1, (byte)10, (byte)15, (byte)9, (byte)2, (byte)6, (byte)8, (byte)0, (byte)13, (byte)3, (byte)4, (byte)14, (byte)7, (byte)5, (byte)11, (byte)10, (byte)15, (byte)4, (byte)2, (byte)7, (byte)12, (byte)9, (byte)5, (byte)6, (byte)1, (byte)13, (byte)14, (byte)0, (byte)11, (byte)3, (byte)8, (byte)9, (byte)14, (byte)15, (byte)5, (byte)2, (byte)8, (byte)12, (byte)3, (byte)7, (byte)0, (byte)4, (byte)10, (byte)1, (byte)13, (byte)11, (byte)6, (byte)4, (byte)3, (byte)2, (byte)12, (byte)9, (byte)5, (byte)15, (byte)10, (byte)11, (byte)14, (byte)1, (byte)7, (byte)6, (byte)0, (byte)8, (byte)13}, {(byte)4, (byte)11, (byte)2, (byte)14, (byte)15, (byte)0, (byte)8, (byte)13, (byte)3, (byte)12, (byte)9, (byte)7, (byte)5, (byte)10, (byte)6, (byte)1, (byte)13, (byte)0, (byte)11, (byte)7, (byte)4, (byte)9, (byte)1, (byte)10, (byte)14, (byte)3, (byte)5, (byte)12, (byte)2, (byte)15, (byte)8, (byte)6, (byte)1, (byte)4, (byte)11, (byte)13, (byte)12, (byte)3, (byte)7, (byte)14, (byte)10, (byte)15, (byte)6, (byte)8, (byte)0, (byte)5, (byte)9, (byte)2, (byte)6, (byte)11, (byte)13, (byte)8, (byte)1, (byte)4, (byte)10, (byte)7, (byte)9, (byte)5, (byte)0, (byte)15, (byte)14, (byte)2, (byte)3, (byte)12}, {(byte)13, (byte)2, (byte)8, (byte)4, (byte)6, (byte)15, (byte)11, (byte)1, (byte)10, (byte)9, (byte)3, (byte)14, (byte)5, (byte)0, (byte)12, (byte)7, (byte)1, (byte)15, (byte)13, (byte)8, (byte)10, (byte)3, (byte)7, (byte)4, (byte)12, (byte)5, (byte)6, (byte)11, (byte)0, (byte)14, (byte)9, (byte)2, (byte)7, (byte)11, (byte)4, (byte)1, (byte)9, (byte)12, (byte)14, (byte)2, (byte)0, (byte)6, (byte)10, (byte)13, (byte)15, (byte)3, (byte)5, (byte)8, (byte)2, (byte)1, (byte)14, (byte)7, (byte)4, (byte)10, (byte)8, (byte)13, (byte)15, (byte)12, (byte)9, (byte)0, (byte)3, (byte)5, (byte)6, (byte)11}};
    byte[] BitPMC1 = new byte[]{(byte)56, (byte)48, (byte)40, (byte)32, (byte)24, (byte)16, (byte)8, (byte)0, (byte)57, (byte)49, (byte)41, (byte)33, (byte)25, (byte)17, (byte)9, (byte)1, (byte)58, (byte)50, (byte)42, (byte)34, (byte)26, (byte)18, (byte)10, (byte)2, (byte)59, (byte)51, (byte)43, (byte)35, (byte)62, (byte)54, (byte)46, (byte)38, (byte)30, (byte)22, (byte)14, (byte)6, (byte)61, (byte)53, (byte)45, (byte)37, (byte)29, (byte)21, (byte)13, (byte)5, (byte)60, (byte)52, (byte)44, (byte)36, (byte)28, (byte)20, (byte)12, (byte)4, (byte)27, (byte)19, (byte)11, (byte)3};
    byte[] BitPMC2 = new byte[]{(byte)13, (byte)16, (byte)10, (byte)23, (byte)0, (byte)4, (byte)2, (byte)27, (byte)14, (byte)5, (byte)20, (byte)9, (byte)22, (byte)18, (byte)11, (byte)3, (byte)25, (byte)7, (byte)15, (byte)6, (byte)26, (byte)19, (byte)12, (byte)1, (byte)40, (byte)51, (byte)30, (byte)36, (byte)46, (byte)54, (byte)29, (byte)39, (byte)50, (byte)44, (byte)32, (byte)47, (byte)43, (byte)48, (byte)38, (byte)55, (byte)33, (byte)52, (byte)45, (byte)41, (byte)49, (byte)35, (byte)28, (byte)31};
    byte[][] SubKey = new byte[16][];
    byte[] bitDisplace = new byte[]{(byte)1, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)1};

    public DesEntry() {
        for(int i = 0; i < this.SubKey.length; ++i) {
            this.SubKey[i] = new byte[6];
        }

    }

    void initPermutation(byte[] inData) {
        byte[] newData = new byte[8];

        int j;
        for(j = 0; j < 64; ++j) {
            if((inData[this.BitIP[j] >> 3] & 1 << 7 - (this.BitIP[j] & 7)) != 0) {
                newData[j >> 3] |= (byte)(1 << 7 - (j & 7));
            }
        }

        for(j = 0; j < 8; ++j) {
            inData[j] = newData[j];
        }

    }

    void conversePermutation(byte[] inData) {
        byte[] newData = new byte[8];

        int j;
        for(j = 0; j < 64; ++j) {
            if((inData[this.BitCP[j] >> 3] & 1 << 7 - (this.BitCP[j] & 7)) != 0) {
                newData[j >> 3] |= (byte)(1 << 7 - (j & 7));
            }
        }

        for(j = 0; j < 8; ++j) {
            inData[j] = newData[j];
        }

    }

    void expand(byte[] inData, byte[] outData) {
        int i;
        for(i = 0; i < 6; ++i) {
            outData[i] = 0;
        }

        for(i = 0; i < 48; ++i) {
            if((inData[this.BitExp[i] >> 3] & 1 << 7 - (this.BitExp[i] & 7)) != 0) {
                outData[i >> 3] |= (byte)(1 << 7 - (i & 7));
            }
        }

    }

    void permutation(byte[] inData) {
        byte[] newData = new byte[4];

        int j;
        for(j = 0; j < 32; ++j) {
            if((inData[this.BitPM[j] >> 3] & 1 << 7 - (this.BitPM[j] & 7)) != 0) {
                newData[j >> 3] |= (byte)(1 << 7 - (j & 7));
            }
        }

        for(j = 0; j < 4; ++j) {
            inData[j] = newData[j];
        }

    }

    byte si(byte s, byte inByte) {
        int c = inByte & 32 | (inByte & 30) >> 1 | (inByte & 1) << 4;
        return (byte)(this.sBox[s][c] & 15);
    }

    void permutationChoose1(byte[] inData, byte[] outData) {
        int i;
        for(i = 0; i < 7; ++i) {
            outData[i] = 0;
        }

        for(i = 0; i < 56; ++i) {
            if((inData[this.BitPMC1[i] >> 3] & 1 << 7 - (this.BitPMC1[i] & 7)) != 0) {
                outData[i >> 3] |= (byte)(1 << 7 - (i & 7));
            }
        }

    }

    void permutationChoose2(byte[] inData, byte[] outData) {
        int i;
        for(i = 0; i < 6; ++i) {
            outData[i] = 0;
        }

        for(i = 0; i < 48; ++i) {
            if((inData[this.BitPMC2[i] >> 3] & 1 << 7 - (this.BitPMC2[i] & 7)) != 0) {
                outData[i >> 3] |= (byte)(1 << 7 - (i & 7));
            }
        }

    }

    void cycleMove(byte[] inData, byte bitMove) {
        for(int i = 0; i < bitMove; ++i) {
            int temp1 = inData[0] & 255;
            int temp2 = inData[1] & 255;
            int temp = temp1 << 1 | temp2 >> 7;
            inData[0] = (byte)(temp & 255);
            temp1 = inData[1] & 255;
            temp2 = inData[2] & 255;
            temp = temp1 << 1 | temp2 >> 7;
            inData[1] = (byte)(temp & 255);
            temp1 = inData[2] & 255;
            temp2 = inData[3] & 255;
            temp = temp1 << 1 | temp2 >> 7;
            inData[2] = (byte)(temp & 255);
            temp1 = inData[3] & 255;
            temp2 = inData[0] & 255;
            temp = temp1 << 1 | (temp2 & 16) >> 4;
            inData[3] = (byte)(temp & 255);
            inData[0] = (byte)(inData[0] & 15);
        }

    }

    void makeKey(byte[] inKey, byte[][] outKey) {
        byte[] outData56 = new byte[7];
        byte[] key28l = new byte[4];
        byte[] key28r = new byte[4];
        byte[] key56o = new byte[7];
        this.permutationChoose1(inKey, outData56);
        int temp = outData56[0] & 255;
        temp = temp >> 4 & 255;
        key28l[0] = (byte)temp;
        int temp1 = outData56[0] & 255;
        int temp2 = outData56[1] & 255;
        temp = temp1 << 4 | temp2 >> 4;
        key28l[1] = (byte)(temp & 255);
        temp1 = outData56[1] & 255;
        temp2 = outData56[2] & 255;
        temp = temp1 << 4 | temp2 >> 4;
        key28l[2] = (byte)(temp & 255);
        temp1 = outData56[2] & 255;
        temp2 = outData56[3] & 255;
        temp = temp1 << 4 | temp2 >> 4;
        key28l[3] = (byte)(temp & 255);
        key28r[0] = (byte)(outData56[3] & 15);
        key28r[1] = outData56[4];
        key28r[2] = outData56[5];
        key28r[3] = outData56[6];

        for(int i = 0; i < 16; ++i) {
            this.cycleMove(key28l, this.bitDisplace[i]);
            this.cycleMove(key28r, this.bitDisplace[i]);
            temp1 = key28l[0] & 255;
            temp2 = key28l[1] & 255;
            temp = temp1 << 4 | temp2 >> 4;
            key56o[0] = (byte)(temp & 255);
            temp1 = key28l[1] & 255;
            temp2 = key28l[2] & 255;
            temp = temp1 << 4 | temp2 >> 4;
            key56o[1] = (byte)(temp & 255);
            temp1 = key28l[2] & 255;
            temp2 = key28l[3] & 255;
            temp = temp1 << 4 | temp2 >> 4;
            key56o[2] = (byte)(temp & 255);
            temp1 = key28l[3] & 255;
            temp2 = key28r[0] & 255;
            temp = temp1 << 4 | temp2;
            key56o[3] = (byte)(temp & 255);
            key56o[4] = key28r[1];
            key56o[5] = key28r[2];
            key56o[6] = key28r[3];
            this.permutationChoose2(key56o, outKey[i]);
        }

    }

    void encry(byte[] inData, byte[] subKey, byte[] outData) {
        byte[] outBuf = new byte[6];
        byte[] buf = new byte[8];
        this.expand(inData, outBuf);

        int temp;
        for(temp = 0; temp < 6; ++temp) {
            outBuf[temp] ^= subKey[temp];
        }

        int temp1 = outBuf[0] & 255;
        temp = temp1 >> 2;
        buf[0] = (byte)(temp & 255);
        temp1 = outBuf[0] & 255;
        int temp2 = outBuf[1] & 255;
        temp = (temp1 & 3) << 4 | temp2 >> 4;
        buf[1] = (byte)(temp & 255);
        temp1 = outBuf[1] & 255;
        temp2 = outBuf[2] & 255;
        temp = (temp1 & 15) << 2 | temp2 >> 6;
        buf[2] = (byte)(temp & 255);
        buf[3] = (byte)(outBuf[2] & 63);
        temp1 = outBuf[3] & 255;
        temp = temp1 >> 2;
        buf[4] = (byte)(temp & 255);
        temp1 = outBuf[3] & 255;
        temp2 = outBuf[4] & 255;
        temp = (temp1 & 3) << 4 | temp2 >> 4;
        buf[5] = (byte)(temp & 255);
        temp1 = outBuf[4] & 255;
        temp2 = outBuf[5] & 255;
        temp = (temp1 & 15) << 2 | temp2 >> 6;
        buf[6] = (byte)(temp & 255);
        buf[7] = (byte)(outBuf[5] & 63);

        int i;
        for(i = 0; i < 8; ++i) {
            buf[i] = this.si((byte)i, buf[i]);
        }

        for(i = 0; i < 4; ++i) {
            temp1 = buf[i * 2] & 255;
            temp2 = buf[i * 2 + 1] & 255;
            temp = temp1 << 4 | temp2;
            outBuf[i] = (byte)(temp & 255);
        }

        this.permutation(outBuf);

        for(i = 0; i < 4; ++i) {
            outData[i] = outBuf[i];
        }

    }

    void desData(DesEntry.TDesMode desMode, byte[] inData, byte[] outData) {
        byte[] temp = new byte[4];
        byte[] buf = new byte[4];

        int i;
        for(i = 0; i < 8; ++i) {
            outData[i] = inData[i];
        }

        this.initPermutation(outData);
        int j;
        if(desMode == DesEntry.TDesMode.dmEncry) {
            for(i = 0; i < 16; ++i) {
                for(j = 0; j < 4; ++j) {
                    temp[j] = outData[j];
                }

                for(j = 0; j < 4; ++j) {
                    outData[j] = outData[j + 4];
                }

                this.encry(outData, this.SubKey[i], buf);

                for(j = 0; j < 4; ++j) {
                    outData[j + 4] = (byte)(temp[j] ^ buf[j]);
                }
            }

            for(j = 0; j < 4; ++j) {
                temp[j] = outData[j + 4];
            }

            for(j = 0; j < 4; ++j) {
                outData[j + 4] = outData[j];
            }

            for(j = 0; j < 4; ++j) {
                outData[j] = temp[j];
            }
        } else if(desMode == DesEntry.TDesMode.dmDecry) {
            for(i = 15; i >= 0; --i) {
                for(j = 0; j < 4; ++j) {
                    temp[j] = outData[j];
                }

                for(j = 0; j < 4; ++j) {
                    outData[j] = outData[j + 4];
                }

                this.encry(outData, this.SubKey[i], buf);

                for(j = 0; j < 4; ++j) {
                    outData[j + 4] = (byte)(temp[j] ^ buf[j]);
                }
            }

            for(j = 0; j < 4; ++j) {
                temp[j] = outData[j + 4];
            }

            for(j = 0; j < 4; ++j) {
                outData[j + 4] = outData[j];
            }

            for(j = 0; j < 4; ++j) {
                outData[j] = temp[j];
            }
        }

        this.conversePermutation(outData);
    }

    @SuppressLint({"DefaultLocale"})
    String EncryStr(String Str, String Key) {
        byte[] StrByte = new byte[8];
        byte[] OutByte = new byte[8];
        byte[] KeyByte = new byte[8];

        for(int StrResult = 0; StrResult < 8; ++StrResult) {
            KeyByte[StrResult] = (byte)Integer.parseInt(Key.substring(StrResult * 2, StrResult * 2 + 2), 16);
        }

        this.makeKey(KeyByte, this.SubKey);
        StringBuilder var9 = new StringBuilder();

        int j;
        for(j = 0; j < 8; ++j) {
            StrByte[j] = (byte)Integer.parseInt(Str.substring(j * 2, j * 2 + 2), 16);
        }

        this.desData(DesEntry.TDesMode.dmEncry, StrByte, OutByte);

        for(j = 0; j < 8; ++j) {
            String hex = Integer.toHexString(OutByte[j] & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            var9.append(hex);
        }

        return var9.toString().toUpperCase();
    }

    @SuppressLint({"DefaultLocale"})
    String DecryStr(String Str, String Key) {
        byte[] StrByte = new byte[8];
        byte[] OutByte = new byte[8];
        byte[] KeyByte = new byte[8];

        for(int StrResult = 0; StrResult < 8; ++StrResult) {
            KeyByte[StrResult] = (byte)Integer.parseInt(Key.substring(StrResult * 2, StrResult * 2 + 2), 16);
        }

        this.makeKey(KeyByte, this.SubKey);
        StringBuilder var9 = new StringBuilder();

        int j;
        for(j = 0; j < 8; ++j) {
            StrByte[j] = (byte)Integer.parseInt(Str.substring(j * 2, j * 2 + 2), 16);
        }

        this.desData(DesEntry.TDesMode.dmDecry, StrByte, OutByte);

        for(j = 0; j < 8; ++j) {
            String hex = Integer.toHexString(OutByte[j] & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            var9.append(hex);
        }

        return var9.toString().toUpperCase();
    }

    String DesOperation(String str_Data, String str_Key, int i_Sign) {
        return i_Sign == 1?this.EncryStr(str_Data, str_Key):(i_Sign == 0?this.DecryStr(str_Data, str_Key):"");
    }

    String TriDesOperation(String str_Data, String str_Key, int i_Sign) {
        String str_Left = str_Key.substring(0, 16);
        String str_Right = str_Key.substring(16, 32);
        String str_Result;
        if(i_Sign == 1) {
            str_Result = this.DesOperation(str_Data, str_Left, 1);
            str_Result = this.DesOperation(str_Result, str_Right, 0);
            str_Result = this.DesOperation(str_Result, str_Left, 1);
            return str_Result;
        } else if(i_Sign == 0) {
            str_Result = this.DesOperation(str_Data, str_Left, 0);
            str_Result = this.DesOperation(str_Result, str_Right, 1);
            str_Result = this.DesOperation(str_Result, str_Left, 0);
            return str_Result;
        } else {
            return "";
        }
    }

    String OffSetEncryptDataDES(String str_Data, String str_Key, int i_OffSet) {
        if(i_OffSet * 2 + 16 > str_Data.length()) {
            return "";
        } else {
            String str_Encrypt = this.DesOperation(str_Data.substring(i_OffSet * 2, 16), str_Key, 1);
            String str_Left = str_Data.substring(i_OffSet * 2 + 16, str_Data.length());
            return str_Data.substring(0, i_OffSet * 2) + str_Encrypt + str_Left.toString();
        }
    }

    String OffSetDecryptDataDES(String str_Data, String str_Key, int i_OffSet) {
        if(i_OffSet * 2 + 16 > str_Data.length()) {
            return "";
        } else {
            String str_Decrypt = this.DesOperation(str_Data.substring(i_OffSet * 2, 16), str_Key, 0);
            String str_Left = str_Data.substring(i_OffSet * 2 + 16, str_Data.length());
            return str_Data.substring(0, i_OffSet * 2) + str_Decrypt + str_Left;
        }
    }

    String OffSetEncryptDataTriDES(String str_Data, String str_Key, int i_OffSet) {
        if(i_OffSet * 2 + 16 > str_Data.length()) {
            return "";
        } else {
            String str_Encrypt = this.TriDesOperation(str_Data.substring(i_OffSet * 2, 16), str_Key, 1);
            String str_Left = str_Data.substring(i_OffSet * 2 + 16, str_Data.length());
            return str_Data.substring(0, i_OffSet * 2) + str_Encrypt + str_Left.toString();
        }
    }

    String OffSetDecryptDataTriDES(String str_Data, String str_Key, int i_OffSet) {
        if(i_OffSet * 2 + 16 > str_Data.length()) {
            return "";
        } else {
            String str_Decrypt = this.TriDesOperation(str_Data.substring(i_OffSet * 2, 16), str_Key, 0);
            String str_Left = str_Data.substring(i_OffSet * 2 + 16, str_Data.length());
            return str_Data.substring(0, i_OffSet * 2) + str_Decrypt + str_Left;
        }
    }

    @SuppressLint({"DefaultLocale"})
    String _8ByteHexNot(String str_Data) {
        String rtn = "";

        for(int i = 0; i < str_Data.length() / 2; ++i) {
            String hex = Integer.toHexString(255 - Integer.parseInt(str_Data.substring(i * 2, i * 2 + 2), 16) & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            rtn = rtn + hex;
        }

        return rtn.toUpperCase();
    }

    String KeyDisperse(String str_RootKey, String str_DisperseDene) {
        String str_Result;
        if(str_RootKey.length() == 32) {
            String str_Left = this.OffSetEncryptDataTriDES(str_DisperseDene, str_RootKey, 0);
            String str_Right = this.OffSetEncryptDataTriDES(this._8ByteHexNot(str_DisperseDene), str_RootKey, 0);
            str_Result = str_Left + str_Right;
        } else {
            str_Result = this.OffSetEncryptDataDES(str_DisperseDene, str_RootKey, 0);
        }

        return str_Result;
    }

    public String DesNFC(String data, String NFCRandm) {
        String str_Con_Key03 = "2233445566778899AABBCCDDEEFF0011";
        String str_Key = this.KeyDisperse(str_Con_Key03, NFCRandm);
        String UserData = data;
        byte[] fCardData = new byte[16];

        int sum;
        for(sum = 0; sum < UserData.length() / 2; ++sum) {
            fCardData[sum] = (byte)(Integer.parseInt(UserData.substring(sum * 2, sum * 2 + 2), 16) & 255);
        }

        sum = 0;

        for(int hex = 0; hex < 15; ++hex) {
            sum += fCardData[hex];
        }

        fCardData[15] = (byte)(sum & 255);
        String var11 = Integer.toHexString(fCardData[15] & 255);
        if(var11.length() == 1) {
            var11 = "0" + var11;
        }

        UserData = UserData + var11;
        String str_Encrypt = "";
        if(str_Key.length() == 32) {
            for(int i = 0; i < 2; ++i) {
                str_Encrypt = str_Encrypt + this.OffSetEncryptDataTriDES(UserData.substring(i * 16, i * 16 + 16), str_Key, 0);
            }
        }

        return str_Encrypt + NFCRandm;
    }

    static enum TDesMode {
        dmEncry,
        dmDecry;

        private TDesMode() {
        }
    }
}
