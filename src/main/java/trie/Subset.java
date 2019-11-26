package trie;

public class Subset {
    int keyStart;
    int keyEnd;
    int fromIndex;

    Subset(int keyStart, int keyEnd, int fromIndex){
        this.keyStart = keyStart;
        this.keyEnd = keyEnd;
        this.fromIndex = fromIndex;
    }
}
