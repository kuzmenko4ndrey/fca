package checker;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;

import java.io.*;
import java.net.URL;
import java.util.*;

public class WN {
    private IRAMDictionary dict;

    public WN(String dictFilder, boolean loadToRAM) throws IOException, InterruptedException {
        URL url = new URL("file", null, dictFilder);
        dict = new RAMDictionary(url, ILoadPolicy.NO_LOAD);
        dict.open();
        dict.load(loadToRAM);
    }

    public boolean checkTaxonomy(String hypernym, String hyponym) {
        IIndexWord idxHyponym = dict.getIndexWord(hyponym, POS.NOUN);
        if (idxHyponym != null) {
            Queue<IWordID> wordIDs = new LinkedList<IWordID>(idxHyponym.getWordIDs());
            HashSet<IWordID> usedWords = new HashSet<IWordID>(wordIDs);

            while (!wordIDs.isEmpty()) {
                IWordID wordID = wordIDs.poll();
                usedWords.add(wordID);

                IWord word = dict.getWord(wordID);

                ISynset synset = word.getSynset();

                List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);

                List<IWord> words;
                for (ISynsetID sid : hypernyms) {
                    words = dict.getSynset(sid).getWords();

                    for (IWord w : words) {
//                    System.out.println(w);
                        if (w.getLemma().equals(hypernym))
                            return true;
                        IWordID id = w.getID();
                        if (!usedWords.contains(id)) {
                            wordIDs.add(id);
                            usedWords.add(id);
                        }
                    }
                }
            }
        }
        return false;
    }

    public int isHypernym(String word1, String word2) {
        String w1 = word1.trim().replaceAll(" ", "_");
        String w2 = word2.trim().replaceAll(" ", "_");
        if (checkTaxonomy(w1, w2)) {      // word1 is hypernym, word2 is hyponym
            return 1;
        }
        if (checkTaxonomy(w2, w1)) {      // word2 is hypernym, word1 is hyponym
            return 0;
        }
        return -1;
    }
}