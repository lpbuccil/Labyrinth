/**
 * @author Lucas Buccilli
 * Class:
 * Date: 3/31/2018
 * Time: 10:53 PM
 * Project Name: Maps
 * File Name: UnorderedMap
 * Discription: Implementation of unordered hasmap
 */

package cs2321;


import net.datastructures.Entry;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class UnorderedMap<K, V> extends AbstractMap<K, V> {

    private ArrayList<AbstractMap.mapEntry<K, V>> List;

    public UnorderedMap() {
        List = new ArrayList<>();
    }

    /**
     * Returns index of where the key is in map
     *
     * @param key
     * @return index of where key is in map
     */
    @TimeComplexity("O(n)")
    private int findIndex(K key) {
        int n = List.size();
        for (int j = 0; j < n; j++) {
            if (List.get(j).getKey().equals(key)) {
                return j;
            }
        }
        return -1;
    }


    @Override
    public int size() {
        return List.size();
    }

    @Override
    public boolean isEmpty() {
        return List.isEmpty();
    }

    /**
     * Returns value associated with key
     *
     * @param key the key whose associated value is to be returned
     * @return
     */
    @TimeComplexity("O(n)")
    @Override
    public V get(K key) {
        int j = findIndex(key);
        if (j == -1) {
            return null;
        }

        return List.get(j).getValue();
    }

    /**
     * Adds new key value to map
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return
     */
    @TimeComplexity("O(n)")
    @Override
    public V put(K key, V value) {
        int j = findIndex(key);
        // key does not exist, add it
        if (j == -1) {
            List.addLast(new mapEntry<>(key, value));
            return null;

            //Key exists, update value
        } else {
            V temp = List.get(j).getValue();
            List.get(j).setValue(value);
            return temp;
        }

    }

    /**
     * Removes key value from map
     *
     * @param key the key whose entry is to be removed from the map
     * @return
     */
    @TimeComplexity("O(n)")
    @Override
    public V remove(K key) {
        int j = findIndex(key);
        int n = size();

        //key does not exist
        if (j == -1) {
            return null;
        }

        //save returns value
        V answer = List.get(j).getValue();

        //Move last entry to index of entry to remove
        if (j != (n - 1)) {
            List.set(j, List.get(n - 1));
        }

        //remove last entry
        List.remove(n - 1);
        return answer;
    }


    @Override
    public Iterable<Entry<K, V>> entrySet() {
        return new EntryIterable();
    }

    public class EntryIterator implements Iterator<Entry<K, V>> {
        private int j = 0;

        public boolean hasNext() {
            return j < List.size();
        }

        public Entry<K, V> next() {
            if (j == List.size()) {
                throw new NoSuchElementException();
            }
            return List.get(j++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class EntryIterable implements Iterable<Entry<K, V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
    }


}
