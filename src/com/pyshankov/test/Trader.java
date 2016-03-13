package com.pyshankov.test;

import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * Created by pyshankov on 25.02.2016.
 */
@Immutable
public final class Trader {
    private final String name;
    private final String city;
    public Trader(String n, String c){
        this.name = n;
        this.city = c;
    }
    public String getName(){
        return this.name;
    }
    public String getCity(){
        return this.city;
    }
    public String toString(){
        return "Trader:"+this.name + " in " + this.city;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass()!=this.getClass()) return false;
        else return this.getName().equals(obj);
    }
}
