package cn.keking.utils;

import cn.hutool.core.collection.CollUtil;
import cn.keking.config.ConfigConstants;
import cn.keking.huawei.ObsService;
import cn.keking.huawei.ObsServiceContext;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfUtils {

    private final Logger logger = LoggerFactory.getLogger(PdfUtils.class);

    private final FileUtils fileUtils;

    @Value("${server.tomcat.uri-encoding:UTF-8}")
    private String uriEncoding;

    public PdfUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public List<String> pdf2jpg(String pdfFilePath, String pdfName, String baseUrl) {
        String pdfFolder = pdfName.substring(0, pdfName.length() - 4);
        List<String> imageUrls = new ArrayList<>();
        Boolean obsEnabled = ConfigConstants.isObsEnabled();
        ObsService obsService = null;
        if (obsEnabled) {
            obsService = ObsServiceContext.getObsService();
            List<String> urls = obsService.listFolderFiles(pdfFolder);
            if (CollUtil.isNotEmpty(urls)) {
                return urls;
            }
        }

        Integer imageCount = fileUtils.getConvertedPdfImage(pdfFilePath);
        String imageFileSuffix = ".jpg";
        String urlPrefix;
        try {
            urlPrefix = baseUrl + URLEncoder.encode(URLEncoder.encode(pdfFolder, uriEncoding).replaceAll("\\+", "%20"), uriEncoding);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
            urlPrefix = baseUrl + pdfFolder;
        }
        if (imageCount != null && imageCount > 0) {
            for (int i = 0; i < imageCount; i++)
                imageUrls.add(urlPrefix + "/" + i + imageFileSuffix);
            return imageUrls;
        }
        try {
            File pdfFile = new File(pdfFilePath);
            PDDocument doc = PDDocument.load(pdfFile);
            int pageCount = doc.getNumberOfPages();
            PDFRenderer pdfRenderer = new PDFRenderer(doc);

            int index = pdfFilePath.lastIndexOf(".");
            String folder = pdfFilePath.substring(0, index);

            File path = new File(folder);
            if (!path.exists()) {
                path.mkdirs();
            }

            if (obsEnabled) {
                obsService.newFolder(pdfFolder);
            }

            String imageFilePath;
            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                imageFilePath = folder + File.separator + pageIndex + imageFileSuffix;
                String obsImageFilePath = pdfFolder + "/" + pageIndex + imageFileSuffix;
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 105, ImageType.RGB);
                ImageIOUtil.writeImage(image, imageFilePath, 105);
                if (obsEnabled) {
                    String osbImgUrl = obsService.fileUpload(obsImageFilePath, new File(imageFilePath));
                    imageUrls.add(osbImgUrl);
                } else {
                    imageUrls.add(urlPrefix + "/" + pageIndex + imageFileSuffix);
                }
            }
            doc.close();
            fileUtils.addConvertedPdfImage(pdfFilePath, pageCount);
        } catch (IOException e) {
            logger.error("Convert pdf to jpg exception, pdfFilePath：{}", pdfFilePath, e);
        }
        return imageUrls;
    }
}
