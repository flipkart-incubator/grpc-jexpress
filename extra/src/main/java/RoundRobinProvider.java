import lombok.Data;

@Data
public abstract class RoundRobinProvider<T> {
    private int index;
    protected T[] objects;
    protected int maxCount;

    protected abstract T createObject();

    protected abstract void populateObjects();

    public T get(){
        index = (++index) % maxCount;
        return objects[index];
    }
}
