package com.mojtaba;

public class Main {

    public static void main(String[] args)
    {
        DataBase dataBase = com.mojtaba.AllSingletons.getDataBaseInstance();
        Network network = com.mojtaba.AllSingletons.getNetworkInstance();

        System.out.println(dataBase.getSampleData());
        System.out.println(network.getSampleData());
    }
}
