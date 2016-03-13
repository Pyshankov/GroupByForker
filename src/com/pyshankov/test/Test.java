package com.pyshankov.test;

import com.pyshankov.groupbyforker.ForkJoinMapGroupBuy;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by pyshankov on 13.03.2016.
 */

public class Test {

    public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
        Trader pavlo = new Trader("Pavlo","Kiev");

        List<Trader> traders = Arrays.asList(raoul,mario,alan,brian,pavlo);

        ForkJoinMapGroupBuy<String,Trader> map = new ForkJoinMapGroupBuy<>(traders,Trader::getCity);
      
        Map<String,Collection<Trader>> map1 = FORK_JOIN_POOL.invoke(map);

        System.out.println(map1);

    }
}
