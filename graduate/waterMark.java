package graduate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class waterMark {

    static int aCount = 1;
    static int bCount = 1;
    static int cCount = 1;

    public static void main(String[] args) throws IOException {

        int gap = 8;

        long startTime = System.currentTimeMillis();
        //读取测试图像
        File file = new File("./graduate/lena_256.bmp");
        BufferedImage test_image = readImageFile(file);
        int Height = test_image.getHeight();
        int Width = test_image.getWidth();

        //创建分形编码文件
        File out_file = new File("./graduate/A/encode.txt");
        BufferedWriter out_txt = new BufferedWriter(new FileWriter(out_file));
        out_txt.write(Height + "\t" + Width + "\t" + gap + "\t" + gap + "\n");

        int[][] dct_quantize = getDCT_Quantize(Objects.requireNonNull(readImageFile(new File("./graduate/B/first/286.bmp"))));
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (count % 8 == 0) System.out.println();
                System.out.print(dct_quantize[i][j] + "\t");
                count++;
            }
        }

        // 修改图像的2-LSB
//        BufferedImage newImage = modify2LSB(test_image);
//        writeImageFile(newImage,"modify");

        // 划分三类块
//        getA_block(test_image, gap);
//        getB_block(test_image,4, gap);
//        getC_block(test_image,4, gap);

//        ArrayList<Integer> secondIndex = getPicIndex("./graduate/A/Range/second/");
//        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
//        for (int i = 0; i <= 50; i++) {
//            ArrayList<Integer> ints = new ArrayList<>();
//            ints.add(-1);
//            map.put(i,ints);
//        }
//        HashMap<Integer, ArrayList<Integer>> map1 = new HashMap<>();
//        for (int i = 0; i <= 50; i++) {
//            ArrayList<Integer> ints = new ArrayList<>();
//            ints.add(-1);
//            map1.put(i,ints);
//        }
//        for (int j:secondIndex) {
//            File domainFile = new File("./graduate/A/Domain/" + 1 + "/second/" + j + ".bmp");
//            File domainFile1 = new File("./graduate/A/Domain/" + 7 + "/second/" + j + ".bmp");
//            BufferedImage domainImage = readImageFile(domainFile);
//            BufferedImage domainImage1 = readImageFile(domainFile1);
//            double[][] dct = performDCT(domainImage);
//            double[][] dct1 = performDCT(domainImage1);
//
//            int val = (int) (100 * dct[0][1]);
//            int val1 = (int) (100 * dct1[0][1]);
//            ArrayList<Integer> integers = map.get(val);
//            ArrayList<Integer> integers1 = map1.get(val1);
//            integers.add(j);
//            integers1.add(j);
//            map.replace(val, integers);
//            map1.replace(val1,integers1);
//        }
//
//        Set<Map.Entry<Integer, ArrayList<Integer>>> entries = map.entrySet();
//        for (Map.Entry<Integer, ArrayList<Integer>> next : entries) {
//            System.out.println(next);
//        }
//
//        System.out.println("*******************************");
//
//        Set<Map.Entry<Integer, ArrayList<Integer>>> entries1 = map1.entrySet();
//        for (Map.Entry<Integer, ArrayList<Integer>> next : entries1) {
//            System.out.println(next);
//        }

        // 构建索引树
//        ArrayList<Integer> firstIndex = getPicIndex("./graduate/A/Range/first/");
//        ArrayList<Integer> secondIndex = getPicIndex("./graduate/A/Range/second/");
//        ArrayList<Integer> thirdIndex = getPicIndex("./graduate/A/Range/third/");
//        ArrayList<Integer> forthIndex = getPicIndex("./graduate/A/Range/forth/");
//
//        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map1 = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map2 = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map3 = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map4 = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map5 = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map6 = new HashMap<>();
//        HashMap<Integer, ArrayList<Integer>> map7 = new HashMap<>();
//
//        for (int i = 0; i < 8; i++) {
//            HashMap<Integer, ArrayList<Integer>> M = switch (i) {
//                case 0 -> map;
//                case 1 -> map1;
//                case 2 -> map2;
//                case 3 -> map3;
//                case 4 -> map4;
//                case 5 -> map5;
//                case 6 -> map6;
//                case 7 -> map7;
//                default -> null;
//            };
//            for (int j = 0; j <= 50; j++) {
//                ArrayList<Integer> ints = new ArrayList<>();
//                ints.add(-1);
//                M.put(j,ints);
//            }
//        }
//
//        for (int i = 1; i < 5; i++) {
//            String dir = null;
//            ArrayList<Integer> pack = null;
//            HashMap<Integer, ArrayList<Integer>> M = null;
//            HashMap<Integer, ArrayList<Integer>> M1 = null;
//            switch (i) {
//                case 1 -> {
//                    pack = firstIndex;
//                    dir = "/first/";
//                    M = map;
//                    M1 = map1;
//                }
//                case 2 -> {
//                    pack = secondIndex;
//                    dir = "/second/";
//                    M = map2;
//                    M1 = map3;
//                }
//                case 3 -> {
//                    pack = thirdIndex;
//                    dir = "/third/";
//                    M = map4;
//                    M1 = map5;
//                }
//                case 4 -> {
//                    pack = forthIndex;
//                    dir = "/forth/";
//                    M = map6;
//                    M1 = map7;
//                }
//            }
//            for (int j:pack) {
//                File domainFile = new File("./graduate/A/Domain/" + 1 + dir + j + ".bmp");
//                File domainFile1 = new File("./graduate/A/Domain/" + 7 + dir + j + ".bmp");
//                BufferedImage domainImage = readImageFile(domainFile);
//                BufferedImage domainImage1 = readImageFile(domainFile1);
//                double[][] dct = performDCT(domainImage);
//                double[][] dct1 = performDCT(domainImage1);
//
//                int val = (int) (100 * dct[0][1]);
//                int val1 = (int) (100 * dct1[0][1]);
//                ArrayList<Integer> integers = M.get(val);
//                ArrayList<Integer> integers1 = M1.get(val1);
//                integers.add(j);
//                integers1.add(j);
//                M.replace(val, integers);
//                M1.replace(val1,integers1);
//            }
//        }
//
//        // 处理A类块
//        for (int m = 1; m < 5; m++) {
//            String dir = null;
//            String opp_dir = null;
//            ArrayList<Integer> pack = null;
//            HashMap<Integer, ArrayList<Integer>> M = null;
//            HashMap<Integer, ArrayList<Integer>> M1 = null;
//            switch (m) {
//                case 1 -> {
//                    pack = firstIndex;
//                    dir = "/first/";
//                    opp_dir = "/third/";
//                    M = map4;
//                    M1 = map5;
//                }
//                case 2 -> {
//                    pack = secondIndex;
//                    dir = "/second/";
//                    opp_dir = "/forth/";
//                    M = map6;
//                    M1 = map7;
//                }
//                case 3 -> {
//                    pack = thirdIndex;
//                    dir = "/third/";
//                    opp_dir = "/first/";
//                    M = map;
//                    M1 = map1;
//                }
//                case 4 -> {
//                    pack = forthIndex;
//                    dir = "/forth/";
//                    opp_dir = "/second/";
//                    M = map2;
//                    M1 = map3;
//                }
//            }
//
//            for (Integer i:pack) {
//                BufferedImage rangeImage = readImageFile(new File("./graduate/A/Range" + dir + i + ".bmp"));
//                double[][] dct = performDCT(rangeImage);
//                int val = (int) (100 * dct[0][1]);
//
//                ArrayList<Integer> integers = M.get(val);
//                Iterator<Integer> iterator = integers.iterator();
//                ArrayList<Integer> integers1 = M1.get(val);
//                Iterator<Integer> iterator1 = integers1.iterator();
//
//                int targetDomain = 1, targetTransform = 1;
//                double minMSE = 99999.0;
//
//                while (iterator.hasNext()) {
//                    Integer next = iterator.next();
//                    if (next == -1) continue;
////                System.out.println(next);
//                    for (int j = 1; j < 5; j++) {
//                        BufferedImage domainImage = readImageFile(new File("./graduate/A/Domain/" + j + opp_dir + next + ".bmp"));
//                        double MSE = getError(rangeImage, domainImage);
//                        if (minMSE > MSE) {
//                            minMSE = MSE;
//                            targetDomain = next;
//                            targetTransform = j;
//                        }
//                    }
//                }
//
//                while (iterator1.hasNext()){
//                    Integer next = iterator1.next();
//                    if (next == -1) continue;
//                    for (int j = 5; j < 9; j++) {
//                        BufferedImage domainImage = readImageFile(new File("./graduate/A/Domain/" + j + opp_dir + next + ".bmp"));
//                        double MSE = getError(rangeImage, domainImage);
//                        if(minMSE > MSE){
//                            minMSE = MSE;
//                            targetDomain = next;
//                            targetTransform = j;
//                        }
//                    }
//
//                }
//
//                // 有未匹配的则暴力寻找
//                if (targetTransform == 1 && targetDomain == 1){
//                    int[] ints = violentMatch(rangeImage, m);
//                    targetDomain = ints[0];
//                    targetTransform = ints[1];
//                }
//
//                File targetDomainFile = new File("./graduate/A/Domain/" + targetTransform + opp_dir + targetDomain + ".bmp");
//                BufferedImage targetDomainImage = readImageFile(targetDomainFile);
//                double scalefactor = getScalefactor(rangeImage, targetDomainImage);
//                double offset = getGrayscaleoffset(rangeImage, targetDomainImage);
//                String outcome = "i = " + i + ", j = " + targetDomain + ", k = " + targetTransform  + ", s = " + scalefactor + ", offset = " + offset;
////            System.out.println(outcome);
//                out_txt.write(i + "\t" + targetDomain + "\t" + targetTransform + "\t" + scalefactor + "\t" + offset + "\n");
//                out_txt.flush();
//
//            }
//        }

        long endTime = System.currentTimeMillis();

        long runTime = endTime - startTime;
        System.out.println("运行时间：" + runTime + "ms");

    }

    // 读取测试图像
    public static BufferedImage readImageFile(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 储存图像信息
    public static boolean writeImageFile(BufferedImage image, String name) {
        File outputfile = new File(name + ".bmp");
        try {
            if (ImageIO.write(image, "bmp", outputfile)) {
                System.out.println("图像写入成功！");
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("图像写入失败！");
        return false;
    }

    // 获取图像每个像素灰度值
    public static int[][] getGrayValue(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] array = new int[width][height];

        WritableRaster raster = image.getRaster();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                array[i][j] = raster.getSample(i, j, 0);
            }
        }
        return array;
    }

    // 修改图像块的灰度值
    public static BufferedImage modifyGrayValue(BufferedImage image, Double scalefactor, Double offset) {
        int height = image.getHeight();
        int width = image.getWidth();
        int [][]gray = getGrayValue(image);

        BufferedImage modifiedImage = new BufferedImage(height, width, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = modifiedImage.getRaster();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double value = scalefactor * gray[i][j] + offset;
                raster.setSample(i, j, 0, value);
            }
        }

        return modifiedImage;
    }

    // 暴力寻找最佳匹配块
    public static int[] violentMatch(BufferedImage rangeImage, int index){
        int[] ints = new int[2];
        String path = switch (index) {
            case 1 -> "/third/";
            case 2 -> "/forth/";
            case 3 -> "/first/";
            case 4 -> "/second/";
            default -> null;
        };
        ArrayList<Integer> picIndex = getPicIndex("./graduate/A/Range" + path);
        double minMSE = 99999.0;
        for (int k = 1; k < 9; k++) {
            for (int j:picIndex) {
                BufferedImage domainImage = readImageFile(new File("./graduate/A/Domain/" + k + path + j + ".bmp"));
                double MSE = getError(rangeImage, domainImage);
                if (minMSE > MSE) {
                    minMSE = MSE;
                    ints[0] = j;
                    ints[1] = k;
                }
            }
        }
        return ints;
    }

    // 获取文件夹中图片索引
    public static ArrayList<Integer> getPicIndex(String path) {
        ArrayList<Integer> list = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("文件夹是空的!");
            } else {
                for (File insideFile : files) {
                    if (insideFile.isDirectory()) {
                        getPicIndex(insideFile.getAbsolutePath());
                    } else {
                        if (!insideFile.isHidden()) {
                            String name = insideFile.getName();
                            String[] split = name.split("\\.");
                            list.add(Integer.parseInt(split[0]));
//                            BufferedImage image = readImageFile(insideFile);
//                            System.out.println(split[0]);
                        }

                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return list;
    }

    // 选择仿射变换
    public static BufferedImage selectAffineTrans(BufferedImage image, int i){
        int width = image.getWidth();
        int height = image.getHeight();
        new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage transformedImage = switch (i) {
            case 1 -> rotateImage(image, 0);
            case 2 -> symmetryImage(image, "Vertical");
            case 3 -> symmetryImage(image, "Horizontal");
            case 4 -> rotateImage(image, 180);
            case 5 -> symmetryImage(image, "Pdiagonal");
            case 6 -> rotateImage(image, 270);
            case 7 -> rotateImage(image, 90);
            case 8 -> symmetryImage(image, "Ndiagonal");
            default -> new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        };
        return transformedImage;
    }

    // 图像旋转变换
    public static BufferedImage rotateImage(BufferedImage image, int degree) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] array = getGrayValue(image);
        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster =rotatedImage.getRaster();

        switch(degree){
            case 0:
                rotatedImage = image;
                break;
            case 90:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        raster.setSample(i, j, 0, array[j][height - i -1]);
                    }
                }
                break;
            case 180:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        raster.setSample(i, j, 0, array[height - i -1][width - j -1]);
                    }
                }
                break;
            case 270:
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        raster.setSample(i, j, 0, array[height - j -1][i]);
                    }
                }
                break;
            default:
                System.out.println("输入有误，重新输入！");
                break;
        }
        return rotatedImage;
    }

    // 图像对称变换
    public static BufferedImage symmetryImage(BufferedImage image, String type) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] array = getGrayValue(image);
        BufferedImage symmetryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster =symmetryImage.getRaster();

        switch(type){
            case "Horizontal":
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        raster.setSample(i, j, 0, array[i][height - j -1]);
                    }
                }
                break;
            case "Vertical":
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        raster.setSample(j, i, 0, array[height - j -1][i]);
                    }
                }
                break;
            case "Pdiagonal":
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        raster.setSample(j, i, 0, array[height - i -1][height - j - 1]);
                    }
                }
                break;
            case "Ndiagonal":
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        raster.setSample(i, j, 0, array[j][i]);
                    }
                }
                break;
            default:
                System.out.println("输入有误，重新输入！");
                break;

        }
        return symmetryImage;
    }

    // 计算比例因子
    public static double getScalefactor(BufferedImage Range, BufferedImage Domain){
        double s = 0.0;
        double totalR = 0, totalD = 0, totalD2 = 0;
        double upfirst = 0;
        int width = Range.getWidth();
        int height = Range.getHeight();
        int N = width * height;
        int[][] range = getGrayValue(Range);
        int[][] domain = getGrayValue(Domain);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                totalR += range[i][j];
                totalD += domain[i][j];
                totalD2 += domain[i][j] * domain[i][j];
                upfirst += range[i][j] * domain[i][j];
            }
        }

        s = (N * upfirst - totalR * totalD) / (N * totalD2 - totalD * totalD);
        return s;

    }

    // 计算灰度偏移量
    public static double getGrayscaleoffset(BufferedImage Range, BufferedImage Domain){
        double grayoffset = 0.0;
        int width = Range.getWidth();
        int height = Range.getHeight();
        int N = width * height;
        double first = 0, second = 0;

        int[][] range = getGrayValue(Range);
        int[][] domain = getGrayValue(Domain);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                first += range[i][j];
                second += domain[i][j];
            }
        }
        //System.out.println(first + "\n" + second);
        grayoffset = (first / N )- (second / N) * getScalefactor(Range,Domain);
        return grayoffset;
    }

    // 计算误差
    public static double getError(BufferedImage Range, BufferedImage Domain){
        double e = 0.0;
        int width = Range.getWidth();
        int height = Range.getHeight();
        int N = width * height;
        int[][] range = getGrayValue(Range);
        int[][] domain = getGrayValue(Domain);

        double s = getScalefactor(Range, Domain);
        double o = getGrayscaleoffset(Range, Domain);

        for(int i = 0; i < height; i ++){
            for(int j = 0; j < width; j++){
                e += ( range[i][j] - (s * domain[i][j] + o) ) * ( range[i][j] - (s * domain[i][j] + o) );
            }
        }

        e = e / N;
        return e;
    }

    // DCT变换中间计算
    public static double tempDct(int[][] gray, double u, double v){
        int height = gray.length;
        int width = gray[0].length;
        double res = 0;

        for(int i = 0; i < height; i ++){
            for(int j = 0; j < width; j++){
                res += gray[i][j] * Math.cos((2*i+1) * Math.PI * u / (2*width)) * Math.cos((2*j+1) * Math.PI * v / (2*width));
            }
        }
        return res;
    }

    // 进行DCT变换并标准化
    public static double[][] performDCT(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        double [][]dct = new double[height][width];
        int[][] gray = getGrayValue(image);
        double sum = 0;
        double dct2d = 0;
        double cu;
        double cv;

        for(int u = 0; u < height; u++){
            for(int v = 0; v < width; v++){
                sum = tempDct(gray,u,v);
                if(u == 0){
                    cu = Math.sqrt(1.0/width);
                }else {
                    cu = Math.sqrt(2.0/width);
                }
                if(v == 0){
                    cv = Math.sqrt(1.0/width);
                }else {
                    cv = Math.sqrt(2.0/width);
                }
                dct[u][v] = cu * cv * sum;
                dct2d += dct[u][v] * dct[u][v];
            }
        }
        dct2d = Math.sqrt(dct2d);

        for(int u = 0; u < height; u ++){
            for(int v = 0; v < width; v++) {
                dct[u][v] = Math.abs(dct[u][v]) / dct2d;
                BigDecimal temp = new BigDecimal(dct[u][v]);
                dct[u][v] = temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        }

        return dct;
    }

    // 进行DCT变换并量化（JPEG量化表）
    public static int[][] getDCT_Quantize(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int [][]dct = new int[height][width];
        int[][] gray = getGrayValue(image);
        double sum = 0;
        double cu;
        double cv;

        int[][] table = {{16,11,10,16,24,40,51,61},{12,12,14,19,26,58,60,55},{14,13,16,24,40,57,69,56},
            {14,17,22,29,51,87,80,62},{18,22,37,56,68,109,103,77},{24,35,55,64,81,104,113,92},
                {49,64,78,87,103,121,120,101}, {72,92,95,98,112,100,103,99}};

        for(int u = 0; u < height; u++){
            for(int v = 0; v < width; v++){
                sum = tempDct(gray,u,v);
                if(u == 0){
                    cu = Math.sqrt(1.0/width);
                }else {
                    cu = Math.sqrt(2.0/width);
                }
                if(v == 0){
                    cv = Math.sqrt(1.0/width);
                }else {
                    cv = Math.sqrt(2.0/width);
                }
                dct[u][v] = (int) (cu * cv * sum / table[u][v]);
                BigDecimal temp = BigDecimal.valueOf(dct[u][v]);
                dct[u][v] = (int) temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        }

        return dct;
    }

    // 修改2-LSB
    public static BufferedImage modify2LSB(BufferedImage image){
        int [][]gray = getGrayValue(image);
        BufferedImage modifiedImage = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = modifiedImage.getRaster();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int value = gray[i][j] & 252;
                raster.setSample(i, j, 0, value);
            }
        }

        return modifiedImage;
    }

    // 划分A类块
    public static boolean getA_block(BufferedImage image, int r) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 分割图像
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = image.getSubimage(r * j, r * i, r, r);
                    if (i % 2 == 0 && j % 2 == 0){
                        File outfile = new File("./graduate/A/Range/second/" + aCount + ".bmp");

                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File domainfile = new File("./graduate/A/Domain/" + k + "/second/" + aCount + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", domainfile);
                        }
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 != 0 && j % 2 == 0){
                        File outfile = new File("./graduate/A/Range/third/" + aCount + ".bmp");

                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File domainfile = new File("./graduate/A/Domain/" + k + "/third/" + aCount + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", domainfile);
                        }
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 == 0 && j % 2 != 0){
                        File outfile = new File("./graduate/A/Range/first/" + aCount + ".bmp");

                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File domainfile = new File("./graduate/A/Domain/" + k + "/first/" + aCount + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", domainfile);
                        }
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 != 0 && j % 2 != 0){
                        File outfile = new File("./graduate/A/Range/forth/" + aCount + ".bmp");

                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File domainfile = new File("./graduate/A/Domain/" + k + "/forth/" + aCount + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", domainfile);
                        }
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    System.out.println("正在划分第" + aCount +"块A类块");
                    aCount++;
                }
            }
            System.out.println("A类块处理完成!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("A类块处理失败!");
        return false;
    }

    // 划分B类块
    public static boolean getB_block(BufferedImage image, int len, int r) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage splicingImage = image;

        // 生成新图像
        try {
//            for (int i = 0; i < height/len; i++) {
                BufferedImage image1 = splicingImage.getSubimage(0, 0, width, len);
                BufferedImage image2 = splicingImage.getSubimage(0, len, width, height-len);
                splicingImage = SplicingImage(image1, image2, 1);
                File outfile = new File("./graduate/B/newB.bmp");
                ImageIO.write(splicingImage, "bmp", outfile);
//                bCount++;
//            }
//            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 划分子块
        BufferedImage new_Image = readImageFile(new File("./graduate/B/newB.bmp"));
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = new_Image.getSubimage(r * j, r * i, r, r);
                    if (i % 2 == 0 && j % 2 == 0){
                        File outfile = new File("./graduate/B/second/" + bCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 != 0 && j % 2 == 0){
                        File outfile = new File("./graduate/B/third/" + bCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 == 0 && j % 2 != 0){
                        File outfile = new File("./graduate/B/first/" + bCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 != 0 && j % 2 != 0){
                        File outfile = new File("./graduate/B/forth/" + bCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    System.out.println("正在划分第" + bCount +"块B类块");
                    bCount++;
                }
            }
            System.out.println("B类块处理完成!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 划分C类块
    public static boolean getC_block(BufferedImage image, int len, int r) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage splicingImage = image;

        // 生成新的图像
        try {
//            for (int i = 0; i < width/len; i++) {
                BufferedImage image1 = splicingImage.getSubimage(0, 0, len, height);
                BufferedImage image2 = splicingImage.getSubimage(len, 0, width-len, height);
                splicingImage = SplicingImage(image1, image2, 2);
                File outfile = new File("./graduate/C/newC.bmp");
                ImageIO.write(splicingImage, "bmp", outfile);
//                cCount++;
//            }
//            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 划分子块
        BufferedImage new_Image = readImageFile(new File("./graduate/C/newC.bmp"));
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = new_Image.getSubimage(r * j, r * i, r, r);
                    if (i % 2 == 0 && j % 2 == 0){
                        File outfile = new File("./graduate/C/second/" + cCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 != 0 && j % 2 == 0){
                        File outfile = new File("./graduate/C/third/" + cCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 == 0 && j % 2 != 0){
                        File outfile = new File("./graduate/C/first/" + cCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    if (i % 2 != 0 && j % 2 != 0){
                        File outfile = new File("./graduate/C/forth/" + cCount + ".bmp");
                        ImageIO.write(subImage, "bmp", outfile);
                    }
                    System.out.println("正在划分第" + cCount +"块C类块");
                    cCount++;
                }
            }
            System.out.println("C类块处理完成!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 拼接图像
    public static BufferedImage SplicingImage(BufferedImage image1, BufferedImage image2, int option){
        BufferedImage res = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_GRAY);
        int[][] gray1 = getGrayValue(image1);
        int[][] gray2 = getGrayValue(image2);
        WritableRaster raster = res.getRaster();

        switch (option){
            case 1:
                for (int i = 0; i < gray2.length; i++) {
                    for (int j = 0; j < gray2[0].length; j++) {
                        raster.setSample(i,j,0, gray2[i][j]);
                    }
                }
                for (int i = 0; i < gray1.length; i++) {
                    for (int j = 0; j < gray1[0].length; j++) {
                        raster.setSample(i,j+gray2[0].length,0, gray1[i][j]);
                    }
                }
                break;
            case 2:
                for (int i = 0; i < gray2.length; i++) {
                    for (int j = 0; j < gray2[0].length; j++) {
                        raster.setSample(i,j,0, gray2[i][j]);
                    }
                }
                for (int i = 0; i < gray1.length; i++) {
                    for (int j = 0; j < gray1[0].length; j++) {
                        raster.setSample(i+gray2.length,j,0, gray1[i][j]);
                    }
                }
                break;
            default:
                System.out.println("输入有误！");
        }

        return res;
    }

}
