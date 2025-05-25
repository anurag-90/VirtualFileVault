import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.sql.Timestamp;
import java.util.List;

public class VaultDashboard extends JFrame {
    private User currentUser;
    private JTable fileTable;
    private DefaultTableModel tableModel;

    public VaultDashboard(User user) {
        this.currentUser = user;
        setTitle("Vault - " + user.getUsername());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"ID", "Filename", "Size (bytes)", "Upload Time"}, 0);
        fileTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(fileTable);

        // Buttons
        JButton uploadBtn = new JButton("Upload");
        JButton downloadBtn = new JButton("Download");
        JButton deleteBtn = new JButton("Delete");

        JPanel btnPanel = new JPanel();
        btnPanel.add(uploadBtn);
        btnPanel.add(downloadBtn);
        btnPanel.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadFiles();

        uploadBtn.addActionListener(e -> uploadFile());
        deleteBtn.addActionListener(e -> deleteFile());
        downloadBtn.addActionListener(e -> downloadFile());
    }

    private void loadFiles() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db")) {
            FileDao dao = new FileDao(conn);
            List<VaultFile> files = dao.getFilesByUser(currentUser.getId());
            for (VaultFile f : files) {
                tableModel.addRow(new Object[]{
                        f.getId(),
                        f.getFilename(),
                        f.getFileSize(),
                        f.getUploadTime()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFile() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            try {
                String encryptedName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path dest = Paths.get("vault/" + encryptedName);
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                byte[] encrypted = CryptoUtil.encryptBytes(fileBytes);
                Files.write(dest, encrypted);

                VaultFile file = new VaultFile(
                        currentUser.getId(),
                        selectedFile.getName(),
                        selectedFile.length(),
                        new Timestamp(System.currentTimeMillis()),
                        dest.toString()
                );

                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db")) {
                    FileDao dao = new FileDao(conn);
                    if (dao.saveFile(file)) {
                        JOptionPane.showMessageDialog(this, "Upload successful");
                        loadFiles();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFile() {
        int row = fileTable.getSelectedRow();
        if (row == -1) return;
        int fileId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db")) {
            FileDao dao = new FileDao(conn);
            VaultFile file = dao.getFileById(fileId);
            if (file != null && dao.deleteFile(fileId, currentUser.getId())) {
                Files.deleteIfExists(Paths.get(file.getFileUrl()));
                JOptionPane.showMessageDialog(this, "Deleted");
                loadFiles();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile() {
        int row = fileTable.getSelectedRow();
        if (row == -1) return;
        int fileId = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db")) {
            FileDao dao = new FileDao(conn);
            VaultFile file = dao.getFileById(fileId);
            if (file != null) {
                byte[] encrypted = Files.readAllBytes(Paths.get(file.getFileUrl()));
                byte[] decrypted = CryptoUtil.decryptBytes(encrypted);

                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File(file.getFilename()));
                int res = fc.showSaveDialog(this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    Files.write(fc.getSelectedFile().toPath(), decrypted);
                    JOptionPane.showMessageDialog(this, "Downloaded");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
