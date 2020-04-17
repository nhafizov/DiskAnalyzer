package com.nkjavaproject.diskanalyzer;

class FileInfo implements Comparable<FileInfo> {
    private long size;
    private String filePath;

    FileInfo(String filePath, long size) {
        this.filePath = filePath;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public int compareTo(FileInfo other) {
        return Long.compare(size, other.getSize());
    }
}
