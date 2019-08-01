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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.kitodo.mediaserver.core.api.IPage;

/**
* A PDF document.
*/
public class PdfboxDocument extends AbstractDocument {

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

        // Version 1.7 needed for PDF/A-2
        document.setVersion(1.7f);

        // TTF font needed for Unicode support in OCR texts
        PDFont font = PDType0Font.load(document,
            PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), true);

        // Metadata
        String title = getTitle();
        String author = getAuthor();
        Calendar creationDate = Calendar.getInstance();

        // Create XMP metadata (needed by PDF/A)
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();

        // Create document information metadata
        PDDocumentInformation docInfo = new PDDocumentInformation();
        docInfo.setCreationDate(creationDate);

        DublinCoreSchema dublinCore = xmp.createAndAddDublinCoreSchema();

        if (title != null) {
            docInfo.setTitle(title);
            dublinCore.setTitle(title);
        }
        if (author != null) {
            docInfo.setAuthor(author);
            dublinCore.addCreator(author);
        }

        // Add document information metadata
        document.setDocumentInformation(docInfo);

        // Add XMP basic schema
        XMPBasicSchema xmpBasic = xmp.createAndAddXMPBasicSchema();
        xmpBasic.setCreateDate(creationDate);

        // Set PDF/A metadata
        PDFAIdentificationSchema pdfaId = xmp.createAndAddPFAIdentificationSchema();
        pdfaId.setPart(2);
        pdfaId.setConformance("B");

        // Create xpacket text from XMP
        XmpSerializer serializer = new XmpSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(xmp, outputStream, true);

        // Write XMP metadata to document
        PDMetadata metadata = new PDMetadata(document);
        metadata.importXMPMetadata(outputStream.toByteArray());
        document.getDocumentCatalog().setMetadata(metadata);

        // Set color profile (needed by PDF/A)
        // Use a default sRGB profile for everything
        InputStream colorProfileStream = new ByteArrayInputStream(getIccProfile().getData());
        PDOutputIntent intent = new PDOutputIntent(document, colorProfileStream);
        intent.setInfo("sRGB");
        intent.setOutputCondition("sRGB");
        intent.setOutputConditionIdentifier("sRGB");
        intent.setRegistryName("");
        document.getDocumentCatalog().addOutputIntent(intent);

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
