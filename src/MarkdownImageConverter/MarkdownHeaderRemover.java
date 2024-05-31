package MarkdownImageConverter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MarkdownHeaderRemover {
    public static void main(String[] args) {
        String directoryPath = "A:\\usr\\MyBlog\\RawBlog";  // Markdown 文件所在目录
        int linesToDelete = 6; // 需要删除的行数，包括 YAML 的分隔符

        try {
            Files.list(Paths.get(directoryPath))
                    .filter(path -> path.toString().endsWith(".md"))
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path);
                            removeHeader(path, lines, linesToDelete);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeHeader(Path filePath, List<String> lines, int linesToDelete) throws IOException {
        int headerStartIndex = -1;

        // 找到 YAML 前端元数据的开始
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals("---")) {
                headerStartIndex = i;
                break;
            }
        }

        // 如果找到了 YAML 前端元数据的开始，并且可以删除包括 --- 在内的指定行数
        if (headerStartIndex != -1 && lines.size() > headerStartIndex + linesToDelete - 1) {
            lines.subList(headerStartIndex, headerStartIndex + linesToDelete).clear();
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Removed header from file: " + filePath);
        }
    }
}