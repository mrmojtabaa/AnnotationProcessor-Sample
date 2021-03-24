package com.mojtaba;

public class Main {

    public static void main(String[] args)
    {
        DataBase dataBase = com.mojtaba.DataBase_singleton.getInstance();
        Network network = com.mojtaba.Network_singleton.getInstance();

        System.out.println(dataBase.getSampleData());
        System.out.println(network.getSampleData());
    }
}
