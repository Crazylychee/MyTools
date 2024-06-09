package MarkdownImageConverter;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.*;

public class MarkdownImageConverter {
    public static void main(String[] args) {
        String inputFolder = "A:\\usr\\MyBlog\\RawBlog";  // 输入文件夹路径
        String outputFolder = "A:\\usr\\MyBlog\\CrazylycheeBlog\\source\\_posts";  // 输出文件夹路径

        addTheHexoBegin(inputFolder);

        File inputDir = new File(inputFolder);
        File outputDir = new File(outputFolder);

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] files = inputDir.listFiles((dir, name) -> name.endsWith(".md"));

        if (files != null) {
            for (File file : files) {
                try {
                    convertFile(file, outputDir);
                    copyRelatedFolder(file, inputDir, outputDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void convertFile(File inputFile, File outputDir) throws IOException {
        String content = new String(Files.readAllBytes(inputFile.toPath()));

        String name = inputFile.getName().replaceAll("\\.md$", "");

        String redix1 = "<img src=\""+name+"/([^/][^\"]+)\" alt=\"([^\"]+)\" />";
        String redix2 ="!\\[([^\\]]+)\\]\\("+name+"/([^/][^\\)]+)\\)";

        // 处理 HTML 格式的 <img src="Logback/..."> 标签
        Pattern htmlPattern = Pattern.compile(redix1);
        Matcher htmlMatcher = htmlPattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (htmlMatcher.find()) {
            String replacement = String.format("{%% asset_img %s %s %%}", htmlMatcher.group(1), htmlMatcher.group(2));
            htmlMatcher.appendReplacement(sb, replacement);
        }
        htmlMatcher.appendTail(sb);

        // 处理 Markdown 格式的 ![在这里插入图片描述](Logback/...)
        String updatedContent = sb.toString();
        sb.setLength(0); // 清空 StringBuffer

        Pattern markdownPattern = Pattern.compile(redix2);
        Matcher markdownMatcher = markdownPattern.matcher(updatedContent);

        while (markdownMatcher.find()) {
            String replacement = String.format("{%% asset_img %s %s %%}", markdownMatcher.group(2), markdownMatcher.group(1));
            markdownMatcher.appendReplacement(sb, replacement);
        }
        markdownMatcher.appendTail(sb);

        File outputFile = new File(outputDir, inputFile.getName());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(sb.toString());
        }

        System.out.println("Processed " + inputFile.getName());
    }

    private static void copyRelatedFolder(File inputFile, File inputDir, File outputDir) throws IOException {
        String baseName = inputFile.getName().replaceAll("\\.md$", "");
        File sourceFolder = new File(inputDir, baseName);
        File destinationFolder = new File(outputDir, baseName);

        if (sourceFolder.exists() && sourceFolder.isDirectory()) {
            copyDirectory(sourceFolder.toPath(), destinationFolder.toPath());
            System.out.println("Copied folder " + baseName);
        }
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                try {
                    Files.createDirectories(targetDir);
                } catch (FileAlreadyExistsException e) {
                    if (!Files.isDirectory(targetDir)) throw e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void addTheHexoBegin(String directoryPath) {

        String beginFilePath = "A:\\usr\\JavaProject\\Mytools\\src\\MarkdownImageConverter\\begin.txt";       // begin.txt 文件路径

        try {
            List<String> beginLines = Files.readAllLines(Paths.get(beginFilePath));
            String beginContent = String.join(System.lineSeparator(), beginLines) + System.lineSeparator();

            Files.list(Paths.get(directoryPath))
                    .filter(path -> path.toString().endsWith(".md"))
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path);
                            if (lines.isEmpty() || !lines.get(0).trim().equals("---")) {
                                System.out.println("Updating file: " + path);
                                addHeaderToFile(path, beginContent);
                            }else {
                                checkAndAddTitle(path, lines);
                                checkAndAddDate(path, lines);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addHeaderToFile(Path filePath, String headerContent) throws IOException {
        List<String> originalLines = Files.readAllLines(filePath);
        String originalContent = String.join(System.lineSeparator(), originalLines);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(headerContent);
            writer.write(originalContent);
        }
    }

    private static void checkAndAddTitle(Path filePath, List<String> lines) throws IOException {
        boolean titleFound = false;
        boolean titleEmpty = false;
        int titleIndex = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("title:")) {
                titleFound = true;
                if (line.equals("title:") || line.equals("title: ")) {
                    titleEmpty = true;
                    titleIndex = i;
                }
                break;
            }
        }

        if (titleFound && titleEmpty) {
            String fileName = filePath.getFileName().toString();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            lines.set(titleIndex, "title: " + baseName);

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Added file name to title in file: " + filePath);
        }
    }

    private static void checkAndAddDate(Path filePath, List<String> lines) throws IOException {
        boolean dateFound = false;
        boolean dateEmpty = false;
        int dateIndex = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("date:")) {
                dateFound = true;
                if (line.equals("date:") || line.equals("date: ")) {
                    dateEmpty = true;
                    dateIndex = i;
                }
                break;
            }
        }

        if (dateFound && dateEmpty) {
            //设置为今天
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            lines.set(dateIndex, "date: " + date);

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Added file name to title in file: " + filePath);
        }
    }

}