import java.util.*;
import java.util.zip.CRC32;

public class ConsistentHash
{
    private final SortedMap<Long, Server> hashRing;
    private final int numberOfVirtualNodes;

    public ConsistentHash(int numberOfVirtualNodes, Collection<Server> servers)
    {
        this.numberOfVirtualNodes = numberOfVirtualNodes;
        hashRing = new TreeMap<>();
        if (servers != null) {
            for (Server n : servers) {
                this.add(n);
            }
        }
    }

    public void add(Server server)
    {
        for (int i = 0; i < numberOfVirtualNodes; i++) {
            hashRing.put(hash(server.toString() + i), server);
        }
    }

    public void remove(Server server)
    {
        for (int i = 0; i < numberOfVirtualNodes; i++) {
            hashRing.remove(hash(server.toString() + i));
        }
    }

    public Server get(String key)
    {
        if (hashRing.isEmpty()) {
            return null;
        }
        Long hashVal = hash(key);
        if (!hashRing.containsKey(hashVal)) {
            SortedMap<Long, Server> tailMap = hashRing.tailMap(hashVal);
            hashVal = tailMap.isEmpty() ? hashRing.firstKey() : tailMap.firstKey();
        }
        return hashRing.get(hashVal);
    }

    private Long hash(String key)
    {
        CRC32 crc = new CRC32();
        crc.update(key.getBytes());
        return crc.getValue();
    }
    public static void main(String[] args)
    {
        List<Server> servers = new LinkedList<>();
        servers.add(new Server("10.0.0.1"));
        servers.add(new Server("10.0.0.2"));
        int numberOfVirtualNodes = 200;
        ConsistentHash consistentHashObj = new ConsistentHash(numberOfVirtualNodes, servers);

        // add a new server
        Server newServer = new Server("10.0.0.3");
        consistentHashObj.add(newServer);
        System.out.println(consistentHashObj.get("key0")); // return 10.0.0.3

        // remove a server
        consistentHashObj.remove(newServer);
        System.out.println(consistentHashObj.get("key0")); // return 10.0.0.2
    }
}

class Server
{
    public String ipAddress;
    Server(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String toString() {
        return ipAddress;
    }
}