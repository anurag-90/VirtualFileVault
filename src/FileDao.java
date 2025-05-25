import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDao {

    private Connection conn;

    public FileDao(Connection conn) {
        this.conn = conn;
    }

    // 1. Save File
    public boolean saveFile(VaultFile file) {
        boolean success = false;
        try {
            String sql = "INSERT INTO files(user_id, filename, file_size, upload_time, file_url) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, file.getUserId());
            ps.setString(2, file.getFilename());
            ps.setLong(3, file.getFileSize());
            ps.setTimestamp(4, file.getUploadTime());
            ps.setString(5, file.getFileUrl());

            success = ps.executeUpdate() == 1;
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    // 2. Get Files by User ID
    public List<VaultFile> getFilesByUser(int userId) {
        List<VaultFile> files = new ArrayList<>();
        try {
            String sql = "SELECT * FROM files WHERE user_id = ? ORDER BY upload_time DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VaultFile file = new VaultFile(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("filename"),
                        rs.getLong("file_size"),
                        rs.getTimestamp("upload_time"),
                        rs.getString("file_url")
                );
                files.add(file);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    // 3. Delete File by ID
    public boolean deleteFile(int fileId, int userId) {
        boolean success = false;
        try {
            String sql = "DELETE FROM files WHERE id = ? AND user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, fileId);
            ps.setInt(2, userId);

            success = ps.executeUpdate() == 1;
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    // 4. Get single file by ID
    public VaultFile getFileById(int fileId) {
        VaultFile file = null;
        try {
            String sql = "SELECT * FROM files WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, fileId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                file = new VaultFile(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("filename"),
                        rs.getLong("file_size"),
                        rs.getTimestamp("upload_time"),
                        rs.getString("file_url")
                );
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
