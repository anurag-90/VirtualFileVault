import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

public class FileManager {

    private Connection conn;
    private String vaultFolder = "vault";

    public FileManager(Connection conn) {
        this.conn = conn;
        createVaultFolder();
    }

    // Ensure vault folder exists
    private void createVaultFolder() {
        File folder = new File(vaultFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // Save file bytes physically and record metadata in DB
    public boolean saveFile(int userId, String filename, byte[] fileData) {
        try {
            // Save file physically
            File file = new File(vaultFolder + File.separator + filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileData);
            }

            // Insert metadata in DB
            String fileUrl = file.getAbsolutePath();
            long fileSize = file.length();
            Timestamp uploadTime = new Timestamp(System.currentTimeMillis());

            String sql = "INSERT INTO files (user_id, filename, file_size, upload_time, file_url) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, filename);
            ps.setLong(3, fileSize);
            ps.setTimestamp(4, uploadTime);
            ps.setString(5, fileUrl);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete file physically and from DB
    public boolean deleteFile(int fileId, int userId) {
        try {
            // Get file URL from DB
            String sqlGet = "SELECT file_url FROM files WHERE id = ? AND user_id = ?";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, fileId);
            psGet.setInt(2, userId);
            ResultSet rs = psGet.executeQuery();

            if (rs.next()) {
                String fileUrl = rs.getString("file_url");
                File file = new File(fileUrl);
                if (file.exists()) {
                    file.delete();
                }

                // Delete DB record
                String sqlDel = "DELETE FROM files WHERE id = ? AND user_id = ?";
                PreparedStatement psDel = conn.prepareStatement(sqlDel);
                psDel.setInt(1, fileId);
                psDel.setInt(2, userId);

                return psDel.executeUpdate() == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Load file data by file ID
    public byte[] loadFile(int fileId) {
        try {
            String sql = "SELECT file_url FROM files WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, fileId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String fileUrl = rs.getString("file_url");
                File file = new File(fileUrl);
                if (file.exists()) {
                    return Files.readAllBytes(file.toPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
