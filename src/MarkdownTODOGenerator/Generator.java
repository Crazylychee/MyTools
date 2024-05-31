package MarkdownTODOGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Generator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入要生成的天数：");
        int days = scanner.nextInt();
        scanner.nextLine(); // 消耗掉换行符

        List<String> markdownLines = new ArrayList<>();
        System.out.println("请输入事件数量：");
        int events = scanner.nextInt();
        scanner.nextLine(); // 消耗掉换行符

        System.out.println("请输入你要从第几天开始：  格式：(yyyy-MM-dd)");
        String startDate = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(startDate, formatter);


        for (int day = 1; day <= days; day++) {
            LocalDate nextDate = date.plusDays(day-1);
            markdownLines.add("- 第" + day + "天 - "+ nextDate.format(formatter));

            for (int event = 1; event <= events; event++) {
                markdownLines.add("- [ ] ");
            }
        }
        scanner.close();

        // 输出Markdown格式的Todo列表
        for (String line : markdownLines) {
            System.out.println(line);
        }
    }


}
