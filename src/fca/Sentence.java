/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fca;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author neophron
 */
public class Sentence {

    protected List<String> prop = new ArrayList<>();
    private List<Integer> num = new ArrayList<>();
    protected List<Entry> entries = new ArrayList<>();

    public Sentence(List<Pair<String, Object>> l) {
        int j = 0;
        for (Pair<String, Object> p : l) {
            String s = p.getValue().toString();
            if (!prop.contains(s)) {
                prop.add(s);
                num.add(j);
                j++;
            }
        }
        int i = 0;
        int max_i = l.size(); 
        while (i < max_i) {
            Entry e = Entry.createEntry(l.get(i).getKey());
            if (e == null) {
                i++;
                continue;
            }
            while (i < max_i && e.entry().equals(l.get(i).getKey())) {
                e.addProp(prop.indexOf(l.get(i).getValue().toString()));
                i++;
            }
            entries.add(e);
        }
    }

    public void setNum(int s, int k) {
        num.set(s, k);
    }

    public int numberOfProp() {
        return prop.size();
    }

    public int numberOfEntries() {
        return entries.size();
    }

    public String getProp(int i) {
        return prop.get(i);
    }

    public Entry getEntry(int i) {
        return entries.get(i);
    }

    public void normalizeAll() {
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).normalize(num);
        }
    }

}
