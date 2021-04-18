package graduate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/*
* 原始分形压缩算法实现
*/

public class originalCompression {

	static int rangeCount = 1;
	static int domainCount = 1;
	public static void main(String[] args) throws IOException {

		int rangeR = 4;
		int domainR = 2 * rangeR;
		int step = 4;
		long startTime = System.currentTimeMillis();
		//读取测试图像
		File file = new File("./graduate/lena_256.bmp");
		BufferedImage test_image = readImageFile(file);
		int width = test_image.getWidth();
		int height = test_image.getHeight();

		//创建分形编码文件
		File out_file = new File("./graduate/encode.txt");
		BufferedWriter out_txt = new BufferedWriter(new FileWriter(out_file)); 
		out_txt.write(height + "\t" + width + "\t" + rangeR + "\t" + domainR + "\t" + step + "\n");
		
		//输出图像
//		BufferedImage out_image = new BufferedImage(height, width, test_image.getType());
//		WritableRaster raster = out_image.getRaster();
		
		// 划分Range块
		cutRangeBlock(test_image, rangeR);

		// 划分Domain块并压缩
		cutDomainBlock(test_image, domainR, step);

		// 对Domain块处理获得Tn(Dj);
		getTnDomain("./graduate/Domain");

		//寻找最佳匹配块
		for(int i = 1; i < rangeCount; i++){
			int targetDomain = 1, targetTransform = 1;
			double MSE;
			double minMSE = 99999.0;
			File rangeFile = new File("./graduate/Range/" + i + ".bmp");
			BufferedImage rangeImage = readImageFile(rangeFile);
			
			for(int k = 1; k < 9; k++){
				for(int j = 1; j < domainCount; j++){
					File domainFile = new File("./graduate/TnDomain/" + k + "/" + j + ".bmp");
					BufferedImage domainImage = readImageFile(domainFile);
					
					MSE = getError(rangeImage, domainImage);
					if(minMSE > MSE){
						minMSE = MSE;
						targetDomain = j;
						targetTransform = k;
					}

				}
				
			}

			File targetDomainFile = new File("./graduate/TnDomain/" + targetTransform + "/" + targetDomain + ".bmp");
			BufferedImage targetDomainImage = readImageFile(targetDomainFile);
			double scalefactor = getScalefactor(rangeImage, targetDomainImage);
			double offset = getGrayscaleoffset(rangeImage, targetDomainImage);
			String outcome = "i = " + i + ", j = " + targetDomain + ", k = " + targetTransform  + ", s = " + scalefactor + ", offset = " + offset;
			System.out.println(outcome);
			out_txt.write(i + "\t" + targetDomain + "\t" + targetTransform + "\t" + scalefactor + "\t" + offset + "\n");
			out_txt.flush();

//			BufferedImage modifyDomainImage = modifyGrayValue(targetDomainImage, scalefactor, offset);
//			double [][]domain = getGrayValue(modifyDomainImage);
//			for(int m = 0; m < modifyDomainImage.getHeight(); m++){
//				for(int n =0; n < modifyDomainImage.getWidth(); n++){
//					int x = ((i - 1) % (width / rangeR)) * rangeR + m;
//					int y = ((i - 1) / (height / rangeR)) * rangeR + n;
//					//System.out.println(x + "\t" + y);
//					raster.setSample(x, y, 0, domain[m][n]);
//				}
//			}
		}
		long endTime = System.currentTimeMillis();
		out_txt.close();
//		writeImageFile(out_image, "compressed");

//		System.out.println(rangeCount + "\t" + domainCount);
//		double psnr = getPSNR(test_image, out_image);
//		System.out.println(psnr);
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
	public static double[][] getGrayValue(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		double[][] array = new double[height][width];
		WritableRaster raster = image.getRaster();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				array[i][j] = raster.getSample(i, j, 0);
			}
		}

		return array;
	}

	// 修改图像块的灰度值
	public static BufferedImage modifyGrayValue(BufferedImage image, Double scalefactor, Double offset) {
		double value = 0;
		int height = image.getHeight();
		int width = image.getWidth();
		double [][]gray = getGrayValue(image);

		BufferedImage modifiedImage = new BufferedImage(height, width, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = modifiedImage.getRaster();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				value = scalefactor * gray[i][j] + offset;
				raster.setSample(i, j, 0, value);
			}
		}	

		return modifiedImage;
	}

	// 划分Range块
	public static boolean cutRangeBlock(BufferedImage image, int r) {
		int width = image.getWidth();
		int height = image.getHeight();

		// 分割图像
		try {
			for (int i = 0; i < height / r; i++) {
				for (int j = 0; j < width / r; j++) {
					BufferedImage subImage = image.getSubimage(r * j, r * i, r, r);
					System.out.println("正在划分第" + rangeCount + "个Range块！");
					File outfile = new File("./graduate/Range/" + rangeCount + ".bmp");
					if (ImageIO.write(subImage, "bmp", outfile)) {
						// System.out.println("Range块划分成功!");
					}
					rangeCount++;
				}
			}
			System.out.println("Range块处理完成!");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Range块处理失败!");
		return false;
	}

	// 划分Domain块并压缩
	public static boolean cutDomainBlock(BufferedImage image, int r, int step) {
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
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Domain块处理失败!");
		return false;
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
								BufferedImage modifyImage = selectAffineTrans(image, k);
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

	// 几何变换  ----压缩Domain块
	public static BufferedImage compressDomain(BufferedImage image) {
		int width = image.getWidth() / 2;
		int height = image.getHeight() / 2;
		double [][]array = getGrayValue(image);
		BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = compressedImage.getRaster();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double Sample = (array[2 * i][2 * j] + array[2 * i + 1][2 * j] + array[2 * i][2 * j + 1] + array[2 * i + 1][2 * j + 1]) / 4;
				raster.setSample(i, j, 0, Sample);
			}
		}
		return compressedImage;
	}

	// 图像旋转变换
	public static BufferedImage rotateImage(BufferedImage image, int degree) {
        int width = image.getWidth();
		int height = image.getHeight();
		double [][]array = getGrayValue(image);
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
		double[][] array = getGrayValue(image);
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
		double [][]range = getGrayValue(Range);
		double [][]domain = getGrayValue(Domain);

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

		double [][]range = getGrayValue(Range);
		double [][]domain = getGrayValue(Domain);

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
		double [][]range = getGrayValue(Range);
		double [][]domain = getGrayValue(Domain);

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

	//计算PSNR
	public static double getPSNR(BufferedImage originalImage, BufferedImage decodeImage){
		double mse = getError(originalImage, decodeImage);
		return 10 * Math.log10((65025 / mse));
	}

	// 进行DCT变换并标准化
	public static double[][] performDCT(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		double [][]dct = new double[height][width];
		double [][]gray = getGrayValue(image);
		double sum = 0;
		double dct2d = 0;
		double cu = Math.sqrt(2.0/width);
		double cv = Math.sqrt(2.0/width);

		for(int u = 0; u < height; u ++){
			for(int v = 0; v < width; v++){
				for(int i = 0; i < height; i ++){
					for(int j = 0; j < width; j++){
						sum += gray[i][j] * Math.cos((2*i+1) * Math.PI * u / (2*width)) * Math.cos((2*j+1) * Math.PI * v / (2*width));
					}
				}
				if(u == 0)
					cu = Math.sqrt(1.0/width);
				if(v == 0)
					cv = Math.sqrt(1.0/width);
				dct[u][v] = cu * cv * sum;
				dct2d += dct[u][v] * dct[u][v];
			}
		}
		dct2d = Math.sqrt(dct2d);

		for(int u = 0; u < height; u ++){
			for(int v = 0; v < width; v++) {
				dct[u][v] = Math.abs(dct[u][v]) / dct2d;
			}
		}

		return dct;
	}
}