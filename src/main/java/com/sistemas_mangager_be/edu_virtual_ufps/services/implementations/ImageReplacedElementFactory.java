package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.xhtmlrenderer.extend.*;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.w3c.dom.Element;
import java.io.IOException;
import com.lowagie.text.Image;

public class ImageReplacedElementFactory implements ReplacedElementFactory {
    private final ReplacedElementFactory superFactory;
    private FormSubmissionListener formSubmissionListener;

    public ImageReplacedElementFactory(ReplacedElementFactory superFactory) {
        this.superFactory = superFactory;
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, 
            UserAgentCallback uac, int cssWidth, int cssHeight) {
        Element e = box.getElement();
        if (e == null) {
            return null;
        }

        String nodeName = e.getNodeName();
        if ("img".equals(nodeName)) {
            String src = e.getAttribute("src");
            try {
                // Manejar imágenes base64
                if (src.startsWith("data:image")) {
                    String base64 = src.substring(src.indexOf(",") + 1);
                    byte[] bytes = java.util.Base64.getDecoder().decode(base64);
                    Image image = Image.getInstance(bytes);
                    ITextFSImage fsImage = new ITextFSImage(image);
                    if (cssWidth != -1 || cssHeight != -1) {
                        fsImage.scale(cssWidth, cssHeight);
                    }
                    return new ITextImageElement(fsImage);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error cargando imagen: " + src, ex);
            }
        }
        return superFactory.createReplacedElement(c, box, uac, cssWidth, cssHeight);
    }

    @Override
    public void reset() {
        superFactory.reset();
    }

    @Override
    public void remove(Element e) {
        superFactory.remove(e);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        this.formSubmissionListener = listener;
        if (superFactory != null) {
            superFactory.setFormSubmissionListener(listener);
        }
    }
}