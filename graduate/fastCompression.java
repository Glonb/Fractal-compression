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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
/*
 * 快速分形压缩算法实现
 */

public class fastCompression {

    static int rangeCount = 1;
    static int domainCount = 1;
    public static void main(String[] args) throws IOException {

        int rangeR = 8;
        int domainR = 2 * rangeR;
        long startTime = System.currentTimeMillis();
        //读取测试图像
        File file = new File("./graduate/lena_256.bmp");
        BufferedImage test_image = readImageFile(file);

        //创建分形编码文件
        File out_file = new File("./graduate/encode.txt");
        BufferedWriter out_txt = new BufferedWriter(new FileWriter(out_file));
        out_txt.write(Objects.requireNonNull(test_image).getHeight() + "\t" + test_image.getWidth() + "\t" + rangeR + "\t" + domainR + "\t" + rangeR + "\n");

        // 划分Range块
         cutRangeBlock(test_image, rangeR);

        // 划分Domain块并压缩
        cutDomainBlock(test_image, domainR, rangeR);

        // 对Domain块处理获得Tn(Dj);
        getTnDomain("./graduate/Domain");

        // 构建索引树
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        for (int i = 0; i <= 100; i++) {
            ArrayList<Integer> ints = new ArrayList<>();
            ints.add(-1);
            map.put(i,ints);
        }

        for(int j = 1; j < domainCount; j++) {
            File domainFile = new File("./graduate/TnDomain/" + 1 + "/" + j + ".bmp");
            BufferedImage domainImage = readImageFile(domainFile);
            double[][] dct = performDCT(Objects.requireNonNull(domainImage));

            int val = (int) (100 * dct[1][0]);
            ArrayList<Integer> integers = map.get(val);
            integers.add(j);
            map.replace(val, integers);
        }

        HashMap<Integer, ArrayList<Integer>> map1 = new HashMap<>();
        for (int i = 0; i <= 100; i++) {
            ArrayList<Integer> ints = new ArrayList<>();
            ints.add(-1);
            map1.put(i,ints);
        }

        for(int j = 1; j < domainCount; j++){
            File domainFile = new File("./graduate/TnDomain/" + 7 + "/" + j + ".bmp");
            BufferedImage domainImage = readImageFile(domainFile);
            double[][] dct = performDCT(Objects.requireNonNull(domainImage));

            int val = (int) (100 * dct[1][0]);
            ArrayList<Integer> integers = map1.get(val);
            integers.add(j);
            map1.replace(val, integers);

        }

        // 寻找最佳匹配
        for(int i = 1; i < rangeCount; i++) {
            File rangeFile = new File("./graduate/Range/" + i + ".bmp");
            BufferedImage rangeImage = readImageFile(rangeFile);
            double[][] dct = performDCT(Objects.requireNonNull(rangeImage));
            int val = (int) (100 * dct[1][0]);

            ArrayList<Integer> integers = map.get(val);
            Iterator<Integer> iterator = integers.iterator();
            ArrayList<Integer> integers1 = map1.get(val);
            Iterator<Integer> iterator1 = integers1.iterator();

            int targetDomain = 1, targetTransform = 1;
            double minMSE = 99999.0;

            while (iterator.hasNext()){
                Integer next = iterator.next();
                if (next == -1) continue;
                for (int j = 1; j < 5; j++) {
                    BufferedImage domainImage = readImageFile(new File("./graduate/TnDomain/" + j + "/" + next + ".bmp"));
                    double MSE = getError(rangeImage, domainImage);
                    if(minMSE > MSE){
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
                    BufferedImage domainImage = readImageFile(new File("./graduate/TnDomain/" + j + "/" + next + ".bmp"));
                    double MSE = getError(rangeImage, domainImage);
                    if(minMSE > MSE){
                        minMSE = MSE;
                        targetDomain = next;
                        targetTransform = j;
                    }
                }
            }

            File targetDomainFile = new File("./graduate/TnDomain/" + targetTransform + "/" + targetDomain + ".bmp");
            BufferedImage targetDomainImage = readImageFile(targetDomainFile);
            int scalefactor = getScalefactor(rangeImage, targetDomainImage);
            int offset = getGrayscaleoffset(rangeImage, targetDomainImage);
            String outcome = "i = " + i + ", j = " + targetDomain + ", k = " + targetTransform  + ", s = " + scalefactor + ", offset = " + offset;
            System.out.println(outcome);
            out_txt.write(i + "\t" + targetDomain + "\t" + targetTransform + "\t" + scalefactor + "\t" + offset + "\n");
            out_txt.flush();

        }

        out_txt.close();
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

    // 获取图像每个像素灰度值
    public static int[][] getGrayValue(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] array = new int[height][width];
        WritableRaster raster = image.getRaster();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                array[i][j] = raster.getSample(i, j, 0);
            }
        }
        return array;
    }

    // 划分Range块
    public static void cutRangeBlock(BufferedImage image, int r) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 分割图像
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = image.getSubimage(r * j, r * i, r, r);
                    System.out.println("正在划分第" + rangeCount + "个Range块！");
                    File outfile = new File("./graduate/Range/" + rangeCount + ".bmp");
                    ImageIO.write(subImage, "bmp", outfile);
                    rangeCount++;
                }
            }
            System.out.println("Range块处理完成!");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Range块处理失败!");
    }

    // 划分Domain块并压缩
    public static void cutDomainBlock(BufferedImage image, int r, int step) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 分割图像
        try {
            for (int i = 0; i < height; i = i + step) {
                for (int j = 0; j < width; j = j + step) {
                    if((i <= height - r) && (j <= width - r)){
                        BufferedImage subImage = image.getSubimage(j, i, r, r);
                        System.out.print("正在划分第" + domainCount + "个Domain块！");

                        BufferedImage compressedImage = compressDomain(subImage);
                        File outfile = new File("./graduate/Domain/" + domainCount + ".bmp");
                        if (ImageIO.write(compressedImage, "bmp", outfile)) {
                            System.out.println("正在压缩第" + domainCount + "个Domain块！");
                        }
                        domainCount++;
                    }
                }
            }
            System.out.println("Domain块处理完成!");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Domain块处理失败!");
    }

    // 处理压缩后的Domain块 ----仿射变换
    public static void getTnDomain(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("文件夹是空的!");
            } else {
                for (File insideFile : files) {
                    if (insideFile.isDirectory()) {
                        getTnDomain(insideFile.getAbsolutePath());
                    } else {
                        if (!insideFile.isHidden()) {
                            BufferedImage image = readImageFile(insideFile);

                            for (int k = 1; k < 9; k++) {
                                BufferedImage modifyImage = selectAffineTrans(Objects.requireNonNull(image), k);
                                File outfile = new File("./graduate/TnDomain/" + k + "/" + insideFile.getName());
                                try {
                                    ImageIO.write(modifyImage, "bmp", outfile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
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

    // 几何变换  ----压缩Domain块
    public static BufferedImage compressDomain(BufferedImage image) {
        int width = image.getWidth() / 2;
        int height = image.getHeight() / 2;
        int[][] array = getGrayValue(image);
        BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = compressedImage.getRaster();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double Sample = (array[2 * i][2 * j] + array[2 * i + 1][2 * j] + array[2 * i][2 * j + 1] + array[2 * i + 1][2 * j + 1]) / 4.0;
                raster.setSample(i, j, 0, Sample);
            }
        }
        return compressedImage;
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
        int width;
        width = image.getWidth();
        int height;
        height = image.getHeight();
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
        float s;
        float totalR = 0, totalD = 0, totalD2 = 0;
        float upfirst = 0;
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
        return Math.round(s);
    }

    // 计算灰度偏移量
    public static int getGrayscaleoffset(BufferedImage Range, BufferedImage Domain){
        float grayoffset;
        int width = Range.getWidth();
        int height = Range.getHeight();
        int N = width * height;
        float first = 0, second = 0;

        int[][] range = getGrayValue(Range);
        int[][] domain = getGrayValue(Domain);

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                first += range[i][j];
                second += domain[i][j];
            }
        }

        grayoffset = (first / N )- (second / N) * getScalefactor(Range,Domain);
        return Math.round(grayoffset);
    }

    // 计算误差
    public static double getError(BufferedImage Range, BufferedImage Domain){
        double e = 0;
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
    public static double tempDct(int[][] gray, double u, double v){
        int height = gray.length;
        int width = gray[0].length;
        double res = 0;

        for(int i = 0; i < height; i ++){
            for(int j = 0; j < width; j++){
                res += (gray[i][j] - 128) * Math.cos((2*i+1) * Math.PI * u / (2*width)) * Math.cos((2*j+1) * Math.PI * v / (2*width));
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
        double sum, cu, cv;
        double dct2d = 0;

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
}
