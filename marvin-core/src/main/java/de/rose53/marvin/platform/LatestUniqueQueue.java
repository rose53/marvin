package de.rose53.marvin.platform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

public class LatestUniqueQueue<E extends Message> implements Queue<E> {

    private final Queue<E>      queue = new LinkedList<E>();
    private final Map<String,E> map   = new HashMap<String,E>();

    @Override
    public boolean addAll(Collection<? extends E> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void clear() {
        queue.clear();
        map.clear();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    @Override
    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    @Override
    public synchronized boolean add(E e) {
        String key = getKey(e);
        E victim = map.get(key);
        if (victim != null) {
            queue.remove(victim);
            map.replace(key, e);
        } else {
            map.put(key, e);
        }
        queue.add(e);
        return true;
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E peek() {
        return queue.peek();
    }

    @Override
    public synchronized  E poll() {
        E e = queue.poll();
        if (e != null) {
            map.remove(getKey(e));
        }
        return e;
    }

    @Override
    public synchronized  E remove() {
        E e = queue.remove();

        map.remove(getKey(e));

        return e;
    }


    private String getKey(E e) {
        StringBuilder b = new StringBuilder(e.getMessageType().toString());

        if (StringUtils.isNotBlank(e.getMessageId())) {
            b.append('#')
             .append(e.getMessageId());
        }
        return b.toString();
    }
}
