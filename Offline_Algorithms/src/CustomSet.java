package com.multiKnapsackAlgorithm;
// Generic Set Implementation for Calculating Power Set
// Author: H. Mehryar
// Email: hmehryar@wayne.edu
//Inspired From Geek For Geeks
//https://www.geeksforgeeks.org/power-set/
//// Usage:	ArrayList<Character> setCharItems =new ArrayList<>();
////		setCharItems.add('a');
////		setCharItems.add( 'b');
////		setCharItems.add('c');
////
////		CustomSet customSet=new CustomSet(setCharItems);
////		customSet.setPowerSet();
////		customSet.printPowerSet();
////
////		ArrayList<String> setStringItems =new ArrayList<>();
////		setStringItems.add("A");
////		setStringItems.add("B");
////		setStringItems.add("C");
////		customSet.setSetItems(setStringItems);
////		customSet.setPowerSet();
////		customSet.printPowerSet();

import java.util.ArrayList;
import java.util.Arrays;

public class CustomSet<T> {
    public CustomSet(ArrayList<T> setItems) {
        this.setItems = setItems;
        this.powerSet=new PowerSet();
    }

    private ArrayList<T> setItems;

    public ArrayList<T> getSetItems() {
        return setItems;
    }

    public void setSetItems(ArrayList<T> setItems) {
        this.setItems = setItems;
    }

    private PowerSet powerSet;

    public PowerSet getPowerSet() {
        return powerSet;
    }

    public void setPowerSet() {
        /*set_size of power set of a set
        with set_size n is (2**n -1)*/
        long pow_set_size =
                (long)Math.pow(2, setItems.size());
        int counter, j;

        /*Run from counter 000..0 to
        111..1*/
        for(counter = 0; counter <
                pow_set_size; counter++)
        {
            ArrayList<T> currentSubset=new ArrayList<>();
            for(j = 0; j < setItems.size(); j++)
            {
                /* Check if jth bit in the
                counter is set If set then
                print jth element from set */
                if((counter & (1 << j)) > 0)
                    currentSubset.add(setItems.get(j));
            }
            powerSet.add(currentSubset);
        }
    }
    void printPowerSet(){
        for (ArrayList<T> item:powerSet) {
            System.out.println(item.toString());
        }
    }
    class PowerSet extends ArrayList<ArrayList<T>>{
    }
}
