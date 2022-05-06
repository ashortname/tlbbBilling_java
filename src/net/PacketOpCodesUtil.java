package net;
import java.lang.reflect.Field;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class PacketOpCodesUtil {
    private static Int2ObjectMap<String> opcodeMap;

    static {
        opcodeMap = new Int2ObjectOpenHashMap<String>();

        Field[] fields = PacketOpCodes.class.getFields();

        for (Field f : fields) {
            try {
                opcodeMap.put(f.getInt(null), f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getOpcodeName(int opcode) {
        if (opcode < 0) return "UNKNOWN";
        return opcodeMap.getOrDefault(opcode, "UNKNOWN");
    }
}
