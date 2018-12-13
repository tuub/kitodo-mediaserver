/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.core.conversion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.validation.constraints.NotNull;
import javax.xml.xpath.XPathExpressionException;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.kitodo.mediaserver.core.api.IDocument;
import org.kitodo.mediaserver.core.api.IOcrReader;
import org.kitodo.mediaserver.core.api.IPage;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.kitodo.mediaserver.core.processors.ocr.OcrLine;
import org.kitodo.mediaserver.core.processors.ocr.OcrPage;
import org.kitodo.mediaserver.core.processors.ocr.OcrParagraph;
import org.kitodo.mediaserver.core.processors.ocr.OcrWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.security.action.GetPropertyAction;

/**
 * A converter to produce PDF files using Apache PDFBox package.
 */
public class PdfboxFileConverter extends AbstractConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfboxFileConverter.class);

    @Autowired
    private ObjectFactory<PdfboxPage> pageFactory;

    @Override
    public InputStream convert(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, String> parameter) throws Exception {

        checkParams(pages, parameter, "derivativePath", "target_mime");

        int size = getConversionSize(parameter);

        File convertedFile = new File(conversionTargetPath, parameter.get("derivativePath"));

        // if the cache file already exists, there is another thread already performing the conversion.
        boolean fileAlreadyExists = createCacheFile(convertedFile);

        if (!fileAlreadyExists) {
            try {

                // Set up memory usage settings for PDF conversion
                File tmpDir = new File(AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
                MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting
                    .setupMixed(conversionPropertiesPdf.getMaxMemory() * 1024 * 1024)
                    .setTempDir(tmpDir);

                // Initialize PDF document
                IDocument document = new PdfboxDocument(memoryUsageSetting);

                // Set up all pages
                for (Map<String, FileEntry> metsPage : pages.values()) {
                    PdfboxPage page = pageFactory.getObject();
                    page.setImagePath(metsPage.get("master").getFile().getAbsolutePath());
                    page.setSize(size);

                    // Add optional Fulltext file
                    FileEntry fulltextEntry = metsPage.get("fulltext");
                    if (fulltextEntry != null) {
                        page.setFulltextPath(fulltextEntry.getFile().getAbsolutePath());
                    }

                    document.getPages().add(page);
                }

                // Save PDF file
                document.save(convertedFile.getAbsolutePath());

            } catch (Exception e) {
                convertedFile.delete();
                throw e;
            }
        }

        InputStream convertedInputStream = new FileInputStream(convertedFile);

        if (!saveConvertedFile) {
            LOGGER.info("Deleting file " + convertedFile.getAbsolutePath());
            convertedFile.delete();
        }

        return convertedInputStream;
    }

    /**
     * A PDF document.
     */
    static class PdfboxDocument implements IDocument {

        private PDDocument document;
        private List<IPage> pages = new ArrayList<>();

        public PdfboxDocument(MemoryUsageSetting memoryUsageSetting) {
            document = new PDDocument(memoryUsageSetting);
        }

        public PDDocument getDocument() {
            return document;
        }

        @Override
        public List<IPage> getPages() {
            return pages;
        }

        @Override
        public void save(String path) throws Exception {

            // TTF font needed for Unicode support in OCR texts
            PDFont font = PDType0Font.load(document,
                PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), true);

            // Render all pages
            for (IPage page : pages) {
                ((PdfboxPage)page).setFont(font);
                page.renderPage(this);
                document.addPage((PDPage) page.getPage());
            }

            document.save(path);
            document.close();
        }
    }

    /**
     * A PDF page.
     */
    @Component
    @Scope("prototype")
    static class PdfboxPage extends AbstractPage {

        private static final Logger LOGGER = LoggerFactory.getLogger(PdfboxPage.class);

        private PDPage page;
        private String fulltextPath;
        protected IOcrReader ocrReader;
        protected PDFont font;

        @Autowired
        protected ConversionProperties.Pdf conversionPropertiesPdf;

        @Override
        public PDPage getPage() {
            return page;
        }

        public void setFulltextPath(String fulltextPath) {
            this.fulltextPath = fulltextPath;
        }

        @Autowired
        public void setOcrReader(IOcrReader ocrReader) {
            this.ocrReader = ocrReader;
        }

        public void setFont(PDFont font) {
            this.font = font;
        }

        @Override
        public void setImagePath(@NotNull String path) {
            super.setImagePath(path);
        }

        @Override
        public void renderPage(IDocument document) throws Exception {

            // Get processed image and initialize the page
            BufferedImage resizedImage = renderImage();
            page = new PDPage(new PDRectangle(resizedImage.getWidth(), resizedImage.getHeight()));

            // Draw image on page
            PDDocument pdDocument = (PDDocument) document.getDocument();
            PDImageXObject imageObj = JPEGFactory.createFromImage(pdDocument, resizedImage);
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page);
            contentStream.drawImage(imageObj, 0, 0);

            // Add OCR Text layer
            if (conversionPropertiesPdf.isAddOcrText() && StringUtils.hasText(fulltextPath)) {
                try {
                    addOcrText(contentStream);
                } catch (Exception ex) {
                    LOGGER.warn("Could not write OCR text on page using fulltext file '" + fulltextPath + "'", ex);
                }
            }

            contentStream.close();
        }

        /**
         * Add OCR text to page.
         *
         * @param stream PDF Page content stream
         * @throws IOException on file errors
         * @throws XPathExpressionException on OCR XML parse errors
         */
        private void addOcrText(PDPageContentStream stream) throws Exception {

            // Make text invisible
            stream.setRenderingMode(RenderingMode.NEITHER);

            // Get OCR text
            OcrPage ocrPage = ocrReader.read(Paths.get(fulltextPath));

            float tx;
            float ty;
            float size = 12f;
            float wordWidth;
            float wordWidthScaling;
            OcrWord lastWord = new OcrWord();

            // Write words on canvas
            stream.beginText();
            // PDF layout is upside down, so start on correct top
            stream.newLineAtOffset(imageRect.x, imageSize.height * imageScaling + (pageSize.height - imageRect.height - imageRect.y));
            for (OcrParagraph paragraph : ocrPage.paragraphs) {
                for (OcrLine line : paragraph.lines) {
                    for (OcrWord word : line.words) {

                        // Use word height to guess font size
                        if (word.height > 0) {
                            size = word.height * imageScaling * 1.1f;
                        }

                        // Check word width, if not possible (maybe illegal characters) drop this word and continue
                        try {
                            wordWidth = font.getStringWidth(word.word) / 1000 * size;
                        } catch (Exception ex) {
                            LOGGER.debug("Could not render word '" + word.word + "'. Dropping it.", ex);
                            continue;
                        }

                        // Calculate relative word position
                        tx = (word.x - lastWord.x) * imageScaling;
                        ty = ((lastWord.y + lastWord.height) - (word.y + word.height)) * imageScaling;

                        // Stretch word to match word box
                        wordWidthScaling = 100 * word.width * imageScaling / wordWidth;
                        stream.setHorizontalScaling(wordWidthScaling);

                        // TODO: [MINOR] Check if word has descending characters and add this space to y

                        // Set up canvas and write word
                        stream.newLineAtOffset(tx, ty);
                        stream.setFont(font, size);
                        stream.showText(word.word);

                        lastWord = word;
                    }
                }
            }
            stream.endText();
        }
    }
}
