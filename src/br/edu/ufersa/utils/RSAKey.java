package br.edu.ufersa.utils;

import java.io.Serializable;
import java.math.BigInteger;

// TODO: Migrar essa classe para entities ? talvez

public class RSAKey implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger factor1;
    private BigInteger factor2;

    public RSAKey(BigInteger n1, BigInteger n2) {
        setFactor1(n1);
        setFactor2(n2);
    }

    public BigInteger getFactor1() {
        return factor1;
    }

    public BigInteger getFactor2() {
        return factor2;
    }

    private void setFactor1(BigInteger factor1) {
        if (factor1 != null) {
            this.factor1 = factor1;
        } else {
            System.err.println("not allowed number");
            this.factor1 = BigInteger.ZERO;
        }
    }

    private void setFactor2(BigInteger factor2) {
        if (factor2 != null) {
            this.factor2 = factor2;
        } else {
            System.err.println("not allowed number");
            this.factor2 = BigInteger.ZERO;
        }
    }    

}
