import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author Denis Zolotarev
 */
public class AmigoSet<E> extends AbstractSet<E> implements Serializable, Cloneable, Set<E> {
    private static final Object PRESENT = new Object();
    private transient HashMap<E, Object> map;

    public AmigoSet() {
        this.map = new HashMap<>();
    }

    public AmigoSet(Collection<? extends E> collection) {
        int capacity = Math.max(16, (int) (collection.size() / .75f + 1));
        this.map = new HashMap<>(capacity);
        this.addAll(collection);
    }

    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public Object clone() {
        try {
            AmigoSet<E> newSet = (AmigoSet<E>) super.clone();
            newSet.map = (HashMap<E, Object>) map.clone();
            return newSet;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        int capacity = HashMapReflectionHelper.<Integer>callHiddenMethod(map, "capacity");
        float loadFactor = HashMapReflectionHelper.<Float>callHiddenMethod(map, "loadFactor");
        out.defaultWriteObject();
        out.writeInt(map.size());
        out.writeInt(capacity);
        out.writeFloat(loadFactor);
        for (E key : map.keySet()) {
            out.writeObject(key);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        int capacity = in.readInt();
        float loadFactor = in.readFloat();
        map = new HashMap<>(capacity, loadFactor);
        for (int i = 0; i < size; i++) {
            E key = (E) in.readObject();
            map.put(key, PRESENT);
        }
    }

}
