package sw806f18.server.model;

/**
 * Created by chrae on 10-10-2017.
 */
public class Tuple<T, U> {
    private T first;
    private U second;

    public Tuple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(T val) {
        first = val;
    }

    public void setSecond(U val) {
        second = val;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return first.toString() + " " + second.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tuple)) {
            return false;
        }

        Tuple obj = (Tuple) other;

        return this.getFirst().equals(obj.getFirst()) && this.getSecond().equals(obj.getSecond());
    }

    @Override
    public int hashCode() {
        return this.getFirst().hashCode() + this.getSecond().hashCode();
    }
}