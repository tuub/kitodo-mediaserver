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
import java.io.IOException;
import java.nio.file.Paths;
import javax.validation.constraints.NotNull;
import javax.xml.xpath.XPathExpressionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.kitodo.mediaserver.core.api.IDocument;
import org.kitodo.mediaserver.core.api.IOcrReader;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.kitodo.mediaserver.core.processors.ocr.OcrLine;
import org.kitodo.mediaserver.core.processors.ocr.OcrPage;
import org.kitodo.mediaserver.core.processors.ocr.OcrParagraph;
import org.kitodo.mediaserver.core.processors.ocr.OcrWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * A PDF page.
 */
@Component
@Scope("prototype")
public class PdfboxPage extends AbstractPage {

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
