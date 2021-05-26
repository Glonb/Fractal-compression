package graduate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

/**
 * @Description: 水印检测
 * @Author wanyao
 * @Date 2021/4/23 10:43
 */

public class tamperDetection {
    static int aCount = 1;
    static int d_1_Cnt = 1;
    static int d_2_Cnt = 1;
    static int d_3_Cnt = 1;
    static int d_4_Cnt = 1;
    static int bCount = 1;
    static int cCount = 1;
    static HashMap<Integer,Integer> tamp_1 = new HashMap<>();
    static HashMap<Integer,Integer> tamp_2 = new HashMap<>();
    static HashMap<Integer,Integer> tamp_3 = new HashMap<>();
    static HashMap<Integer,Integer> tamp_4 = new HashMap<>();
    static HashMap<Integer,Integer> domain_1  = new HashMap<>();
    static HashMap<Integer,Integer> domain_2  = new HashMap<>();
    static HashMap<Integer,Integer> domain_3  = new HashMap<>();
    static HashMap<Integer,Integer> domain_4  = new HashMap<>();
    static HashMap<Integer, Integer> t_l_k = new HashMap<>();
    static ArrayList<Integer> A_firstIndex = getPicIndex("graduate/tamperDetection/A/Range/first/");
    static ArrayList<Integer> A_secondIndex = getPicIndex("graduate/tamperDetection/A/Range/second/");
    static ArrayList<Integer> A_thirdIndex = getPicIndex("graduate/tamperDetection/A/Range/third/");
    static ArrayList<Integer> A_forthIndex = getPicIndex("graduate/tamperDetection/A/Range/forth/");
    static ArrayList<Integer> B_firstIndex = getPicIndex("graduate/tamperDetection/B/first/");
    static ArrayList<Integer> B_secondIndex = getPicIndex("graduate/tamperDetection/B/second/");
    static ArrayList<Integer> B_thirdIndex = getPicIndex("graduate/tamperDetection/B/third/");
    static ArrayList<Integer> B_forthIndex = getPicIndex("graduate/tamperDetection/B/forth/");
    static ArrayList<Integer> C_firstIndex = getPicIndex("graduate/tamperDetection/C/first/");
    static ArrayList<Integer> C_secondIndex = getPicIndex("graduate/tamperDetection/C/second/");
    static ArrayList<Integer> C_thirdIndex = getPicIndex("graduate/tamperDetection/C/third/");
    static ArrayList<Integer> C_forthIndex = getPicIndex("graduate/tamperDetection/C/forth/");
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
        int gap = 8, step  =4;
        String dir = null;
        String opp_dir = null;
        ArrayList<Integer> src = null;
        ArrayList<Integer> dst = null;
        ArrayList<Integer> list = null;
        HashMap<Integer,Integer> map = null;

        // 读取待检测图像
        BufferedImage test_image = readImageFile(new File("tamper.bmp"));
        int Height = Objects.requireNonNull(test_image).getHeight();
        int Width = test_image.getWidth();

        // 划分三类块
        getA_block(test_image, gap, step);
        getB_block(test_image,4, gap);
        getC_block(test_image,4, gap);

        // 记录四级校验结果
        for (int i = 1; i <= 1024; i++){
            tamp_1.put(i, -1);
            tamp_2.put(i, -1);
            tamp_3.put(i, -1);
            tamp_4.put(i, -1);
        }

        // 记录Domain块的校验结果
        for (int i = 1; i <= 961; i++) {
            domain_1.put(i, -1);
            domain_2.put(i, -1);
            domain_3.put(i, -1);
            domain_4.put(i, -1);
        }

        // 记录最终校验结果
        for (int i = 1; i <= 4096; i++) {
            t_l_k.put(i, -1);
        }

        // 比较校验位是否正确
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0 -> {
                    dir = "first/";
                    list = A_firstIndex;
                }
                case 1 -> {
                    dir = "second/";
                    list = A_secondIndex;
                }
                case 2 -> {
                    dir = "third/";
                    list = A_thirdIndex;
                }
                case 3 -> {
                    dir = "forth/";
                    list = A_forthIndex;
                }
            }

            for (Integer integer : list) {
                BufferedImage bufferedImage = readImageFile(new File("graduate/tamperDetection/A/Range/" + dir + integer + ".bmp"));
                boolean b = compareCheckBits(bufferedImage);
                if (b) {
                    tamp_1.replace(integer, 1);
                }
            }
        }

        // 检测Domain块是否正确
        checkDomainBlock(Height, Width);

        // 比较分形压缩编码系数是否正确
        for (int i = 1; i < 5; i++) {
            switch (i) {
                case 1 -> {
                    dir = "first/";
                    opp_dir = "third/";
                    src = A_firstIndex;
                    dst = A_thirdIndex;
                    map = domain_3;
                }
                case 2 -> {
                    dir = "second/";
                    opp_dir = "forth/";
                    src = A_secondIndex;
                    dst = A_forthIndex;
                    map = domain_4;
                }
                case 3 -> {
                    dir = "third/";
                    opp_dir = "first/";
                    src = A_thirdIndex;
                    dst = A_firstIndex;
                    map = domain_1;
                }
                case 4 -> {
                    dir = "forth/";
                    opp_dir = "second/";
                    src = A_forthIndex;
                    dst = A_secondIndex;
                    map = domain_2;
                }
            }

            for (int m = 0; m < src.size(); m++) {
                Integer src_integer = src.get(m);
                Integer dst_integer = dst.get(a_p_map.get(m));
                if (tamp_1.get(dst_integer) == -1){
                    tamp_2.replace(src_integer, 0);
                    continue;
                }
                BufferedImage src_image = readImageFile(new File("graduate/tamperDetection/A/Range/" + dir + src_integer + ".bmp"));
                BufferedImage dst_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ opp_dir + dst_integer + ".bmp"));
                int[] ints = extractFractalCode(Objects.requireNonNull(dst_image));
                if (map.get(ints[0]) == -1){
                    tamp_2.replace(src_integer, 0);
                }else{
                    boolean b = compareFractalCode(src_image, dst_image, i);
                    if (b) tamp_2.replace(src_integer, 1);
                    else {
                        tamp_2.replace(src_integer, -1);
                    }
                }
            }
        }

        // 比较DCT系数是否正确
        for (int i = 0; i < 4; i++) {
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

            for (int m = 0; m < src.size(); m++) {
                Integer src_integer = src.get(m);
                Integer dst_integer = dst.get(b_p_map.get(m));
                if (tamp_1.get(dst_integer) == -1){
                    tamp_3.replace(src_integer, 0);
                    continue;
                }
                BufferedImage src_image = readImageFile(new File("graduate/tamperDetection/B/" + dir + src_integer + ".bmp"));
                BufferedImage dst_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ opp_dir + dst_integer + ".bmp"));
                boolean b = compareDctCoefficient(src_image, dst_image, 1);
                if (b) tamp_3.replace(src_integer, 1);
            }
        }

        for (int i = 0; i < 4; i++) {
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

            for (int m = 0; m < src.size(); m++) {
                Integer src_integer = src.get(m);
                Integer dst_integer = dst.get(c_p_map.get(m));
                if (tamp_1.get(dst_integer) == -1){
                    tamp_4.replace(src_integer, 0);
                    continue;
                }
                BufferedImage src_image = readImageFile(new File("graduate/tamperDetection/C/" + dir + src_integer + ".bmp"));
                BufferedImage dst_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ opp_dir + dst_integer + ".bmp"));
                boolean b = compareDctCoefficient(src_image, dst_image, 2);
                if (b) tamp_4.replace(src_integer, 1);
            }
        }

        // 最终检测结果
        int cnt = 1, chb_v, a_val, b_val, c_val;
        BufferedImage ck = new BufferedImage(Width, Height, 10);
        BufferedImage a = new BufferedImage(Width, Height, 10);
        BufferedImage b = new BufferedImage(Width, Height, 10);
        BufferedImage c = new BufferedImage(Width, Height, 10);
        for (int i = 0; i < Height / gap; i++) {
            for (int j = 0; j < Width / gap; j++) {
                chb_v = 0; a_val = 0; b_val = 0; c_val = 0;
                BufferedImage che_bit = Objects.requireNonNull(ck).getSubimage(gap * j, gap * i, gap, gap);
                BufferedImage fractal = Objects.requireNonNull(a).getSubimage(gap * j, gap * i, gap, gap);
                BufferedImage subImage = Objects.requireNonNull(b).getSubimage(gap * j, gap * i, gap, gap);
                BufferedImage subImage1 = Objects.requireNonNull(c).getSubimage(gap * j, gap * i, gap, gap);
                WritableRaster cheBitRaster = che_bit.getRaster();
                WritableRaster f_raster = fractal.getRaster();
                WritableRaster raster = subImage.getRaster();
                WritableRaster raster1 = subImage1.getRaster();
                if (tamp_1.get(cnt) == -1) chb_v = 255;
                if (tamp_2.get(cnt) == -1) a_val = 255;
                else if (tamp_2.get(cnt) == 0) a_val = 128;
                if (tamp_3.get(cnt) == -1) b_val = 255;
                else if (tamp_3.get(cnt) == 0) b_val = 128;
                if (tamp_4.get(cnt) == -1) c_val = 255;
                else if (tamp_4.get(cnt) == 0) c_val = 128;
                for (int k = 0; k < gap; k++) {
                    for (int l = 0; l < gap; l++) {
                        cheBitRaster.setSample(k,l,0, chb_v);
                        f_raster.setSample(k,l,0,a_val);
                        raster.setSample(k,l,0,b_val);
                        raster1.setSample(k,l,0,c_val);
                    }
                }
                cnt++;
            }
        }
        BufferedImage image1 = b.getSubimage(0, 0, 252, Height);
        BufferedImage image2 = b.getSubimage(252,0, Width-252, Height);
        BufferedImage b_image = SplicingImage(image1, image2, 2);
        BufferedImage image3 = c.getSubimage(0, 0, Width, 252);
        BufferedImage image4 = c.getSubimage(0,252, Width, Height-252);
        BufferedImage c_image = SplicingImage(image3, image4, 1);
        writeImageFile(ck, "MK");
        writeImageFile(a, "Fractal");
        writeImageFile(b_image, "B_DCT");
        writeImageFile(c_image, "C_DCT");

        // 最终检测结果并生成检测图像
        get_T_L_K(ck, a, b_image, c_image);

        // 恢复A类块
//        for (int i = 1; i < 5; i++) {
//            switch (i) {
//                case 1 -> {
//                    dir = "first/";
//                    opp_dir = "/third/";
//                    src = A_firstIndex;
//                    dst = A_thirdIndex;
//                    map = domain_3;
//                }
//                case 2 -> {
//                    dir = "second/";
//                    opp_dir = "/forth/";
//                    src = A_secondIndex;
//                    dst = A_forthIndex;
//                    map = domain_4;
//                }
//                case 3 -> {
//                    dir = "third/";
//                    opp_dir = "/first/";
//                    src = A_thirdIndex;
//                    dst = A_firstIndex;
//                    map = domain_1;
//                }
//                case 4 -> {
//                    dir = "forth/";
//                    opp_dir = "/second/";
//                    src = A_forthIndex;
//                    dst = A_secondIndex;
//                    map = domain_2;
//                }
//            }
//
//            for (int m = 0; m < src.size(); m++) {
//                Integer src_integer = src.get(m);
//                Integer map_integer = dst.get(a_p_map.get(m));
//                if ((tamp_2.get(src_integer) == -1) && (tamp_1.get(map_integer) == 1)){
//                    BufferedImage map_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ opp_dir + map_integer + ".bmp"));
//                    int[] ints = extractFractalCode(Objects.requireNonNull(map_image));
//                    if (map.get(ints[0]) == 1){
//                        BufferedImage src_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ dir + src_integer + ".bmp"));
//                        BufferedImage domain_image = readImageFile(new File("graduate/tamperDetection/A/Domain/"+ ints[1] + opp_dir + ints[0] + ".bmp"));
//                        BufferedImage dst_image = modifyGrayValue(Objects.requireNonNull(domain_image), ints[2], ints[3]);
//                        replaceDamagedBlock(Objects.requireNonNull(src_image), dst_image, "graduate/Recover/A/"+ dir + src_integer);
//                        tamp_2.replace(src_integer, 2);
//                    }
//                }
//            }
//        }
//        BufferedImage Image_1 = compositeImage(Width, Height, 1);
//        writeImageFile(Image_1, "I_1");
//
//        // 恢复B类块
//        for (int i = 1; i < 5; i++) {
//            switch (i) {
//                case 1 -> {
//                    dir = "first/";
//                    opp_dir = "/second/";
//                    src = B_firstIndex;
//                    dst = A_secondIndex;
//                }
//                case 2 -> {
//                    dir = "second/";
//                    opp_dir = "/third/";
//                    src = B_secondIndex;
//                    dst = A_thirdIndex;
//                }
//                case 3 -> {
//                    dir = "third/";
//                    opp_dir = "/forth/";
//                    src = B_thirdIndex;
//                    dst = A_forthIndex;
//                }
//                case 4 -> {
//                    dir = "forth/";
//                    opp_dir = "/first/";
//                    src = B_forthIndex;
//                    dst = A_firstIndex;
//                }
//            }
//
//            for (int m = 0; m < src.size(); m++) {
//                Integer src_integer = src.get(m);
//                Integer map_integer = dst.get(b_p_map.get(m));
//                if ((tamp_3.get(src_integer) == -1) && (tamp_1.get(map_integer) == 1)){
//                    BufferedImage map_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ opp_dir + map_integer + ".bmp"));
//                    BufferedImage i_dct_img = getI_DCT_Quantize(Objects.requireNonNull(map_image), 1);
//                    writeImageFile(i_dct_img, "graduate/Recover/B/"+ dir + src_integer);
//                    tamp_3.replace(src_integer, 2);
//                }
//            }
//        }
//        BufferedImage img = compositeImage(Width, Height, 2);
//        BufferedImage sub_img = img.getSubimage(0, 0, Width, Height - 4);
//        BufferedImage sub_img1 = img.getSubimage(0, Height - 4, Width, 4);
//        BufferedImage Image_2 = SplicingImage(sub_img, sub_img1, 1);
//        writeImageFile(Image_2, "I_2");
//
//        // 恢复C类块
//        for (int i = 1; i < 5; i++) {
//            switch (i) {
//                case 1 -> {
//                    dir = "first/";
//                    opp_dir = "/forth/";
//                    src = C_firstIndex;
//                    dst = A_forthIndex;
//                }
//                case 2 -> {
//                    dir = "second/";
//                    opp_dir = "/first/";
//                    src = C_secondIndex;
//                    dst = A_firstIndex;
//                }
//                case 3 -> {
//                    dir = "third/";
//                    opp_dir = "/second/";
//                    src = C_thirdIndex;
//                    dst = A_secondIndex;
//                }
//                case 4 -> {
//                    dir = "forth/";
//                    opp_dir = "/third/";
//                    src = C_forthIndex;
//                    dst = A_thirdIndex;
//                }
//            }
//
//            for (int m = 0; m < src.size(); m++) {
//                Integer src_integer = src.get(m);
//                Integer map_integer = dst.get(c_p_map.get(m));
//                if ((tamp_4.get(src_integer) == -1) && (tamp_1.get(map_integer) == 1)){
//                    BufferedImage map_image = readImageFile(new File("graduate/tamperDetection/A/Range/"+ opp_dir + map_integer + ".bmp"));
//                    BufferedImage i_dct_img = getI_DCT_Quantize(Objects.requireNonNull(map_image), 2);
//                    writeImageFile(i_dct_img, "graduate/Recover/C/"+ dir + src_integer);
//                    tamp_4.replace(src_integer, 2);
//                }
//            }
//        }
//        BufferedImage img1 = compositeImage(Width, Height, 3);
//        BufferedImage sub_img2 = img1.getSubimage(0, 0, Width-4, Height);
//        BufferedImage sub_img3 = img1.getSubimage(Width - 4, 0, 4, Height);
//        BufferedImage Image_3 = SplicingImage(sub_img2, sub_img3, 2);
//        writeImageFile(Image_3, "I_3");

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

    //修改图像块的灰度值
    public static BufferedImage modifyGrayValue(BufferedImage image, int scalefactor, int offset) {
        double value;
        int height = image.getHeight();
        int width = image.getWidth();
        int [][] gray = getGrayValue(image);

        BufferedImage modifiedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = modifiedImage.getRaster();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                value = scalefactor * gray[i][j] + offset;
                raster.setSample(i, j, 0, value);
            }
        }

        return modifiedImage;
    }

    // 替换受损图像块
    public static void replaceDamagedBlock(BufferedImage src_image, BufferedImage dst_image, String name){
        int height = src_image.getHeight();
        int width = src_image.getWidth();
        int[][] grayValue = getGrayValue(dst_image);

        WritableRaster raster = src_image.getRaster();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                raster.setSample(i,j,0,grayValue[i][j]);
            }
        }
        writeImageFile(src_image, name);
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

    // 记录Domain块校验结果
    public static void checkDomainBlock(int Height, int Width){
        BufferedImage res = new BufferedImage(Width, Height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = res.getRaster();
        BufferedImage bufferedImage;

        for (int t = 0; t < 4; t++) {
            int cnt = 0;
            int x = 0, y = 0;
            ArrayList<Integer> list = null;
            switch (t) {
                case 0 -> {
                    list = A_firstIndex;
                    x = 128;
                    y = 0;
                }
                case 1 -> {
                    list = A_secondIndex;
                    x = 0;
                    y = 0;
                }
                case 2 -> {
                    list = A_thirdIndex;
                    x = 0;
                    y = 128;
                }
                case 3 -> {
                    list = A_forthIndex;
                    x = 128;
                    y = 128;
                }
            }

            for (int m = 0; m < 16; m++) {
                for (int n = 0; n < 16; n++) {
                    Integer integer = list.get(cnt);
                    if (tamp_1.get(integer) == -1) bufferedImage = readImageFile(new File("black.bmp"));
                    else bufferedImage = readImageFile(new File("white.bmp"));
                    int[][] grayValue = getGrayValue(Objects.requireNonNull(bufferedImage));
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            raster.setSample(x + j + n * 8,y + i + m * 8,0,grayValue[j][i]);
                        }
                    }
                    cnt++;
                }
            }
        }
        writeImageFile(res,"domain_res");
        BufferedImage store = readImageFile(new File("domain_res.bmp"));

        int r = 8, cnt_1 = 1, cnt_2 = 1, cnt_3 = 1, cnt_4 = 1, sum;
        for (int i = 0; i < Height; i = i + 4) {
            for (int j = 0; j < Width; j = j + 4) {
                if((i <= Height - r) && (j <= Width - r)){
                    if (i == 124 || j == 124) continue;
                    BufferedImage subImage = Objects.requireNonNull(store).getSubimage(j, i, r, r);
                    sum = 0;
                    int[][] grayValue = getGrayValue(subImage);
                    for (int k = 0; k < r; k++) {
                        for (int l = 0; l < r; l++) {
                            sum += grayValue[k][l];
                        }
                    }
                    if (j <= Width/2 - r && i <= Height/2 - r){
                        if (sum == 16320) domain_2.replace(cnt_2, 1);
                        cnt_2++;
                    }
                    else if (j <= Width - r && i <= Height/2 - r){
                        if (sum == 16320) domain_1.replace(cnt_1, 1);
                        cnt_1++;
                    }
                    else if (j <= Width/2 - r && i >= Height/2){
                        if (sum == 16320) domain_3.replace(cnt_3, 1);
                        cnt_3++;
                    }
                    else if (j >= Width/2 && i >= Height/2){
                        if (sum == 16320) domain_4.replace(cnt_4, 1);
                        cnt_4++;
                    }

                }
            }
        }

    }

    // 记录最终检测结果
    public static void get_T_L_K(BufferedImage mk, BufferedImage fractal, BufferedImage b, BufferedImage c){
        int sum, mk_tamp, a_tamp, b_tamp, c_tamp, val, r = 4, cnt = 1;
        int w1 = 1, w2 = 2, w3 = 1, w4 = 1;

        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                sum = 0;mk_tamp = 0;a_tamp = 0; b_tamp = 0; c_tamp = 0;
                BufferedImage mk_img = mk.getSubimage(r * j, r * i, r, r);
                BufferedImage subImage = fractal.getSubimage(r * j, r * i, r, r);
                BufferedImage subImage1 = b.getSubimage(r * j, r * i, r, r);
                BufferedImage subImage2 = c.getSubimage(r * j, r * i, r, r);
                int[][] gray = getGrayValue(mk_img);
                int[][] grayValue = getGrayValue(subImage);
                int[][] grayValue1 = getGrayValue(subImage1);
                int[][] grayValue2 = getGrayValue(subImage2);
                for (int k = 0; k < r; k++) {
                    for (int l = 0; l < r; l++) {
                        mk_tamp += gray[k][l];
                        a_tamp += grayValue[k][l];
                        b_tamp += grayValue1[k][l];
                        c_tamp += grayValue2[k][l];
                    }
                }
                sum += mk_tamp == 4080 ? -w1 : w1;
                sum += a_tamp == 4080 ? -w2 : w2;
                sum += b_tamp == 4080 ? -w3 : w3;
                sum += c_tamp == 4080 ? -w4 : w4;
                val = sum >= 0 ? 1 : -1;
                t_l_k.replace(cnt, val);
                cnt++;
            }
        }

        cnt = 1;
        BufferedImage bufferedImage = readImageFile(new File("tamper.bmp"));
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                BufferedImage sub_img = Objects.requireNonNull(bufferedImage).getSubimage(r * j, r * i, r, r);
                WritableRaster raster = sub_img.getRaster();
                if (t_l_k.get(cnt) == -1) {
                    for (int k = 0; k < r; k++) {
                        for (int l = 0; l < r; l++) {
                            raster.setSample(k,l,0,255);
                        }
                    }
                }
                cnt++;
            }
        }
        writeImageFile(bufferedImage, "result");

    }

    // 整合图像
    public static BufferedImage compositeImage(int width, int height, int type){
        BufferedImage res_image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = res_image.getRaster();
        BufferedImage bufferedImage;
        HashMap<Integer,Integer> check;
        String old_path, path;
        switch (type) {
            case 1 -> {
                check = tamp_2;
                old_path = "graduate/tamperDetection/A/Range/";
                path = "graduate/Recover/A/";
            }
            case 2 -> {
                check = tamp_3;
                old_path = "graduate/tamperDetection/B/";
                path = "graduate/Recover/B/";
            }
            case 3 -> {
                check = tamp_4;
                old_path = "graduate/tamperDetection/C/";
                path = "graduate/Recover/C/";
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        for (int t = 0; t < 4; t++) {
            int cnt = 0;
            int x = 0, y = 0;
            String dir = null;
            ArrayList<Integer> list = null;
            switch (t) {
                case 0 -> {
                    dir = "first/";
                    list = A_firstIndex;
                    x = 128;
                    y = 0;
                }
                case 1 -> {
                    dir = "second/";
                    list = A_secondIndex;
                    x = 0;
                    y = 0;
                }
                case 2 -> {
                    dir = "third/";
                    list = A_thirdIndex;
                    x = 0;
                    y = 128;
                }
                case 3 -> {
                    dir = "forth/";
                    list = A_forthIndex;
                    x = 128;
                    y = 128;
                }
            }

            for (int m = 0; m < 16; m++) {
                for (int n = 0; n < 16; n++) {
                    Integer integer = list.get(cnt);
                    Integer state = check.get(integer);
                    switch (state) {
                        case -1 -> bufferedImage = readImageFile(new File("black.bmp"));
                        case 0, 1 -> bufferedImage = readImageFile(new File(old_path + dir + integer + ".bmp"));
                        case 2 -> bufferedImage = readImageFile(new File(path + dir + integer + ".bmp"));
                        default -> throw new IllegalStateException("Unexpected value: " + integer);
                    }
                    int[][] grayValue = getGrayValue(Objects.requireNonNull(bufferedImage));
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            raster.setSample(x + j + n * 8,y + i + m * 8,0,grayValue[j][i]);
                        }
                    }
                    cnt++;
                }
            }
        }

        return res_image;
    }

    // 比较校验位是否正确
    public static boolean compareCheckBits(BufferedImage image) throws IOException {
        int cnt = 0;
        int[] checkBits = extractCheckBits(image);
        for (int i:checkBits) cnt += i;
        if (cnt == 0) return false;

        BufferedImage image1 = recoverCheckBits(image);
        int[] checkBits1 = getCheckBits(image1);

        for (int i = 0; i < 16; i++) {
            if (checkBits[i] != checkBits1[i]) return false;
        }
        return true;
    }

    // 比较分形编码信息是否正确
    public static boolean compareFractalCode(BufferedImage src_image, BufferedImage dst_image, int type){
        int num, k, s, offset, scale_offset;
        String dir = null;
        int[] ints = extractFractalCode(dst_image);
        num = ints[0];
        k = ints[1];
        s = ints[2];
        offset = ints[3];

        switch (type) {
            case 1 -> dir = "/third/";
            case 2 -> dir = "/forth/";
            case 3 -> dir = "/first/";
            case 4 -> dir = "/second/";
        }
        BufferedImage bufferedImage = readImageFile(new File("graduate/tamperDetection/A/Domain/" + k + dir + num + ".bmp"));
        BufferedImage range_image = modify2LSB(src_image);
        BufferedImage domain_image = modify2LSB(bufferedImage);
        int scalefactor = getScalefactor(range_image, domain_image);
        int grayscaleoffset = getGrayscaleoffset(range_image, domain_image);
        if (Math.abs(grayscaleoffset) >= 1024) scale_offset = grayscaleoffset > 0 ? 1022 : -1022;
        else scale_offset = grayscaleoffset;
        float error = getError(range_image, domain_image);

        return (s == scalefactor) & (Math.abs(offset - scale_offset) <= 1);
    }

    // 获取嵌入的分形码信息
    public static int[] extractFractalCode(BufferedImage image){
        int width = image.getWidth();
        int[][] grayValue = getGrayValue(image);
        int[] ints = new int[28];
        int[] res = new int[4];

        int cnt = 0;
        int low, high;

        for (int i = 0; i < width; i++) {
            low = grayValue[i][0] & 1;
            high = (grayValue[i][0] >> 1) & 1;
            ints[cnt] = high;
            ints[cnt + 1] = low;
            cnt = cnt + 2;
        }
        for (int i = 0; i < 6; i++) {
            low = grayValue[i][1] & 1;
            high = (grayValue[i][1] >> 1) & 1;
            ints[cnt] = high;
            ints[cnt + 1] = low;
            cnt = cnt + 2;
        }

        int num = 0, k = 0, s = 0, offset = 0, s_prefix = 1, offset_prefix = 1;
        if (ints[13] == 1) s_prefix = -1;
        if (ints[18] == 1) offset_prefix = -1;
        for (int i = 0; i < 10; i++) {
            num += ints[9 - i] * Math.pow(2,i);
        }
        for (int i = 0; i < 3; i++) {
            k += ints[12 - i] * Math.pow(2,i);
        }
        for (int i = 0; i < 4; i++) {
            s += ints[17 - i] * Math.pow(2,i);
        }
        for (int i = 0; i < 9; i++) {
            offset += ints[27 - i] * Math.pow(2,i);
        }
        s = s_prefix * s;
        offset = offset_prefix * offset;

        res[0] = num; res[1] = k + 1; res[2] = s; res[3] = offset * 2;
        return res;
    }

    // 比较DCT系数是否正确
    public static boolean compareDctCoefficient(BufferedImage src_image, BufferedImage dst_image, int type){
        BufferedImage bufferedImage = modify2LSB(src_image);
        int[][] dct_quantize = getDCT_Quantize(bufferedImage);
        int[] dctCoefficient = extractDctCoefficient(dst_image, type);

        StringBuilder read_res = new StringBuilder();
        for (int j : dctCoefficient) {
            read_res.append(j);
        }

        String calc_res = completeBinary(dct_quantize[0][0]) +
                completeBinary(dct_quantize[1][0]) +
                completeBinary(dct_quantize[0][1]) +
                completeBinary(dct_quantize[0][2]) +
                completeBinary(dct_quantize[1][1]) +
                completeBinary(dct_quantize[2][0]);
        return calc_res.equals(read_res.toString());
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

    // 标识被篡改的图像块
    public static BufferedImage labelBlock(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        WritableRaster raster = image.getRaster();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                raster.setSample(i, j, 0, 0);
            }
        }
        return image;
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

    // 反DCT变换及反量化
    public static BufferedImage getI_DCT_Quantize(BufferedImage image, int type){
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] f_u_v = new int[height][width];
        int[] ints = extractDctCoefficient(image, type);
        BufferedImage res_image = new BufferedImage(width,height,10);
        WritableRaster raster = res_image.getRaster();
        double val, cu, cv;

        int[][] table = {{16,11,10,16,24,40,51,61},{12,12,14,19,26,58,60,55},{14,13,16,24,40,57,69,56},
                {14,17,22,29,51,87,80,62},{18,22,37,56,68,109,103,77},{24,35,55,64,81,104,113,92},
                {49,64,78,87,103,121,120,101}, {72,92,95,98,112,100,103,99}};

        for (int i = 1; i < 7; i++) {
            f_u_v[0][0] += ints[7 - i] * Math.pow(2,i - 1);
            f_u_v[1][0] += ints[14 - i] * Math.pow(2,i - 1);
            f_u_v[0][1] += ints[21 - i] * Math.pow(2,i - 1);
            f_u_v[0][2] += ints[28 - i] * Math.pow(2,i - 1);
            f_u_v[1][1] += ints[35 - i] * Math.pow(2,i - 1);
            f_u_v[2][0] += ints[42 - i] * Math.pow(2,i - 1);
        }
        if (ints[0] == 1) f_u_v[0][0] = -f_u_v[0][0];
        if (ints[7] == 1) f_u_v[1][0] = -f_u_v[1][0];
        if (ints[14] == 1) f_u_v[0][1] = -f_u_v[0][1];
        if (ints[21] == 1) f_u_v[0][2] = -f_u_v[0][2];
        if (ints[28] == 1) f_u_v[1][1] = -f_u_v[1][1];
        if (ints[35] == 1) f_u_v[2][0] = -f_u_v[2][0];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                f_u_v[i][j] = f_u_v[i][j] * table[i][j] * 2;
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                val = 0;
                for (int u = 0; u < height; u++) {
                    for (int v = 0; v < width; v++) {
                        cu = u == 0 ? Math.sqrt(1.0/width) : Math.sqrt(2.0/width);
                        cv = v == 0 ? Math.sqrt(1.0/width) : Math.sqrt(2.0/width);
                        val += cu * cv * f_u_v[u][v] * Math.cos((2*i+1) * Math.PI * u / (2*width))
                                * Math.cos((2*j+1) * Math.PI * v / (2*width));
                    }
                }
                raster.setSample(i,j,0,val);
            }
        }

        return res_image;
    }

    // 获取嵌入的DCT系数
    public static int[] extractDctCoefficient(BufferedImage image, int type){
        int width = image.getWidth();
        int[] ints = new int[42];
        int[][] grayValue = getGrayValue(image);

        int cnt = 0;
        int low, high;

        switch (type) {
            case 1 -> {
                for (int i = 6; i < 8; i++) {
                    low = grayValue[i][1] & 1;
                    high = (grayValue[i][1] >> 1) & 1;
                    ints[cnt] = high;
                    ints[cnt + 1] = low;
                    cnt = cnt + 2;
                }
                for (int j = 2; j < 4; j++) {
                    for (int i = 0; i < width; i++) {
                        low = grayValue[i][j] & 1;
                        high = (grayValue[i][j] >> 1) & 1;
                        ints[cnt] = high;
                        ints[cnt + 1] = low;
                        cnt = cnt + 2;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    low = grayValue[i][4] & 1;
                    high = (grayValue[i][4] >> 1) & 1;
                    ints[cnt] = high;
                    ints[cnt + 1] = low;
                    cnt = cnt + 2;
                }
            }
            case 2 -> {
                for (int i = 3; i < 8; i++) {
                    low = grayValue[i][4] & 1;
                    high = (grayValue[i][4] >> 1) & 1;
                    ints[cnt] = high;
                    ints[cnt + 1] = low;
                    cnt = cnt + 2;
                }
                for (int j = 5; j < 7; j++) {
                    for (int i = 0; i < width; i++) {
                        low = grayValue[i][j] & 1;
                        high = (grayValue[i][j] >> 1) & 1;
                        ints[cnt] = high;
                        ints[cnt + 1] = low;
                        cnt = cnt + 2;
                    }
                }
            }
        }
        return ints;
    }

    // 恢复添加校验位之前的2-LSB
    public static BufferedImage recoverCheckBits(BufferedImage image){
        int [][]gray = getGrayValue(image);
        WritableRaster raster = image.getRaster();

        for (int i = 0; i < image.getWidth(); i++) {
            int value = gray[i][7] & 252;
            raster.setSample(i, 7, 0, value);
        }

        return image;
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

    // 获取水印中的校验位
    public static int[] extractCheckBits(BufferedImage image){
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] grayValue = getGrayValue(image);
        int[] res = new int[16];
        int low, high;

        int cnt = 0;
        for (int i = 0; i < width; i++) {
            low = (grayValue[i][height-1] & 1);
            high = ((grayValue[i][height-1] >> 1) & 1);
            res[cnt] = high;
            res[cnt+1] = low;
            cnt = cnt + 2;
        }
        return res;
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

    // 划分A类块
    public static void getA_block(BufferedImage image, int r, int step) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        // 分割Range块
        for (int i = 0; i < height / r; i++) {
            for (int j = 0; j < width / r; j++) {
                BufferedImage subImage = image.getSubimage(r * j, r * i, r, r);
                File outfile = null;
                if (j < width / r / 2 && i < height / r / 2)
                    outfile = new File("graduate/tamperDetection/A/Range/second/" + aCount + ".bmp");
                if (j >= width / r / 2 && i < height / r / 2)
                    outfile = new File("graduate/tamperDetection/A/Range/first/" + aCount + ".bmp");
                if (j < width / r / 2 && i >= height / r / 2)
                    outfile = new File("graduate/tamperDetection/A/Range/third/" + aCount + ".bmp");
                if (j >= width / r / 2 && i >= height / r / 2)
                    outfile = new File("graduate/tamperDetection/A/Range/forth/" + aCount + ".bmp");
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
                            File outfile = new File("graduate/tamperDetection/A/Domain/" + k + "/second/" + d_2_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_2_Cnt + "个A类Domain块！");
                        d_2_Cnt++;
                    }
                    else if (j <= width - r && i <= height/2 - r){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/tamperDetection/A/Domain/" + k + "/first/" + d_1_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_1_Cnt + "个A类Domain块！");
                        d_1_Cnt++;
                    }
                    else if (j <= width/2 - r && i >= height/2){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/tamperDetection/A/Domain/" + k + "/third/" + d_3_Cnt + ".bmp");
                            ImageIO.write(bufferedImage, "bmp", Objects.requireNonNull(outfile));
                        }
                        System.out.println("正在划分第" + d_3_Cnt + "个A类Domain块！");
                        d_3_Cnt++;
                    }
                    else if (j >= width/2 && i >= height/2){
                        for (int k = 1; k < 9; k++) {
                            BufferedImage bufferedImage = selectAffineTrans(subImage, k);
                            File outfile = new File("graduate/tamperDetection/A/Domain/" + k + "/forth/" + d_4_Cnt + ".bmp");
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
                        outfile = new File("graduate/tamperDetection/B/second/" + bCount + ".bmp");
                    if (j >= width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/tamperDetection/B/first/" + bCount + ".bmp");
                    if (j < width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/tamperDetection/B/third/" + bCount + ".bmp");
                    if (j >= width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/tamperDetection/B/forth/" + bCount + ".bmp");
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

        // 划分子块
        BufferedImage new_Image = readImageFile(new File("graduate/C/newC.bmp"));
        try {
            for (int i = 0; i < height / r; i++) {
                for (int j = 0; j < width / r; j++) {
                    BufferedImage subImage = Objects.requireNonNull(new_Image).getSubimage(r * j, r * i, r, r);
                    File outfile = null;
                    if (j < width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/tamperDetection/C/second/" + cCount + ".bmp");
                    if (j >= width / r / 2 && i < height / r / 2)
                        outfile = new File("graduate/tamperDetection/C/first/" + cCount + ".bmp");
                    if (j < width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/tamperDetection/C/third/" + cCount + ".bmp");
                    if (j >= width / r / 2 && i >= height / r / 2)
                        outfile = new File("graduate/tamperDetection/C/forth/" + cCount + ".bmp");
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
}
