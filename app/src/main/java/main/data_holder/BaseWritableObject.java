package main.data_holder;

import java.io.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BaseWritableObject implements Serializable {

    static final long serialVersionUID = -8294949271450580006L;

    public static void writeObject(Serializable serializableObject, String filePath, String fileName) {
        final File objectFile = new File(filePath, fileName);

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(objectFile, false);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(serializableObject);

        } catch (Exception error) {
            error.printStackTrace();

        } finally {
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public static BaseWritableObject readObject(File objectFile) {
        BaseWritableObject object = null;

        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        if (!objectFile.exists()) return null;

        try {
            fileInputStream = new FileInputStream(objectFile);
            objectInputStream = new ObjectInputStream(fileInputStream);
            object = (BaseWritableObject) objectInputStream.readObject();

        } catch (Exception error) {
            error.printStackTrace();
            object = null;
            objectFile.delete();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (objectInputStream != null) objectInputStream.close();
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
        return object;
    }

    public static Serializable readSerializableObjects(File objectFile) {
        Serializable object = null;

        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            fileInputStream = new FileInputStream(objectFile);
            objectInputStream = new ObjectInputStream(fileInputStream);
            object = (Serializable) objectInputStream.readObject();

        } catch (Exception error) {
            error.printStackTrace();
            object = null;
            objectFile.delete();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (objectInputStream != null) objectInputStream.close();
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
        return object;
    }
}
