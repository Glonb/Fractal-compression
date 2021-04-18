package graduate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Vector;
//原始分形压缩算法的解码实现

public class originalDecode {

    static Vector<String> out = new Vector<>();
    public static void main(String[] args) throws IOException {
    	long startTime = System.currentTimeMillis();
		//读取测试图像
		File file = new File("./graduate/lena_256.bmp");
		BufferedImage test_image = readImageFile(file);

        readEncodeFile("./graduate/encode.txt");
        String []first = (out.elementAt(0)).split("\t");
		int rangeR = Integer.parseInt(first[2]);
		int domainR = Integer.parseInt(first[3]);
		int step = Integer.parseInt(first[4]);

		//创建R区以及D区
        int height = Integer.parseInt(first[0]);
        int width = Integer.parseInt(first[1]);
        BufferedImage RangeArea = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage DomainArea = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		//迭代生成图像
		for(int t = 0; t < 19; t++){
			WritableRaster raster = RangeArea.getRaster();
			double PSNR = getPSNR(RangeArea, test_image);
			System.out.println("PSNR: " + PSNR);

			for(int i = 1; i < out.size(); i++){
				//获取码本信息
				String []parse = (out.elementAt(i)).split("\t");
				int j = Integer.parseInt(parse[1]);
				int k = Integer.parseInt(parse[2]);
				double scalefactor = Double.parseDouble(parse[3]);
				double offset = Double.parseDouble(parse[4]);

				BufferedImage domainImage = getDomainBlock(DomainArea, domainR, step, j);
				BufferedImage transDomain = selectAffineTrans(compressDomain(domainImage), k);
				BufferedImage finalDomainImage = modifyGrayValue(transDomain, scalefactor, offset);
				int[][] domain = getGrayValue(finalDomainImage);

				for(int m = 0; m < finalDomainImage.getHeight(); m++){
					for(int n =0; n < finalDomainImage.getWidth(); n++){
						int x = ((i - 1) % (width / rangeR)) * rangeR + m;
						int y = ((i - 1) / (height / rangeR)) * rangeR + n;
						//System.out.println(x + "\t" + y);
						raster.setSample(x, y, 0, domain[m][n]);
					}
				}
			}

			DomainArea = modifyGrayValue(RangeArea, 1.0, 0.0);
		}
		long endTime = System.currentTimeMillis();
		writeImageFile(RangeArea, "outcome");

		double PSNR = getPSNR(RangeArea, test_image);
		System.out.println("PSNR: " + PSNR);

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
                out.add(str);
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
			if (ImageIO.write(image, "tif", outfile)) {
				System.out.println("图像写入成功！");
				return;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("图像写入失败！");
	}

    // 查看图像每个像素灰度值
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

	//修改图像块的灰度值
	public static BufferedImage modifyGrayValue(BufferedImage image, Double scalefactor, Double offset) {
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

	//查找对应Domain块
	public static BufferedImage getDomainBlock(BufferedImage image, int domainR, int step, int targeted) {
		int width = image.getWidth();
		int height = image.getHeight();
		int count = 0;
		BufferedImage rtimage = new BufferedImage(width, height, image.getType()); 

		//分割图像
		for (int i = 0; i < height; i = i + step) {
			for (int j = 0; j < width; j = j + step) {
				if((i <= height - domainR) && (j <= width - domainR)){
					BufferedImage subImage = image.getSubimage(j, i, domainR, domainR);
					count++;	
					if(count == targeted){
						rtimage = subImage;
					}
				}	
			}
		}
		return rtimage;
	}

	//几何变换操作
	public static BufferedImage compressDomain(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] array = getGrayValue(image);
		BufferedImage compressedImage = new BufferedImage(width / 2, height / 2, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = compressedImage.getRaster();

		for (int i = 0; i < height / 2; i++) {
			for (int j = 0; j < width / 2; j++) {
				int Sample = (array[2 * i][2 * j] + array[2 * i + 1][2 * j] + array[2 * i][2 * j + 1]
						+ array[2 * i + 1][2 * j + 1]) / 4;
				raster.setSample(i, j, 0, Sample);
			}
		}
		return compressedImage;
	}

	//选择仿射变换
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
		int[][] array;
		array = getGrayValue(image);
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
	
	//图像对称变换
	public static BufferedImage symmetryImage(BufferedImage image, String type) {
        int width = image.getWidth();
		int height = image.getHeight();
		int[][] array;
		array = getGrayValue(image);
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

	//计算比例因子
	public static double getScalefactor(BufferedImage Range, BufferedImage Domain){
		double s;
		double totalR = 0, totalD = 0, totalD2 = 0;
		double upfirst = 0;
		int width = Range.getWidth();
		int height = Range.getHeight();
		int N = width * height;
		int [][]range = getGrayValue(Range);
		int [][]domain = getGrayValue(Domain);

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

	//计算灰度偏移量
	public static double getGrayscaleoffset(BufferedImage Range, BufferedImage Domain){
		double grayoffset;
		int width = Range.getWidth();
		int height = Range.getHeight();
		int N = width * height;
		int first = 0, second = 0;

		int [][]range = getGrayValue(Range);
		int [][]domain = getGrayValue(Domain);

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				first += range[i][j];
				second += domain[i][j];
			}
		}

		grayoffset = (first / N) - (second / N)* getScalefactor(Range,Domain);
		return grayoffset;
	}

	//计算误差
	public static double getMeanSquareError(BufferedImage originalImage, BufferedImage decodeImage){
		double e = 0.0;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		int N = width * height;
		int [][]original = getGrayValue(originalImage);
		int [][]decode = getGrayValue(decodeImage);

		for(int i = 0; i < height; i ++){
			for(int j = 0; j < width; j++){
				e += ( original[i][j] - decode[i][j] ) * ( original[i][j] - decode[i][j] );
			}
		}

		return e / N;
	}

	//计算峰值信噪比PSNR
	public static double getPSNR(BufferedImage originalImage, BufferedImage decodeImage){

		double mse = getMeanSquareError(originalImage, decodeImage);
		return 10 * Math.log10((65025 / mse));
	}

}