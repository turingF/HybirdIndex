package Bean;

import java.time.LocalDateTime;

public class UidTimeInfo {


    int uid;
    LocalDateTime date;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public UidTimeInfo(int uid, LocalDateTime date) {
        this.uid = uid;
        this.date = date;
    }


}
