package com.pyshankov.groupbyforker;


import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;


//group objects in list by some property
public final class ForkJoinMapGroupBuy<K,V> extends RecursiveTask<ConcurrentMap<K ,Collection<V>>> {

    public static final long THRESHOLD = 4;

    private final int start;

    private final int end;

    private final List<? extends V> storage;

    private final ConcurrentMap<K,Collection<V>> concurrentMap;

    private final Function<V,K> groupByFunction;

    public ForkJoinMapGroupBuy(List<? extends V> traders, Function<V,K> f){
        this(0,traders.size(),traders,f);
    }

    private ForkJoinMapGroupBuy(int start, int end, List<? extends V> traderList, Function<V,K> groupByFunction) {
        this.start = start;
        this.end = end;
        this.storage = Collections.unmodifiableList(traderList);
        this.concurrentMap = new ConcurrentHashMap<>();
        this.groupByFunction=groupByFunction;
    }

    private ConcurrentMap<K,Collection<V>> computeSequentially(){
        V t1;
        for (int i = start ; i<end ; i++){
            t1 = storage.get(i);
            if (!concurrentMap.containsKey(groupByFunction.apply(t1)))
                concurrentMap.putIfAbsent(groupByFunction.apply(t1),new HashSet<>(Arrays.asList(t1)));
            else if(concurrentMap.containsKey(groupByFunction.apply(t1)))
                concurrentMap.get(groupByFunction.apply(t1)).add(t1);
        }
        return concurrentMap;
    }

    private void addElem(ConcurrentMap<K,Collection<V>> subMap){
        for(K s : subMap.keySet()){
            if(concurrentMap.containsKey(s)){
                Collection<V> list = concurrentMap.get(s);
                list.addAll(subMap.get(s));
                concurrentMap.put(s,list);
            }
            else concurrentMap.putIfAbsent(s,subMap.get(s));
        }
    }

    @Override
    protected ConcurrentMap<K,Collection<V>> compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            return computeSequentially();
        }
        ForkJoinMapGroupBuy<K,V> leftTask = new ForkJoinMapGroupBuy( start, start + length/2,storage,groupByFunction);
        leftTask.fork();
        ForkJoinMapGroupBuy rightTask = new ForkJoinMapGroupBuy(start + length/2, end ,storage,groupByFunction);
        ConcurrentMap<K,Collection<V>> right = rightTask.compute();
        ConcurrentMap<K,Collection<V>> left = leftTask.join();

        addElem(right);
        addElem(left);

        return concurrentMap;
    }
}
