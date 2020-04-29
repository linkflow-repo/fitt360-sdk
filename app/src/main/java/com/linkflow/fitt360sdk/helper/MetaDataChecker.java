package com.linkflow.fitt360sdk.helper;

import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.UserBox;
import com.googlecode.mp4parser.util.Path;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import pixy.meta.Metadata;
import pixy.meta.MetadataType;
import pixy.meta.xmp.XMP;
import pixy.string.StringUtils;

public class MetaDataChecker {
    private String xmp_meta_str = "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" xmptk=\"FITT360\">\n" +
            "    <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
            "        <rdf:Description xmlns:GPano=\"http://ns.google.com/photos/1.0/panorama/\" rdf:about=\"\">\n" +
            "            <GPano:ProjectionType>equirectangular</GPano:ProjectionType>\n" +
            "            <GPano:UsePanoramaViewer>True</GPano:UsePanoramaViewer>\n" +
            "            <GPano:CroppedAreaImageWidthPixels>5376</GPano:CroppedAreaImageWidthPixels>\n" +
            "            <GPano:CroppedAreaImageHeightPixels>2688</GPano:CroppedAreaImageHeightPixels>\n" +
            "            <GPano:FullPanoWidthPixels>5376</GPano:FullPanoWidthPixels>\n" +
            "            <GPano:FullPanoHeightPixels>2688</GPano:FullPanoHeightPixels>\n" +
            "            <GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels>\n" +
            "            <GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels>\n" +
            "            <GPano:PoseHeadingDegrees>180.0</GPano:PoseHeadingDegrees>\n" +
            "            <GPano:PosePitchDegrees>-8.9</GPano:PosePitchDegrees>\n" +
            "            <GPano:PoseRollDegrees>0.7</GPano:PoseRollDegrees>\n" +
            "        </rdf:Description>\n" +
            "    </rdf:RDF>\n" +
            "</x:xmpmeta>";

    public boolean is360Photo(String photoFilePath) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(photoFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<MetadataType, Metadata> metaMap = null;
        try {
            metaMap = Metadata.readMetadata(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert metaMap != null;
        if(metaMap.containsKey(MetadataType.XMP)) {
            XMP xmp = (XMP)metaMap.get(MetadataType.XMP);
            if(xmp != null) {
                Document mergedDoc = xmp.getMergedDocument();
                if(mergedDoc != null) {
                    NodeList nodeList = mergedDoc.getElementsByTagName("GPano:ProjectionType");
                    if(nodeList != null && nodeList.getLength() > 0) {
                        Node node = nodeList.item(0);
                        if(node != null) {
                            String text = ((Node) node).getTextContent();
                            if(!StringUtils.isNullOrEmpty(text) && text.equalsIgnoreCase("equirectangular")) {
                                Log.i(getClass().getName(), "This file has equirectangular projection tag");
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean is360Video(String videoFilePath) throws IOException {
        File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            try {
                throw new FileNotFoundException("File " + videoFilePath + " not exists");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (!videoFile.canWrite()) {
            throw new IllegalStateException("No write permissions to file " + videoFilePath);
        }
        IsoFile isoFile = null;
        try {
            isoFile = new IsoFile(videoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert isoFile != null;
        if (isoFile.getBoxes().size() == 0)
            throw new IllegalStateException("Null box exception");

        boolean b360BoxExist = false;
        MovieBox moov = isoFile.getMovieBox();
        if(moov != null) {
            for (Box trBox : moov.getBoxes()) {
                if ("trak".equals(trBox.getType())) {
                    MediaBox mdBox = null;
                    if ((mdBox = Path.getPath(trBox, "mdia")) == null) {
                        break;
                    }

                    HandlerBox hdlrBox = mdBox.getHandlerBox();
                    if (hdlrBox != null) {
                        if (findYoutoubeBox(moov) != null) {
                            b360BoxExist = true;
                        }
                    }
                }
            }
        } else {
            Log.i(getClass().getName(), "moov box does not exist");
            throw new IOException();
        }

        if (!b360BoxExist) {
            Log.i(getClass().getName(), "360 metadata does not exist");
            return false;
        }

        return true;
    }

    private UserBox findYoutoubeBox(Container c) {
        for (Box box : c.getBoxes()) {
            System.err.println(box.getType());
            if (box instanceof UserBox) {
                byte[] comparator = ((UserBox)box).getUserType();
                if(Arrays.equals(comparator, new byte[]{(byte) 0xff, (byte) 0xcc,
                        (byte) 0x82, (byte) 0x63, (byte) 0xf8, (byte) 0x55, (byte) 0x4a,
                        (byte) 0x93, (byte) 0x88, (byte) 0x14, (byte) 0x58, (byte) 0x7a,
                        (byte) 0x02, (byte) 0x52, (byte) 0x1f, (byte) 0xdd})) {

                    return (UserBox) box;
                }
            }
            if (box instanceof Container) {
                UserBox youtoubeBox = findYoutoubeBox((Container) box);
                if (youtoubeBox != null) {
                    return youtoubeBox;
                }
            }
        }
        return null;
    }
}
