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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import org.kitodo.mediaserver.core.api.IDocument;
import org.kitodo.mediaserver.core.api.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A file converter to produce image files using AWT package.
 */
public class AwtImageFileConverter extends AbstractConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwtImageFileConverter.class);

    @Autowired
    private ObjectFactory<AwtImagePage> pageFactory;

    @Override
    public InputStream convert(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, Object> parameter) throws Exception {
        checkParams(pages, parameter, "derivativePath", "target_mime");

        int size = getConversionSize(parameter);

        // if the cache file already exists, there is another thread already performing the conversion.
        Map.Entry<File, Boolean> convertedFile = createDerivativeFile((String)parameter.get("derivativePath"));

        if (!convertedFile.getValue()) {
            try {
                IDocument document = new AwtImageDocument();
                IPage page = pageFactory.getObject();
                page.setImagePath(pages.firstEntry().getValue().get("master").getFile().getAbsolutePath());
                page.setSize(size);
                document.getPages().add(page);
                document.save(convertedFile.getKey().getAbsolutePath());
            } catch (Exception e) {
                convertedFile.getKey().delete();
                throw e;
            }
        }

        InputStream convertedInputStream = new FileInputStream(convertedFile.getKey());

        cleanDerivativeFile(convertedFile.getKey());

        return convertedInputStream;
    }

    /**
     * A document representation using an image.
     */
    static class AwtImageDocument extends AbstractDocument {

        private List<IPage> pages = new ArrayList<>();

        @Override
        public BufferedImage getDocument() {
            return null;
        }

        @Override
        public List<IPage> getPages() {
            return pages;
        }

        @Override
        public void save(String path) throws Exception {
            pages.get(0).renderPage(this);
            ImageIO.write((BufferedImage) pages.get(0).getPage(), "jpg", new File(path));
        }
    }

    /**
     * A page representation using an image.
     */
    @Component
    @Scope("prototype")
    static class AwtImagePage extends AbstractPage {

        protected BufferedImage page = null;

        @Override
        public BufferedImage getPage() {
            return page;
        }

        @Override
        public void renderPage(@NotNull IDocument document) throws Exception {
            page = renderImage();
        }
    }
}
