package com.nkjavaproject.diskanalyzer;

public class Main {
    public static void main(String[] args) {
        String directoryPath = "/bin";
        DiskAnalyzer disk = new DiskAnalyzer(directoryPath);
        disk.print();
    }
}
