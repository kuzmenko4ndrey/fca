/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fca;

import checker.WN;
import db.SQLiteJDBC;
import static fca.Entry.LENGTH_TO_THROW;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author neophron
 */
public class FCL {

    protected List<String> prop;
    protected Map<String, List<Entry>> entries;
    WN wn;

    public FCL() {
        prop = new ArrayList<>();
        entries = new HashMap<>();
        String wnhome = System.getProperty("user.dir");
        String dictPath = wnhome + File.separator + "src/dict";
        String word1 = "metal_money", word2 = "money";
        boolean loadToRAM = false;
        try {
            wn = new WN(dictPath, loadToRAM);
            System.out.println(wn.isHypernym(word1, word2));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addSentence(Sentence s) {
        for (int i = 0; i < s.numberOfProp(); i++) {
            String str = s.getProp(i);
            int k = prop.indexOf(str);
            if (k != -1) {
                s.setNum(i, k);
            } else {
                prop.add(str);
                s.setNum(i, prop.size() - 1);
            }
        }
        s.normalizeAll();
        for (int i = 0; i < s.numberOfEntries(); i++) {
            addEntry(s.getEntry(i));
        }
    }

    public void addSentenceToDB(List<Pair<String, Object>> l) {
        for (int i = 0; i < l.size(); i++) {
            int k = 0;
            for (int j = 0; j < l.get(j).getKey().length(); j++) {
                if (l.get(j).getKey().charAt(j) == ' ') {
                    k++;
                }
            }
            if (k >= LENGTH_TO_THROW) {
                continue;
            }
            SQLiteJDBC.db.newEntry(l.get(i).getKey());
            SQLiteJDBC.db.newKontext(l.get(i).getValue().toString());
            SQLiteJDBC.db.newRelation(l.get(i).getKey(),
                    l.get(i).getValue().toString());
        }

    }

    protected void addEntry(Entry e) {
        boolean flag = true;
        List<Entry> l = entries.get(e.entry());
        if (l == null) {
            l = new ArrayList<>();
            l.add(e);
            entries.put(e.entry(), l);
            return;
        }
        Entry entryToAdd = e;
        while (flag) {
            flag = false;
            for (int i = 0; i < l.size(); i++) {
                if (l.get(i).uniteIfLegal(entryToAdd)) {
                    entryToAdd = l.get(i);
                    l.remove(i);
                    flag = true;
                    break;
                }
            }
        }
        l.add(entryToAdd);
    }

    public List<String> getEntries() {
        List<String> res = new ArrayList<>(entries.keySet());
        return res;
    }

    private boolean parentSonRel(Set parent, Set son) {
        if (parent.isEmpty() || son.isEmpty()) {
            return false;
        }
        return parent.containsAll(son);
    }

    public void finish() throws SQLException {
        SQLiteJDBC.db.closer();
    }

    public List<Pair<String, String>> getEdgesFromDB(int minEntUse, int minKonUse) {
        List<String> l = SQLiteJDBC.db.getEntry(minEntUse);
        List<Pair<String, String>> res = new ArrayList<>();
        int rightHave = 0;
        int wrongHave = 0;
        int rightMiss = 0;
        int wrongMiss = 0;
        for (String p : l) {
            Set<String> parent = SQLiteJDBC.db.getKontexts(p, minKonUse);
            for (String s : l) {
                Set<String> son = SQLiteJDBC.db.getKontexts(s, minKonUse);
                if (s.equals(p)) {
                    continue;
                }
                if (parentSonRel(parent, son)) {
                    boolean flag = true;
                    String key = p;
                    String value = s;
                    if (parentSonRel(son, parent)) {
                        key = "synonyms: " + key;
                        flag = false;
                    }
                    if (flag) {
                        if (wn.isHypernym(p, s) == 1) {
                            rightHave++;
                        } else {
                            wrongHave++;
                        }
                    }
                    res.add(new Pair(key, value));
                } else if (wn.isHypernym(p, s) == 1) {
                    wrongMiss++;
                } else {
                    rightMiss++;
                }
            }
        }
        System.out.println("rightHave " + rightHave);
        System.out.println("wrongHave " + wrongHave);
        System.out.println("rightMiss " + rightMiss);
        System.out.println("wrongMiss " + wrongMiss);
        return res;
    }

    public List<Pair<String, String>> getEdges() {
        List<Pair<String, String>> res = new ArrayList<>();
        int rightHave = 0;
        int wrongHave = 0;
        int rightMiss = 0;
        int wrongMiss = 0;
        Set<String> set = entries.keySet();
        for (String p : set) {
            for (Entry parent : entries.get(p)) {
                for (String s : set) {
                    for (Entry son : entries.get(s)) {
                        if (son == parent) {
                            continue;
                        }
                        if (parentSonRel(parent.getProp(), son.getProp())) {
                            boolean flag = true;
                            String key = parent.entry();
                            String value = son.entry();
                            if (parentSonRel(son.getProp(), parent.getProp())) {
                                key = "synonyms: " + key;
                                flag = false;
                            }
                            if (flag) {
                                if (wn.isHypernym(parent.entry(),
                                        son.entry()) == 1) {
                                    rightHave++;
                                } else {
                                    wrongHave++;
                                }
                            }
                            res.add(new Pair(key, value));
                        } else if (wn.isHypernym(parent.entry(),
                                son.entry()) == 1) {
                            wrongMiss++;
                        } else {
                            rightMiss++;
                        }
                    }
                }
            }
        }
        System.out.println("rightHave " + rightHave);
        System.out.println("wrongHave " + wrongHave);
        System.out.println("rightMiss " + rightMiss);
        System.out.println("wrongMiss " + wrongMiss);
        return res;
    }

}
