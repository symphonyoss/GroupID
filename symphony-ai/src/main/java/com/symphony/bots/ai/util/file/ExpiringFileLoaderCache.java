package com.symphony.bots.ai.util.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick.tarsillo on 7/2/17.
 *
 * A cache that expires entries after given amount of time.
 * Cache will load unknown keys from file (if they exists).
 */
public class ExpiringFileLoaderCache<K, V> {
  private static final Logger LOG = LoggerFactory.getLogger(ExpiringFileLoaderCache.class);

  private final ObjectMapper MAPPER = new ObjectMapper();
  private LoadingCache<K, V> cache;
  private Class<V> classRef;
  private KeyReader<K> keyReader;
  private String filePath;

  public ExpiringFileLoaderCache(String filesPath, KeyReader<K> keyReader,
      long expireTime, TimeUnit unit, Class<V> classRef){
    cache =  CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .maximumSize(10000)
        .expireAfterWrite(expireTime, unit)
        .build(new CacheLoader<K, V>() {
          @Override
          public V load(K key) throws Exception {
            return attemptLoadFromFile(key);
          }
        });

    MAPPER.setVisibility(MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    this.classRef = classRef;
    this.keyReader = keyReader;
    this.filePath = filesPath;
  }

  /**
   * Attempts to load value from file
   * @param key the key to load
   * @return the loaded value (or null)
   */
  private V attemptLoadFromFile(K key){
    String path = filePath + keyReader.readKey(key) + ".json";
    try {
      return MAPPER.readValue(new FileInputStream(path), classRef);
    } catch (Exception e) {
      LOG.warn("Error when loading json in cache: ", e);
    }

    return null;
  }

  /**
   * Writes value to file
   * @param key used to name file
   * @param value value to save
   */
  private void writeToFile(K key, V value){
    String path = filePath + keyReader.readKey(key) + ".json";
    try {
      MAPPER.writeValue(new File(path), value);
    } catch (IOException e) {
      LOG.warn("Error when saving json to file in cache: ", e);
    }
  }

  /**
   * Puts a key in cache with value
   * Saves value to file
   * @param key the key to put
   * @param value the value to put
   */
  public void put(K key, V value){
    cache.put(key, value);
    writeToFile(key, value);
  }

  /**
   * Get a value from cache using key
   * @param key the key to reference value
   * @return the value
   */
  public V get(K key) throws ExecutionException, CacheLoader.InvalidCacheLoadException {
    return cache.get(key);
  }

  public void remove(K key) {
    cache.invalidate(key);
  }

  /**
   * An interface that references how to read a key, and convert it into a file name.
   */
  public interface KeyReader<K>{
    String readKey(K key);
  }
}
