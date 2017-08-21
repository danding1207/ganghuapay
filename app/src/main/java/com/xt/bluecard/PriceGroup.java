//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard;

import java.io.Serializable;

public class PriceGroup implements Serializable {
    private static final long serialVersionUID = -7529674158532293752L;
    public int price1;
    public int divideCount1;
    public int price2;
    public int divideCount2;
    public int price3;
    public int divideCount3;
    public int price4;
    public int divideCount4;
    public int price5;

    public PriceGroup() {
    }

    public String toString() {
        return "PriceGroup [price1=" + this.price1 + ", divideCount1=" + this.divideCount1 + ", price2=" + this.price2 + ", divideCount2=" + this.divideCount2 + ", price3=" + this.price3 + ", divideCount3=" + this.divideCount3 + ", price4=" + this.price4 + ", divideCount4=" + this.divideCount4 + ", price5=" + this.price5 + "]";
    }
}
