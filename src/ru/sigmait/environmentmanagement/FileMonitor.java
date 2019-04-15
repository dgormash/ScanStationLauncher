package ru.sigmait.environmentmanagement;

public class FileMonitor implements FileDownloadedListener {
    @Override
    public void FileDownloaded(String filePath) {
        System.out.println("File " + filePath + " is downloaded");
    }
}
