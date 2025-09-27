package org.sharkonomy.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class serializer {
    public static String serializeItem(ItemStack item) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outStream);

        dataOutput.writeObject(item);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outStream.toByteArray());

    }

    public static ItemStack readSerializedItem(String serial) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(Base64.getDecoder().decode(serial));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inStream);

        try {
            return (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to deserialize ItemStack", e);
        }
    }
}
