import redis.clients.jedis.RedisClient;

public enum RedisFactory {
    INSTANCE;

    public RedisClient getRedisClient() {
        return RedisClient.create("redis://localhost:6379");
    }
}
