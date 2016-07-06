/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author neophron
 */
public class Entry {
    
    public static final int LENGTH_TO_THROW = 3, COUNT_TO_USE = 2;

    protected List<Integer> prop;
    protected String entry;
    protected Map<Integer, Integer> normalizedProp = null;
    protected int countOfMeet = 0;
    
    public static Entry createEntry(String s) {
        int k = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                k++;
            }
        }
        if (k >= LENGTH_TO_THROW) {
            return null;
        }
        else {
            return new Entry(s);
        }
    }

    protected Entry(String s) {
        entry = s;
        prop = new ArrayList<>();
    }

    protected Entry(String s, List<Integer> list) {
        entry = s;
        prop = list;
    }

    public void addProp(int x) {
        prop.add(x);
    }

    public String entry() {
        return entry;
    }

    public void normalize(List<Integer> l) {
        if (normalizedProp == null) {
            for (int i = 0; i < prop.size(); i++) {
                prop.set(i, l.get(prop.get(i)));
            }
            normalizedProp = new HashMap<>();
            for (Integer i : prop) {
                normalizedProp.put(i, 1);
            }
            prop = null;
        }
    }

    public boolean uniteIfLegal(Entry e) {
        if (!e.entry().equals(entry)) {
            return false;
        }
        boolean flag = false;
        Set<Integer> s = e.getSet();
        for (Integer x : s) {
            if (normalizedProp.containsKey(x)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            for (Integer x : s) {
                if (normalizedProp.containsKey(x)) {
                    normalizedProp.replace(x, normalizedProp.get(x) + e.normalizedProp.get(x));
                } else {
                    normalizedProp.put(x, e.normalizedProp.get(x));
                }
            }
            countOfMeet++;
            return true;
        }
        return false;
    }

    public Set getSet() {
        return normalizedProp.keySet();
    }
    
    public int howMuchMeet() {
        return countOfMeet;
    }
    
    public Set getProp() {
        Set<Integer> res = new HashSet<>();
        for (Integer i : normalizedProp.keySet()) {
            if (normalizedProp.get(i) >= COUNT_TO_USE) {
                res.add(i);
            }
        }
        return res;
    }

}
