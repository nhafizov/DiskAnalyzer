package com.nkjavaproject.diskanalyzer;

import java.io.File;
import java.util.Objects;
import java.util.PriorityQueue;
import java.nio.file.Files;

class DiskAnalyzer {
    public static final int NUM_BIGGEST_FILES = 9;
    public static final int KB_SIZE = 1024;
    public static final double NANOSECONDS_TO_SECONDS = 1_000_000_000.0;
    public static final int PERCENT_MULTIPLIER = 100;
    private File parentDirectory;


    DiskAnalyzer(String directoryPath) {
        try {
            parentDirectory = new File(directoryPath);
            if (!parentDirectory.isDirectory()) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.err.print("Not a directory");
        }
    }


    // DFS search
    private void getStatistics(File file, long[] stats) {
        if (!file.exists() || Files.isSymbolicLink(file.toPath())) {
            return;
        }
        stats[0]++;
        if (file.isFile()) {
            stats[1] += file.length();
        }
        if (file.isDirectory()) {
            if (file.list() == null) {
                return;
            }
            for (File curFile : Objects.requireNonNull(file.listFiles())) {
                getStatistics(curFile, stats);
            }
        }
    }

    // DFS search + min heap
    private void getBiggestFiles(File file, PriorityQueue<FileInfo> biggestFiles) {
        if (!file.exists() || Files.isSymbolicLink(file.toPath())) {
            return;
        }
        if (file.isFile()) {
            biggestFiles.add(new FileInfo(file.getAbsolutePath(), file.length()));
            while (biggestFiles.size() > NUM_BIGGEST_FILES) {
                biggestFiles.poll();
            }
        }
        if (file.isDirectory()) {
            if (file.listFiles() == null) {
                return;
            }
            for (File curFile : Objects.requireNonNull(file.listFiles())) {
                getBiggestFiles(curFile, biggestFiles);
            }
        }

    }

    private void printDirectoryTable() {
        File[] parentDirectoryFiles = parentDirectory.listFiles();
        Objects.requireNonNull(parentDirectoryFiles);
        long[] parentDirectoryStats = {-1L, 0};
        getStatistics(parentDirectory, parentDirectoryStats);
        if (parentDirectoryStats[0] == -1L) {
            parentDirectoryStats[0] = 0L;
        }
        for (int i = 0; i < parentDirectoryFiles.length; i++) {
            if (parentDirectoryFiles[i].isDirectory()) {
                long[] curDirectoryStats = {-1L, 0L}; // stats[0] - количество файлов, stats[1] - размер в байтах
                getStatistics(parentDirectoryFiles[i], curDirectoryStats);
                if (curDirectoryStats[0] == -1L) {
                    curDirectoryStats[0] = 0L;
                }
                System.out.format("%4d. %64s| %16d Kb| %5.2f%%| %8d items%n", i + 1, parentDirectoryFiles[i].getName()
                        + "/", curDirectoryStats[1] / KB_SIZE, ((double) curDirectoryStats[1] / parentDirectoryStats[1])
                        * PERCENT_MULTIPLIER, curDirectoryStats[0]);
            } else if (parentDirectoryFiles[i].isFile()) {
                System.out.format("%4d. %64s| %16d Kb| %5.2f%%%n", i + 1, parentDirectoryFiles[i].getName(),
                        parentDirectoryFiles[i].length() / KB_SIZE, ((double) parentDirectoryFiles[i].length()
                                / parentDirectoryStats[1]) * PERCENT_MULTIPLIER);
            }
        }
    }

    private void printBiggestFiles() {
        int i = 1;
        PriorityQueue<FileInfo> biggestFiles = new PriorityQueue<>();
        getBiggestFiles(parentDirectory, biggestFiles);
        FileInfo[] files = new FileInfo[biggestFiles.size()];
        System.out.println("--------- Biggest Files ---------");
        while (!biggestFiles.isEmpty()) {
            files[(i++) - 1] = biggestFiles.poll();
        }
        for (int k = 0; k < files.length; k++) {
            System.out.format("%1d. %16d Kb| %32s%n", k + 1, files[files.length - 1 - k].getSize() / KB_SIZE,
                    files[files.length - 1 - k].getFilePath());
        }
    }

    public void print() {
        long startTime = System.nanoTime();
        printDirectoryTable();
        printBiggestFiles();
        System.out.format("Total time: %.3fs%n", (System.nanoTime() - startTime) / NANOSECONDS_TO_SECONDS);
    }
}
