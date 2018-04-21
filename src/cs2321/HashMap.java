/**
 * @author Lucas Buccilli
 * Class:
 * Date: 3/31/2018
 * Time: 10:53 PM
 * Project Name: Maps
 * File Name: HashMap
 * Discription: Implemintation of hashmap
 */

package cs2321;

import net.datastructures.Entry;
import net.datastructures.Map;

public class HashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

    int capacity; // The size of the hash table
    int Size;
    private UnorderedMap<K, V>[] List;

    /**
     * Constructor that takes a hash size
     *
     * @param hashsize Table capacity: the number of buckets to initialize in the HashMap
     */
    public HashMap(int hashsize) {
        List = (UnorderedMap<K, V>[]) new UnorderedMap[hashsize];
        capacity = hashsize;
        Size = 0;
    }

    /**
     * Helper method for get
     * Returns the value associated with key
     *
     * @param h
     * @param k
     * @return
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("θ(1)")
    private V bucketGet(int h, K k) {
        UnorderedMap<K, V> bucket = List[h];
        if (bucket == null) {
            return null;
        }
        return bucket.get(k);
    }

    /**
     * Helper method for put
     * Adds new value to the hash map
     *
     * @param h hash vale of k
     * @param k key
     * @param v value
     * @return value of old key
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("θ(1)")
    private V bucketPut(int h, K k, V v) {
        UnorderedMap<K, V> bucket = List[h];
        if (bucket == null) {
            bucket = List[h] = new UnorderedMap<>();
        }
        int oldSize = bucket.size();
        V answer = bucket.put(k, v);
        Size += (bucket.size() - oldSize);
        return answer;
    }

    /**
     * helper method for remove
     * Removes key value from hash map
     *
     * @param h hash index
     * @param k key
     * @return
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("θ(1)")
    private V bucketRemove(int h, K k) {
        UnorderedMap<K, V> bucket = List[h];
        if (bucket == null) {
            return null;
        }
        int oldSize = bucket.size();
        V answer = bucket.remove(k);
        Size -= (oldSize - bucket.size());
        return answer;
    }

    private int hashValue(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    @Override
    public int size() {
        return Size;
    }

    @Override
    public boolean isEmpty() {
        return Size == 0;
    }


    /**
     * Returns the value associated with key
     * @param h
     * @param k
     * @return
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("θ(1)")
    @Override
    public V get(K key) {
        /*TCJ
         * Should be O(1) if data is not all in same bucket
         * data could be in same bucket O(n)
         */
        int hashValue = hashValue(key);
        if (List[hashValue] == null) {
            return null;
        }
        return List[hashValue(key)].get(key);
    }

    /**
     * Adds new key value to hash map
     *
     * @param h
     * @param k
     * @return
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("θ(1)")
    @Override
    public V put(K key, V value) {
        /*TCJ
         * Should be O(1) if data is not all in same bucket
         * data could be in same bucket O(n)
         */
        return bucketPut(hashValue(key), key, value);
    }


    /**
     * removes key value from hash map
     *
     * @param h hash index
     * @param k key too remove
     * @return
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("θ(1)")
    @Override
    public V remove(K key) {
        /*TCJ
         * Should be O(1) if data is not all in same bucket
         * data could be in same bucket O(n)
         */
        return bucketRemove(hashValue(key), key);
    }

    @Override
    public Iterable<Entry<K, V>> entrySet() {
        ArrayList<Entry<K, V>> buffer = new ArrayList<>();
        for (int h = 0; h < capacity; h++) {
            if (List[h] != null) {
                for (Entry<K, V> entry : List[h].entrySet()) {
                    buffer.addLast(entry);
                }
            }
        }
        return buffer;
    }

}
