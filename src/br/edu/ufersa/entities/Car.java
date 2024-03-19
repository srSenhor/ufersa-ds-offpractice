package br.edu.ufersa.entities;

import br.edu.ufersa.utils.CarType;

public class Car {
    
    private CarType categoria;
    private long renavam;
    private String nome;
    private int ano_fab;
    private float preco;

    public Car(CarType categoria, long renavam, String nome, int ano_fab, float preco) {
        this.setCategoria(categoria);
        this.setRenavam(renavam);
        this.setNome(nome);
        this.setAnoFab(ano_fab);
        this.setPreco(preco);
    }

    public CarType getCategoria() {
        return categoria;
    }
    public void setCategoria(CarType categoria) {
        this.categoria = categoria;
    }

    public long getRenavam() {
        return renavam;
    }
    private void setRenavam(long renavam) {
        if (renavam > 9999999999f && renavam <= Float.MAX_VALUE) {
            this.renavam = renavam;
        } else {
            this.renavam = -1;
        }
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public int getAnoFab() {
        return ano_fab;
    }
    public void setAnoFab(int ano_fab) {
        if (ano_fab > 1886 && ano_fab < 2500) {
            this.ano_fab = ano_fab;
        } else {
            this.ano_fab = 1886;
        }
    }

    public float getPreco() {
        return preco;
    }
    public void setPreco(float preco) {
        if (preco >= 0 && preco <= Float.MAX_VALUE) {
            this.preco = preco;
        } else {
            this.preco = 130000f;
        }
    }

    // TODO: formatar para algo mais intuitivo depois
    @Override
    public String toString() {
        return  "= = = = = =  " + nome + "  = = = = = =\n" +
                "Renavam   " + renavam      + "\n" +
                "Ano       " + ano_fab      + "\n" +
                "Preco R$  " + preco        + "\n" +
                "Categoria " + categoria    + "\n" +
                "= = = = = = = = = = = = = = = = = = = \n";
    }  
}
