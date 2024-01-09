package com.xinyu.idol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanFileUtils {

   static Map<String, String> staticMap=new HashMap<>();

   static List<String> matchedGroupList=new ArrayList<>();


    public static void main(String[] args) {
        //获取html页的根目录
        File[] allHtmlFiles = getAllHtmlFiles("G:\\xx1\\jsonx.cc");
        //获取要被扫描静态文件的相对目录
        staticMap= getAllFilesAbsPath("G:\\xxx\\xx.cc\\static");

        //遍历html目录
        for (File allHtmlFile : allHtmlFiles) {

            scanHtmlFile(allHtmlFile.getAbsolutePath());
        }

       // System.out.println(staticMap.size());

        //删除已经在html里出现过的的static文件链接
        for (String s : matchedGroupList) {
           if(staticMap.containsKey(s)){
               staticMap.remove(s);
           }
        }

        for (Map.Entry<String,String> entry:staticMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            deleteThisFile(entry.getValue());
        }

        System.out.println(staticMap.size());
    }
    // 获取当前目录下所有的HTML文件
    private static File[] getAllHtmlFiles(String currentDir){


        File directory = new File(currentDir);
        File[] htmlFiles = directory.listFiles((File dir, String name)->{
            return name.toLowerCase().endsWith(".html");
        });

        // 输出文件列表
        if (htmlFiles != null) {
            for (File file : htmlFiles) {

                System.out.println(file.getName());
            }
        }

        return htmlFiles;
    }
    //扫描单个html文件
    public static void scanHtmlFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 使用正则表达式匹配链接
                Pattern pattern = Pattern.compile("href=\"([^\"]+)\"|src=\"([^\"]+)\"");
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    // 提取匹配的链接
                    String matchedGroup = matcher.group(1);
                    if (matchedGroup == null) {
                        matchedGroup = matcher.group(2);
                    }
                    matchedGroupList.add(matchedGroup);

                    // 打印或进一步处理链接
                   // System.out.println("Found file reference: " + matchedGroup);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取相对路径和绝对路径map，k是相对路径，v是绝对路径
    public static Map<String,String> getAllFilesAbsPath(String folderPath) {
        Map<String,String> pathMap = new HashMap<>();

        try {
            Path folder = Paths.get(folderPath);

            // 遍历文件夹下的所有文件和子文件夹
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // 获取相对路径并添加到列表
                    Path relativePath = folder.relativize(file);


                    pathMap.put("static/"+relativePath.toString().replace("\\","/"),file.toAbsolutePath().toString().replace("\\","/"));

                    //System.out.println();
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathMap;
    }
    //删除这个文件
    private static void deleteThisFile(String filePath){
        try {
            // 使用Paths.get()创建Path对象
            Path path = Paths.get(filePath);

            // 使用Files.delete()方法删除文件
            Files.delete(path);

            System.out.println("文件删除成功");
        } catch (NoSuchFileException e) {
            System.err.println("文件不存在：" + e.getMessage());
        } catch (DirectoryNotEmptyException e) {
            System.err.println("目录非空：" + e.getMessage());
        } catch (IOException e) {
            System.err.println("删除文件时出错：" + e.getMessage());
        }

    }
}
