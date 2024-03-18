package br.edu.ufersa.entities;

import br.edu.ufersa.utils.CarType;

public class Car {
    
    private CarType categoria;
    private long renavam;
    private String nome;
    private int ano_fab;
    private float preco;
    private static int quantidade = 0;

    public Car(CarType categoria, long renavam, String nome, int ano_fab, float preco) {
        this.setCategoria(categoria);
        this.setRenavam(renavam);
        this.setNome(nome);
        this.setAnoFab(ano_fab);
        this.setPreco(preco);
        incrementQuantidade();
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

    

    public static int getQuantidade() {
        return quantidade;
    }

    public static void incrementQuantidade() {
        quantidade++;
    }
    public static void decrementQuantidade() {
        quantidade--;
    }

    // TODO: formatar para algo mais intuitivo depois
    @Override
    public String toString() {
        return "Car [categoria=" + categoria + ", renavam=" + renavam + ", nome=" + nome
                + ", ano_fab=" + ano_fab + ", preco=" + preco + "]";
    }  
}
