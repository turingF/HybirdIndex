package IndexTestWithId;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Data_file_2 {

    /**
     * 使用nio+fileChannel来读取文件
     * @throws IOException
     */

    public static void newIOReadFile() throws IOException {
        FileChannel read = new RandomAccessFile("G://lily_947.txt", "r").getChannel();
        FileChannel writer = new RandomAccessFile("G://newIO.tmp", "rw").getChannel();
        ByteBuffer bb = ByteBuffer.allocate(200 * 1024 * 1024);
        while (read.read(bb) != -1) {
            bb.flip();
            writer.write(bb);
            bb.clear();
        }
        read.close();
        writer.close();
    }
}
