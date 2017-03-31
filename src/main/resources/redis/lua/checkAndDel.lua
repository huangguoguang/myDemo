--
-- Delete a lock
--
-- KEYS[1]   - key
-- ARGV[1]   - value

if redis.call('get',KEYS[1]) == ARGV[1]
then
    return redis.call('del',KEYS[1])
else
    return 0
end