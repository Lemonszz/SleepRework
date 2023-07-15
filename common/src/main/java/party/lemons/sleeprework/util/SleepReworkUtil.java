package party.lemons.sleeprework.util;

import java.util.List;
import java.util.Map;

public class SleepReworkUtil
{
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map recursiveCollectionMerge(Map original, Map newMap)
    {
        for (Object key : newMap.keySet())
        {
            if (newMap.get(key) instanceof Map && original.get(key) instanceof Map originalChild)
            {
                Map newChild = (Map) newMap.get(key);
                original.put(key, recursiveCollectionMerge(originalChild, newChild));
            }
            else if (newMap.get(key) instanceof List newChild && original.get(key) instanceof List originalChild) {
                for (Object each : newChild)
                {
                    if (!originalChild.contains(each))
                    {
                        originalChild.add(each);
                    }
                }
            } else
            {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }
}
