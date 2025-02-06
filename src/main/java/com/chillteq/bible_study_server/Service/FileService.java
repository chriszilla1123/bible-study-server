package com.chillteq.bible_study_server.Service;

import com.chillteq.bible_study_server.constant.Constants;
import com.chillteq.bible_study_server.model.Media;
import com.chillteq.bible_study_server.model.Playlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private File baseDirectory = new File(Constants.baseVideoDirectory);

    /*
     * Returns a list of all available files, in their playlists.
     */
    public List<Playlist> getMediaMetadata() throws FileNotFoundException {
        logger.info("Using base directory {}", baseDirectory.getName());
        List<Playlist> list = new ArrayList<>();
        for(File folder: Objects.requireNonNull(baseDirectory.listFiles(File::isDirectory))) {
            logger.info("Checking folder {}", folder.getName());
            File[] files = folder.listFiles();
            if(null != files && files.length > 0) {
                Playlist playlist = new Playlist();
                playlist.setName(folder.getName());
                List<Media> mediaList = new ArrayList<>();
                for(File mediaFile: files) {
                    logger.info("Fould file " + mediaFile.getName());
                    Media media = new Media();
                    media.setName(mediaFile.getName());
                    mediaList.add(media);
                }
                playlist.setMedia(mediaList);
                list.add(playlist);
            }
        }
        return list;
    }

    public File getMediaByPlaylistNameAndMediaName(String playlistName, String mediaName) throws FileNotFoundException {
        logger.info("Searching for a playlist called {} containing a media called {}", playlistName, mediaName);
        for(File folder: Objects.requireNonNull(baseDirectory.listFiles(File::isDirectory))) {
            if(folder.getName().equals(playlistName)) {
                File[] files = folder.listFiles();
                if(null != files && files.length > 0) {
                    for(File mediaFile: files) {
                        if(mediaFile.getName().equals(mediaName)) {
//                            return new FileSystemResource(mediaFile);
                            return mediaFile;
                        }
                    }
                }
            }
        }
        throw new FileNotFoundException("Request playlist " + playlistName + " and media file " + mediaName +"  was not found");
    }

    public InputStream getFileInputStream(String dir) throws FileNotFoundException {
        return new FileInputStream(dir);
    }
}
