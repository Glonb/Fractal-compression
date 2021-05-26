package graduate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class waterMark {

    static int aCount = 1;
    static int d_1_Cnt = 1;
    static int d_2_Cnt = 1;
    static int d_3_Cnt = 1;
    static int d_4_Cnt = 1;
    static int bCount = 1;
    static int cCount = 1;
    static Vector<String> encode_file = new Vector<>();
    static HashMap<Integer, Integer> a_p_map;
    static HashMap<Integer, Integer> b_p_map;
    static HashMap<Integer, Integer> c_p_map;

    static {
        try {
            a_p_map = readPMatrix("graduate/PM_A.txt");
            b_p_map = readPMatrix("graduate/PM_B.txt");
            c_p_map = readPMatrix("graduate/PM_C.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        int gap = 8, step = 4;

        long startTime = System.currentTimeMillis();
        //读取测试图像
        File file = new File("graduate/lena_256.bmp");
        BufferedImage test_image = readImageFile(file);
        int Height = Objects.requireNonNull(test_image).getHeight();
        int Width = test_image.getWidth();

        //创建分形编码文件
        File out_file = new File("graduate/A/encode.txt");
        BufferedWriter out_txt = new BufferedWriter(new FileWriter(out_file));
        out_txt.write(Height + "\t" + Width + "\t" + gap + "\t" + step + "\n");

        // 修改图像的2-LSB
        BufferedImage newImage = modify2LSB(test_image);
        writeImageFile(newImage,"graduate/modify");
        BufferedImage modifiedImage = readImageFile(new File("graduate/modify.bmp"));

        // 划分三类块并划分四个象限
        getA_block(Objects.requireNonNull(modifiedImage), gap, step);
        getB_block(modifiedImage,4, gap);
        getC_block(modifiedImage,4, gap);

        // 构建索引树
        ArrayList<Integer> A_firstIndex = getPicIndex("graduate/A/Range/first/");
        ArrayList<Integer> A_secondIndex = getPicIndex("graduate/A/Range/second/");
        ArrayList<Integer> A_thirdIndex = getPicIndex("graduate/A/Range/third/");
        ArrayList<Integer> A_forthIndex = getPicIndex("graduate/A/Range/forth/");
        ArrayList<Integer> A_d_firstIndex = getPicIndex("graduate/A/Domain/1/first/");
        ArrayList<Integer> A_d_secondIndex = getPicIndex("graduate/A/Domain/1/second/");
        ArrayList<Integer> A_d_thirdIndex = getPicIndex("graduate/A/Domain/1/third/");
        ArrayList<Integer> A_d_forthIndex = getPicIndex("graduate/A/Domain/1/forth/");

        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map1 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map2 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map3 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map4 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map5 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map6 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> map7 = new HashMap<>();

        for (int i = 0; i < 8; i++) {
            HashMap<Integer, ArrayList<Integer>> M = switch (i) {
                case 0 -> map;
                case 1 -> map1;
                case 2 -> map2;
                case 3 -> map3;
                case 4 -> map4;
                case 5 -> map5;
                case 6 -> map6;
                case 7 -> map7;
                default -> null;
            };
            for (int j = 0; j <= 100; j++) {
                ArrayList<Integer> ints = new ArrayList<>();
                ints.add(-1);
                M.put(j,ints);
            }
        }

        for (int i = 1; i < 5; i++) {
            String dir = null;
            ArrayList<Integer> pack = null;
            HashMap<Integer, ArrayList<Integer>> M = null;
            HashMap<Integer, ArrayList<Integer>> M1 = null;
            switch (i) {
                case 1 -> {
                    pack = A_d_firstIndex;
                    dir = "/first/";
                    M = map;
                    M1 = map1;
                }
                case 2 -> {
                    pack = A_d_secondIndex;
                    dir = "/second/";
                    M = map2;
                    M1 = map3;
                }
                case 3 -> {
                    pack = A_d_thirdIndex;
                    dir = "/third/";
                    M = map4;
                    M1 = map5;
                }
                case 4 -> {
                    pack = A_d_forthIndex;
                    dir = "/forth/";
                    M = map6;
                    M1 = map7;
                }
            }
            for (int j:pack) {
                File domainFile = new File("graduate/A/Domain/" + 1 + dir + j + ".bmp");
                File domainFile1 = new File("graduate/A/Domain/" + 7 + dir + j + ".bmp");
                BufferedImage domainImage = readImageFile(domainFile);
                BufferedImage domainImage1 = readImageFile(domainFile1);
                double[][] dct = performDCT(Objects.requireNonNull(domainImage));
                double[][] dct1 = performDCT(Objects.requireNonNull(domainImage1));

                int val = (int) (100 * dct[0][1]);
                int val1 = (int) (100 * dct1[0][1]);
                ArrayList<Integer> integers = M.get(val);
                ArrayList<Integer> integers1 = M1.get(val1);
                integers.add(j);
                integers1.add(j);
                M.replace(val, integers);
                M1.replace(val1,integers1);
            }
        }

        // 处理A类块
        for (int m = 1; m < 5; m++) {
            String dir = null;
            String opp_dir = null;
            ArrayList<Integer> src_pack = null;
            HashMap<Integer, ArrayList<Integer>> M = null;
            HashMap<Integer, ArrayList<Integer>> M1 = null;
            switch (m) {
                case 1 -> {
                    src_pack = A_firstIndex;
                    dir = "/first/";
                    opp_dir = "/third/";
                    M = map4;
                    M1 = map5;
                }
                case 2 -> {
                    src_pack = A_secondIndex;
                    dir = "/second/";
                    opp_dir = "/forth/";
                    M = map6;
                    M1 = map7;
                }
                case 3 -> {
                    src_pack = A_thirdIndex;
                    dir = "/third/";
                    opp_dir = "/first/";
                    M = map;
                    M1 = map1;
                }
                case 4 -> {
                    src_pack = A_forthIndex;
                    dir = "/forth/";
                    opp_dir = "/second/";
                    M = map2;
                    M1 = map3;
                }
            }

            for (Integer i:src_pack) {
                BufferedImage rangeImage = readImageFile(new File("graduate/A/Range" + dir + i + ".bmp"));
                double[][] dct = performDCT(Objects.requireNonNull(rangeImage));
                int val = (int) (100 * dct[0][1]);

                ArrayList<Integer> integers = M.get(val);
                Iterator<Integer> iterator = integers.iterator();
                ArrayList<Integer> integers1 = M1.get(val);
                Iterator<Integer> iterator1 = integers1.iterator();

                int targetDomain = 1, targetTransform = 1;
                double minMSE = 99999.0;

                while (iterator.hasNext()) {
                    Integer next = iterator.next();
                    if (next == -1) continue;
                    for (int j = 1; j < 5; j++) {
                        BufferedImage domainImage = readImageFile(new File("graduate/A/Domain/" + j + opp_dir + next + ".bmp"));
                        double MSE = getError(rangeImage, domainImage);
                        if (minMSE > MSE) {
                            minMSE = MSE;
                            targetDomain = next;
                            targetTransform = j;
                        }
                    }
                }

                while (iterator1.hasNext()){
                    Integer next = iterator1.next();
                    if (next == -1) continue;
                    for (int j = 5; j < 9; j++) {
                        BufferedImage domainImage = readImageFile(new File("graduate/A/Domain/" + j + opp_dir + next + ".bmp"));
                        double MSE = getError(rangeImage, domainImage);
                        if(minMSE > MSE){
                            minMSE = MSE;
                            targetDomain = next;
                            targetTransform = j;
                        }
                    }

                }

                // 有未匹配的则暴力寻找
                if (targetTransform == 1 && targetDomain == 1){
                    int[] ints = violentMatch(rangeImage, m);
                    targetDomain = ints[0];
                    targetTransform = ints[1];
                }

                File targetDomainFile = new File("graduate/A/Domain/" + targetTransform + opp_dir + targetDomain + ".bmp");
                BufferedImage targetDomainImage = readImageFile(targetDomainFile);
                int scalefactor = getScalefactor(rangeImage, targetDomainImage);
                int offset = getGrayscaleoffset(rangeImage, targetDomainImage);
                float error = getError(rangeImage, targetDomainImage);
                String outcome = "i = " + i + ", j = " + targetDomain + ", k = " + targetTransform  + ", " +
                        "s = " + scalefactor + ", offset = " + offset + ", Error = " + error;
                System.out.println(outcome);
                out_txt.write(i + "\t" + targetDomain + "\t" + targetTransform + "\t" + scalefactor + "\t" + offset + "\n");
                out_txt.flush();
            }
        }

        // 嵌入分形编码信息
        readEncodeFile("graduate/A/encode.txt");
        embedFractalCode(A_firstIndex, A_thirdIndex, 1);
        embedFractalCode(A_secondIndex, A_forthIndex, 2);
        embedFractalCode(A_thirdIndex, A_firstIndex, 3);
        embedFractalCode(A_forthIndex, A_secondIndex, 4);

        // 处理B类块
        ArrayList<Integer> B_firstIndex = getPicIndex("graduate/B/first/");
        ArrayList<Integer> B_secondIndex = getPicIndex("graduate/B/second/");
        ArrayList<Integer> B_thirdIndex = getPicIndex("graduate/B/third/");
        ArrayList<Integer> B_forthIndex = getPicIndex("graduate/B/forth/");

        for (int i = 0; i < 4; i++) {
            String dir = null;
            String opp_dir = null;
            ArrayList<Integer> src = null;
            ArrayList<Integer> dst = null;
            switch (i) {
                case 0 -> {
                    dir = "first/";
                    opp_dir = "second/";
                    src = B_firstIndex;
                    dst = A_secondIndex;
                }
                case 1 -> {
                    dir = "second/";
                    opp_dir = "third/";
                    src = B_secondIndex;
                    dst = A_thirdIndex;
                }
                case 2 -> {
                    dir = "third/";
                    opp_dir = "forth/";
                    src = B_thirdIndex;
                    dst = A_forthIndex;
                }
                case 3 -> {
                    dir = "forth/";
                    opp_dir = "first/";
                    src = B_forthIndex;
                    dst = A_firstIndex;
                }
            }

            // 嵌入DCT信息
            for (int m = 0; m < src.size(); m++) {
                Integer src_integer = src.get(m);
                Integer dst_integer = dst.get(b_p_map.get(m));
                BufferedImage src_image = readImageFile(new File("graduate/B/" + dir + src_integer + ".bmp"));
                BufferedImage dst_image = readImageFile(new File("graduate/A/Range/"+ opp_dir + dst_integer + ".bmp"));
                embedDctCoefficient(Objects.requireNonNull(src_image),dst_image,1,"graduate/A/Range/"+ opp_dir + dst_integer);
            }
        }

        // 处理C类块
        ArrayList<Integer> C_firstIndex = getPicIndex("graduate/C/first/");
        ArrayList<Integer> C_secondIndex = getPicIndex("graduate/C/second/");
        ArrayList<Integer> C_thirdIndex = getPicIndex("graduate/C/third/");
        ArrayList<Integer> C_forthIndex = getPicIndex("graduate/C/forth/");

        for (int i = 0; i < 4; i++) {
            String dir = null;
            String opp_dir = null;
            ArrayList<Integer> src = null;
            ArrayList<Integer> dst = null;
            switch (i) {
                case 0 -> {
                    dir = "first/";
                    opp_dir = "forth/";
                    src = C_firstIndex;
                    dst = A_forthIndex;
                }
                case 1 -> {
                    dir = "second/";
                    opp_dir = "first/";
                    src = C_secondIndex;
                    dst = A_firstIndex;
                }
                case 2 -> {
                    dir = "third/";
                    opp_dir = "second/";
                    src = C_thirdIndex;
                    dst = A_secondIndex;
                }
                case 3 -> {
                    dir = "forth/";
                    opp_dir = "third/";
                    src = C_forthIndex;
                    dst = A_thirdIndex;
                }
            }

            // 嵌入DCT信息
            for (int m = 0; m < src.size(); m++) {
                Integer src_integer = src.get(m);
                Integer dst_integer = dst.get(c_p_map.get(m));
                BufferedImage src_image = readImageFile(new File("graduate/C/" + dir + src_integer + ".bmp"));
                BufferedImage dst_image = readImageFile(new File("graduate/A/Range/"+ opp_dir + dst_integer + ".bmp"));
                embedDctCoefficient(Objects.requireNonNull(src_image),dst_image,2,"graduate/A/Range/"+ opp_dir + dst_integer);
            }
        }

        // 嵌入校验和
        for (int i = 0; i < 4; i++) {
            String dir = null;
            ArrayList<Integer> src = null;
            switch (i) {
                case 0 -> {
                    dir = "first/";
                    src = A_firstIndex;
                }
                case 1 -> {
                    dir = "second/";
                    src = A_secondIndex;
                }
                case 2 -> {
                    dir = "third/";
                    src = A_thirdIndex;
                }
                case 3 -> {
                    dir = "forth/";
                    src = A_forthIndex;
                }
            }

            for (Integer integer : src) {
                BufferedImage bufferedImage = readImageFile(new File("graduate/A/Range/" + dir + integer + ".bmp"));
                int[] checkBits = getCheckBits(Objects.requireNonNull(bufferedImage));
                embedCheckBits(bufferedImage, checkBits, "graduate/A/Range/" + dir + integer);
            }
        }

        // 生成最后水印图像
        BufferedImage waterMarkImage = createWaterMarkImage(Width, Height);
        writeImageFile(waterMarkImage, "graduate/lw");

        long endTime = System.currentTimeMillis();

        long runTime = endTime - startTime;
        System.out.println("运行时间：" + runTime + "ms");

    }

    // 读取测试图像
    public static BufferedImage readImageFile(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //读取分形编码文件
    public static void readEncodeFile(String file_path) throws IOException {
        File encodeFile = new File(file_path);
        try {
            BufferedReader in = new BufferedReader(new FileReader(encodeFile));
            String str;
            while((str = in.readLine()) != null){
                encode_file.add(str);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 储存图像信息
    public static void writeImageFile(BufferedImage image, String name) {
        File outfile = new File(name + ".bmp");
        try {
            if (ImageIO.write(image, "bmp", outfile)) {
                System.out.println("图像写入成功！");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("图像写入失败！");
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

    // 生成最终水印图像
    public static BufferedImage createWaterMarkImage(int width, int height){
        BufferedImage res_image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = res_image.getRaster();
        ArrayList<Integer> A_firstIndex = getPicIndex("graduate/A/Range/first/");
        ArrayList<Integer> A_secondIndex = getPicIndex("graduate/A/Range/second/");
        ArrayList<Integer> A_thirdIndex = getPicIndex("graduate/A/Range/third/");
        ArrayList<Integer> A_forthIndex = getPicIndex("graduate/A/Range/forth/");

        int cnt = 0;
        for (int m = 0; m < 16; m++) {
            for (int n = 0; n < 16; n++) {
                BufferedImage src_image = readImageFile(new File("graduate/A/Range/first/" + A_firstIndex.get(cnt++) + ".bmp"));
                int[][] grayValue = getGrayValue(Objects.requireNonNull(src_image));
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        raster.setSample(128 + j + n * 8,i + m * 8,0,grayValue[j][i]);
                    }
                }
            }
        }

        cnt = 0;
        for (int m = 0; m < 16; m++) {
            for (int n = 0; n < 16; n++) {
                BufferedImage src_image = readImageFile(new File("graduate/A/Range/second/" + A_secondIndex.get(cnt++) + ".bmp"));
                int[][] grayValue = getGrayValue(Objects.requireNonNull(src_image));
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        raster.setSample(j + n * 8,i + m * 8,0,grayValue[j][i]);
                    }
                }
            }
        }

        cnt = 0;
        for (int m = 0; m < 16; m++) {
            for (int n = 0; n < 16; n++) {
                BufferedImage src_image = readImageFile(new File("graduate/A/Range/third/" + A_thirdIndex.get(cnt++) + ".bmp"));
                int[][] grayValue = getGrayValue(Objects.requireNonNull(src_image));
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        raster.setSample(j + n * 8,128 + i + m * 8,0,grayValue[j][i]);
                    }
                }
            }
        }

        cnt = 0;
        for (int m = 0; m < 16; m++) {
            for (int n = 0; n < 16; n++) {
                BufferedImage src_image = readImageFile(new File("graduate/A/Range/forth/" + A_forthIndex.get(cnt++) + ".bmp"));
                int[][] grayValue = getGrayValue(Objects.requireNonNull(src_image));
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        raster.setSample(128 + j + n * 8,128 + i + m * 8,0,grayValue[j][i]);
                    }
                }
            }
        }

        return res_image;
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
        ArrayList<Integer> picIndex = getPicIndex("graduate/A/Domain/1" + path);
        double minMSE = 99999.0;
        for (int k = 1; k < 9; k++) {
            for (int j:picIndex) {
                BufferedImage domainImage = readImageFile(new File("graduate/A/Domain/" + k + path + j + ".bmp"));
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
                        }

                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        list.sort((o1, o2) -> {
            if (o1 > o2) return 1;
            else if (o1 < o2) return -1;
            return 0;
        });
        return list;
    }

    // 选择仿射变换
    public static BufferedImage selectAffineTrans(BufferedImage image, int i){
        int width = image.getWidth();
        int height = image.getHeight();
        new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        return switch (i) {
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
    public static int getScalefactor(BufferedImage Range, BufferedImage Domain){
        double s;
        double totalR = 0, totalD = 0, totalD2 = 0, upfirst = 0;
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
        long round = Math.round(s);
        return (int) round;
    }

    // 计算灰度偏移量
    public static int getGrayscaleoffset(BufferedImage Range, BufferedImage Domain){
        double grayoffset;
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

        grayoffset = (first / N )- (second / N) * getScalefactor(Range,Domain);
        long round = Math.round(grayoffset);
        return (int) round;
    }

    // 计算误差
    public static float getError(BufferedImage Range, BufferedImage Domain){
        float e = 0f;
        int width = Range.getWidth();
        int height = Range.getHeight();
        int N = width * height;
        int[][] range = getGrayValue(Range);
        int[][] domain = getGrayValue(Domain);

        int s = getScalefactor(Range, Domain);
        int o = getGrayscaleoffset(Range, Domain);

        for(int i = 0; i < height; i ++){
            for(int j = 0; j < width; j++){
                e += ( range[i][j] - (s * domain[i][j] + o) ) * ( range[i][j] - (s * domain[i][j] + o) );
            }
        }

        e = e / N;
        return e;
    }

    // DCT变换中间计算
    public static float tempDct(int[][] gray, double u, double v){
        int height = gray.length;
        int width = gray[0].length;
        float res = 0f;

        for(int i = 0; i < height; i ++){
            for(int j = 0; j < width; j++){
                res += gray[i][j] * Math.cos((2*i+1) * Math.PI * u / (2*width)) * Math.cos((2*j+1) * Math.PI * v / (2*width));
            }
        }
        return res;
    }

    // 原始DCT变换计算
    public static double[][] getOriginalDCT(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        double [][]dct = new double[height][width];
        int[][] gray = getGrayValue(image);
        double sum;
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
            }
        }

        return dct;
    }

    // 进行DCT变换并标准化
    public static double[][] performDCT(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        double [][]dct = new double[height][width];
        int[][] gray = getGrayValue(image);
        float sum;
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
                BigDecimal temp = BigDecimal.valueOf(dct[u][v]);
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
        float sum;
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
                dct[u][v] = (int) Math.round(cu * cv * sum / table[u][v] / 2);
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
    public static void getA_block(BufferedImage image, int r, int step) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        // 划分Range块
        for (int i = 0; i < height / r; i++) {
            for (int j = 0; j < width / r; j++) {
                BufferedImage subImage = image.getSubimage(r * j, r * i, r, r);
                File outfile = null;
                if (j < width / r / 2 && i < height / r / 2)
                    outfile = new File("graduate/A/Range/second/" + aCount + ".bmp");
                if (j >= width / r / 2 && i < height / r / 2)
                    outfile = new File("graduate/A/Range/first/" + aCount + ".bmp");
                if (j < width / r / 2 && i >= height / r / 2)
                    outfile = new File("graduate/A/Range/third/" + aCount + ".bmp");
                if (j >= width / r / 2 && i >= height / r / 2)
                    outfile = new File("graduate/A/Range/forth/" + aCount + ".bmp");
                ImageIO.write(subImage, "bmp", Objects.requireNonNull(outfile));
                System.out.println("正在划分第" + aCount +"块A类Range块");
                aCount++;
            }
        }

        // 划分Domain块
        for (int i = 0; i < height; i = i + step) {
            for (int j = 0; j < width; j = j + step) {
                if((i <= height - r) && (j <= width - r)){
                    if (i == 124 || j == 124) continue;
                    BufferedImage subImage = image.getSubimage(j, i, r, r);
                    if (j <= width/2 - r && i <= height/2 - r){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/A/Domain/" + k + "/second/" + d_2_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_2_Cnt + "个A类Domain块！");
                        d_2_Cnt++;
                    }
                    else if (j <= width - r && i <= height/2 - r){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/A/Domain/" + k + "/first/" + d_1_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_1_Cnt + "个A类Domain块！");
                        d_1_Cnt++;
                    }
                    else if (j <= width/2 - r && i >= height/2){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/A/Domain/" + k + "/third/" + d_3_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_3_Cnt + "个A类Domain块！");
                        d_3_Cnt++;
                    }
                    else if (j >= width/2 && i >= height/2){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/A/Domain/" + k + "/forth/" + d_4_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_4_Cnt + "个A类Domain块！");
                        d_4_Cnt++;
                    }

                }
            }
        }
    }

    // 划分B类块
    public static void getB_block(BufferedImage image, int len, int r) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage splicingImage = image;

        // 生成新图像
        try {
            BufferedImage image1 = splicingImage.getSubimage(0, 0, width, len);
            BufferedImage image2 = splicingImage.getSubimage(0, len, width, height-len);
            splicingImage = SplicingImage(image1, image2, 1);
            File outfile = new File("graduate/B/newB.bmp");
            ImageIO.write(splicingImage, "bmp", outfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedImage new_Image = readImageFile(new File("graduate/B/newB.bmp"));

        // 划分子块
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = Objects.requireNonNull(new_Image).getSubimage(r * j, r * i, r, r);
                    File outfile = null;
                    if (j < width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/B/second/" + bCount + ".bmp");
                    if (j >= width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/B/first/" + bCount + ".bmp");
                    if (j < width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/B/third/" + bCount + ".bmp");
                    if (j >= width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/B/forth/" + bCount + ".bmp");
                    ImageIO.write(subImage, "bmp", Objects.requireNonNull(outfile));
                    System.out.println("正在划分第" + bCount +"块B类块");
                    bCount++;
                }
            }
            System.out.println("B类块处理完成!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 划分C类块
    public static void getC_block(BufferedImage image, int len, int r) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage splicingImage = image;

        // 生成新的图像
        try {
            BufferedImage image1 = splicingImage.getSubimage(0, 0, len, height);
            BufferedImage image2 = splicingImage.getSubimage(len, 0, width-len, height);
            splicingImage = SplicingImage(image1, image2, 2);
            File outfile = new File("graduate/C/newC.bmp");
            ImageIO.write(splicingImage, "bmp", outfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedImage new_Image = readImageFile(new File("graduate/C/newC.bmp"));

        // 划分子块
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = Objects.requireNonNull(new_Image).getSubimage(r * j, r * i, r, r);
                    File outfile = null;
                    if (j < width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/C/second/" + cCount + ".bmp");
                    if (j >= width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/C/first/" + cCount + ".bmp");
                    if (j < width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/C/third/" + cCount + ".bmp");
                    if (j >= width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/C/forth/" + cCount + ".bmp");
                    ImageIO.write(subImage, "bmp", Objects.requireNonNull(outfile));
                    System.out.println("正在划分第" + cCount +"块C类块");
                    cCount++;
                }
            }
            System.out.println("C类块处理完成!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 拼接图像
    public static BufferedImage SplicingImage(BufferedImage image1, BufferedImage image2, int option){
        BufferedImage res = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_GRAY);
        int[][] gray1 = getGrayValue(image1);
        int[][] gray2 = getGrayValue(image2);
        WritableRaster raster = res.getRaster();

        switch (option) {
            case 1 -> {
                for (int i = 0; i < gray2.length; i++) {
                    for (int j = 0; j < gray2[0].length; j++) {
                        raster.setSample(i, j, 0, gray2[i][j]);
                    }
                }
                for (int i = 0; i < gray1.length; i++) {
                    for (int j = 0; j < gray1[0].length; j++) {
                        raster.setSample(i, j + gray2[0].length, 0, gray1[i][j]);
                    }
                }
            }
            case 2 -> {
                for (int i = 0; i < gray2.length; i++) {
                    for (int j = 0; j < gray2[0].length; j++) {
                        raster.setSample(i, j, 0, gray2[i][j]);
                    }
                }
                for (int i = 0; i < gray1.length; i++) {
                    for (int j = 0; j < gray1[0].length; j++) {
                        raster.setSample(i + gray2.length, j, 0, gray1[i][j]);
                    }
                }
            }
            default -> System.out.println("输入有误！");
        }
        return res;
    }

    // 分形编码信息嵌入
    public static void embedFractalCode(ArrayList<Integer> src_list, ArrayList<Integer> dst_list, int type) {
        String dst_dir = null;
        int deviation = 0;
        switch (type) {
            case 1 -> dst_dir = "third/";
            case 2 -> {
                deviation = 256;
                dst_dir = "forth/";
            }
            case 3 -> {
                deviation = 512;
                dst_dir = "first/";
            }
            case 4 -> {
                deviation = 768;
                dst_dir = "second/";
            }
        }

        for (int i = 1; i <= src_list.size(); i++) {
            String []parse = (encode_file.elementAt(i + deviation)).split("\t");
            int src_no = Integer.parseInt(parse[0]);
            int num = Integer.parseInt(parse[1]);
            int k = Integer.parseInt(parse[2]);
            int s = Integer.parseInt(parse[3]);
            int offset = Integer.parseInt(parse[4]);

            Integer integer = a_p_map.get(src_list.indexOf(src_no));
            Integer dst_integer = dst_list.get(integer);
            BufferedImage dst_image = readImageFile(new File("graduate/A/Range/" + dst_dir + dst_integer + ".bmp"));

            int width = Objects.requireNonNull(dst_image).getWidth();
            int[][] grayValue = getGrayValue(dst_image);
            WritableRaster raster = dst_image.getRaster();

            String fractalCode = setFractalCode(num, k - 1, s, offset / 2);

            int cnt = 0;
            int val, low, high;

            for (int j = 0; j < width; j++) {
                high = Integer.parseInt(String.valueOf(fractalCode.charAt(cnt)));
                low = Integer.parseInt(String.valueOf(fractalCode.charAt(cnt + 1)));
                val = grayValue[j][0] + 2 * high + low;
                raster.setSample(j, 0, 0, val);
                cnt = cnt + 2;
            }

            for (int j = 0; j < 6; j++) {
                high = Integer.parseInt(String.valueOf(fractalCode.charAt(cnt)));
                low = Integer.parseInt(String.valueOf(fractalCode.charAt(cnt + 1)));
                val = grayValue[j][1] + 2 * high + low;
                raster.setSample(j, 1, 0, val);
                cnt = cnt + 2;
            }
            writeImageFile(dst_image,"graduate/A/Range/" + dst_dir + dst_integer);
        }

    }

    // 读取随机置换矩阵
    public static HashMap<Integer,Integer> readPMatrix(String file_path) throws IOException {
        File matrixFile = new File(file_path);
        HashMap<Integer,Integer> matrix = new HashMap<>();
        int cnt = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(matrixFile));
            String str;
            while((str = in.readLine()) != null){
                matrix.put(cnt, Integer.parseInt(str));
                cnt++;
            }
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    // 分形码构成
    public static String setFractalCode(int num, int k, int s, int offset){
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(num));
        while (sb.length() < 10) sb.insert(0,"0");

        sb.append(Integer.toBinaryString(k));
        while (sb.length() < 13) sb.insert(10,"0");

        String s_prefix = s < 0 ? "1" : "0";
        sb.append(Integer.toBinaryString(Math.abs(s)));
        while (sb.length() < 17) sb.insert(13,"0");
        sb.insert(13,s_prefix);

        String off_prefix = offset < 0 ? "1" : "0";
        if (Math.abs(offset) >= 512) sb.append(Integer.toBinaryString(511));
        else sb.append(Integer.toBinaryString(Math.abs(offset)));
        while (sb.length() < 27) sb.insert(18,"0");
        sb.insert(18,off_prefix);

        return sb.toString();
    }

    // DCT系数嵌入
    public static void embedDctCoefficient(BufferedImage src_image, BufferedImage dst_image, int type, String name){
        int width = src_image.getWidth();
        int[][] grayValue = getGrayValue(dst_image);
        int[][] dct_quantize = getDCT_Quantize(src_image);
        WritableRaster raster = dst_image.getRaster();

        String s = completeBinary(dct_quantize[0][0]) +
                completeBinary(dct_quantize[1][0]) +
                completeBinary(dct_quantize[0][1]) +
                completeBinary(dct_quantize[0][2]) +
                completeBinary(dct_quantize[1][1]) +
                completeBinary(dct_quantize[2][0]);
//        System.out.println(s);
        int cnt = 0;
        int val, low, high;

        switch (type) {
            case 1 -> {
                for (int i = 6; i < 8; i++) {
                    high = Integer.parseInt(String.valueOf(s.charAt(cnt)));
                    low = Integer.parseInt(String.valueOf(s.charAt(cnt + 1)));
                    val = grayValue[i][1] + 2 * high + low;
                    raster.setSample(i, 1, 0, val);
                    cnt = cnt + 2;
                }
                for (int j = 2; j < 4; j++) {
                    for (int i = 0; i < width; i++) {
                        high = Integer.parseInt(String.valueOf(s.charAt(cnt)));
                        low = Integer.parseInt(String.valueOf(s.charAt(cnt + 1)));
                        val = grayValue[i][j] + 2 * high + low;
                        raster.setSample(i, j, 0, val);
                        cnt = cnt + 2;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    high = Integer.parseInt(String.valueOf(s.charAt(cnt)));
                    low = Integer.parseInt(String.valueOf(s.charAt(cnt + 1)));
                    val = grayValue[i][4] + 2 * high + low;
                    raster.setSample(i, 4, 0, val);
                    cnt = cnt + 2;
                }
            }
            case 2 -> {
                for (int i = 3; i < 8; i++) {
                    high = Integer.parseInt(String.valueOf(s.charAt(cnt)));
                    low = Integer.parseInt(String.valueOf(s.charAt(cnt + 1)));
                    val = grayValue[i][4] + 2 * high + low;
                    raster.setSample(i, 4, 0, val);
                    cnt = cnt + 2;
                }
                for (int j = 5; j < 7; j++) {
                    for (int i = 0; i < width; i++) {
                        high = Integer.parseInt(String.valueOf(s.charAt(cnt)));
                        low = Integer.parseInt(String.valueOf(s.charAt(cnt + 1)));
                        val = grayValue[i][j] + 2 * high + low;
                        raster.setSample(i, j, 0, val);
                        cnt = cnt + 2;
                    }
                }
            }
        }
        writeImageFile(dst_image,name);
    }

    // 补全二进制
    public static String completeBinary(int m){
        String prefix = "0";
        if (m < 0) prefix = "1";
        int abs = Math.abs(m);
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(abs));

        while (sb.length() != 6) sb.insert(0,"0");
        sb.insert(0,prefix);
        return sb.toString();
    }

    // 读取随机矩阵
    public static Vector<String> readRandomMatrix(String file_path) throws IOException {
        File matrixFile = new File(file_path);
        Vector<String> matrix = new Vector<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(matrixFile));
            String str;
            while((str = in.readLine()) != null){
                matrix.add(str);
            }
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    // 计算校验位
    public static int[] getCheckBits(BufferedImage image) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();
        double[][] dct = getOriginalDCT(image);
        int[] res = new int[16];

        Vector<String> matrixVector = readRandomMatrix("./graduate/matrix.txt");
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < matrixVector.size(); i++) {
            String s = matrixVector.elementAt(i);
            if (s.equals("")) continue;
            String[] split = s.split("\t");
            for (String m:split) {
                list.add(Integer.parseInt(m));
            }

        }

        int count = 0;
        for (int m = 0; m < 16; m++) {
            int temp = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    temp += dct[i][j] * list.get(count);
                    count++;
                }
            }
            res[m] = temp > 0 ? 1 : 0;
        }
        return res;
    }

    // 校验位嵌入
    public static void embedCheckBits(BufferedImage image, int[] bits, String name){
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] grayValue = getGrayValue(image);
        int low, high, val;
        WritableRaster raster = image.getRaster();

        int cnt = 0;
        for (int i = 0; i < width; i++) {
            high = bits[cnt];
            low = bits[cnt+1];
            val = high * 2 + low + grayValue[i][height-1];
            raster.setSample(i,height-1,0, val);
            cnt = cnt + 2;
        }
        writeImageFile(image, name);
    }

}
