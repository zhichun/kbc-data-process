package bnu.kde;


public class Pair<A, B extends Comparable> implements Comparable {
    private A first;
    private B second;

    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
            return
                    ((this.first == otherPair.first ||
                            (this.first != null && otherPair.first != null &&
                                    this.first.equals(otherPair.first))) &&
                            (this.second == otherPair.second ||
                                    (this.second != null && otherPair.second != null &&
                                            this.second.equals(otherPair.second))));
        }

        return false;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public A getFirst() {
        return first;
    }

//    public void setFirst(A first) {
//        this.first = first;
//    }

    public B getSecond() {
        return second;
    }


    public int compareTo(Object o) {
        return ((Pair<A, B>)o).getSecond().compareTo(this.second);
    }
}
