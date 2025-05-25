import java.sql.Timestamp;

public class VaultFile {
    private int id;
    private int userId;
    private String filename;
    private Timestamp uploadTime;
    private long fileSize;
    private String fileUrl;

    // Constructor for reading from DB
    public VaultFile(int id, int userId, String filename, long fileSize, Timestamp uploadTime, String fileUrl) {
        this.id = id;
        this.userId = userId;
        this.filename = filename;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.fileUrl = fileUrl;
    }

    // Constructor for inserting new file (no id)
    public VaultFile(int userId, String filename, long fileSize, Timestamp uploadTime, String fileUrl) {
        this.userId = userId;
        this.filename = filename;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.fileUrl = fileUrl;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public Timestamp getUploadTime() { return uploadTime; }
    public void setUploadTime(Timestamp uploadTime) { this.uploadTime = uploadTime; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}
