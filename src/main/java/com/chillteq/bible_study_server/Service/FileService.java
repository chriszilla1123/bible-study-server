package com.chillteq.bible_study_server.Service;

import com.chillteq.bible_study_server.constant.Constants;
import com.chillteq.bible_study_server.model.Media;
import com.chillteq.bible_study_server.model.Playlist;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final File baseDirectory = new File(Constants.baseVideoDirectory);

    List<Playlist> playlists = new ArrayList<>();

    /*
     * Returns the cashed playlists list.
     */
    public List<Playlist> getMediaMetadata() {
        return playlists;
    }

    /*
     * Scans the baseDirectory to return a list of all available files, in their playlists.
     */
    @Scheduled(initialDelay = 3000, fixedRate = 60000)
    public List<Playlist> getPlaylistsFromDisk() {
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
                    try {
                        logger.info("Found file '{}'", mediaFile.getName());
                        Media media = new Media();
                        media.setName(mediaFile.getName());
                        media.setPlaylistName(playlist.getName());
                        media.setDuration(getMediaDuration(mediaFile));
                        mediaList.add(media);
                    } catch (Exception e) {
                        logger.error("Error processing file {} in playlist {} - will be skipped. message: {}", mediaFile.getName(), playlist.getName(), e.getMessage());
                    }
                }
                if (!mediaList.isEmpty()) {
                    Collections.sort(mediaList);
                    playlist.setMedia(mediaList);
                    list.add(playlist);
                }
            }
        }
        Collections.sort(list);
        this.playlists = list;
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
                            return mediaFile;
                        }
                    }
                }
            }
        }
        throw new FileNotFoundException("Requested playlist " + playlistName + " and media file " + mediaName +"  was not found");
    }

    public InputStream getFileInputStream(String dir) throws FileNotFoundException {
        return new FileInputStream(dir);
    }

    public Duration getMediaDuration(File mediaFile) {
        try {
            //JAudioTagger implementation
            AudioFile audioMetadata = AudioFileIO.read(mediaFile);
//            System.out.println("Audio Metadata "+ audioMetadata.displayStructureAsPlainText());
//            System.out.println(audioMetadata.getAudioHeader().getTrackLength());
//            System.out.println(audioMetadata.getAudioHeader().getBitRate());
            return Duration.ofSeconds(audioMetadata.getAudioHeader().getTrackLength());
        } catch (Exception e) {
            try {
                //ffprobe implementation
                ProcessBuilder pb = new ProcessBuilder(
                        "ffprobe",
                        "-v", "quiet",
                        "-print_format", "json",
                        "-show_format",
                        "-show_streams",
                        mediaFile.getAbsolutePath()
                );
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                process.waitFor();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonString.toString());
                JsonNode format = root.path("format");
                String durationStr = format.get("duration").asText();
                double durationInSeconds = Double.parseDouble(durationStr);

                logger.info("getMediaDuration - {} duration found to be {} seconds using ffprobe fallback", mediaFile.getName(), durationInSeconds);
                return Duration.ofSeconds(Math.round(durationInSeconds));

            } catch (Exception e2) {
                logger.error("getMediaDuration - Error while getting metadata for audio file. Error 1: {}", e.getLocalizedMessage());
                logger.error("getMediaDuration - Error while getting metadata for audio file using fallback. Error 2: {}", e2.getLocalizedMessage());
                throw new RuntimeException("Error while getting metadata for audio file. Error: " +  e.getLocalizedMessage());
            }
        }
    }
}
