package com.example.fetchrewards;

import java.util.ArrayList;
import java.util.Comparator;

public class Sorter implements Comparable<Sorter> {

    private int listId;
    private String name;
    private int id;


    public Sorter(int listId, String name, int id){
        this.listId = listId;
        this.name = name;
        this.id = id;
    }


    public int getListId(){
        return this.listId;
    }

    public String getName(){
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int compareTo(Sorter sorter) {
        int compare = Integer.compare(listId, sorter.listId);
        if (compare == 0){
            compare = Integer.compare(id, sorter.id);
        }
        return compare;
    }
}
