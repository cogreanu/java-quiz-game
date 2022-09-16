package server.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Configuration
public class ActivityLoader {

    private static final String location = "activities";
    public static final String relativePath = "src/main/resources/";
    public static final String path = relativePath + location + "/";
    private final static Logger logger = LoggerFactory.getLogger(ActivityLoader.class);

    //Credits to StackOverflow user Oliv at https://stackoverflow.com/questions/10633595/java-zip-how-to-unzip-folder
    public static void unzip(InputStream is, Path targetDir) throws IOException {
        targetDir = targetDir.toAbsolutePath();
        try (ZipInputStream zipIn = new ZipInputStream(is)) {
            for (ZipEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
                Path resolvedPath = targetDir.resolve(ze.getName()).normalize();
                if (!resolvedPath.startsWith(targetDir)) {
                    // see: https://snyk.io/research/zip-slip-vulnerability
                    throw new RuntimeException("Entry with an illegal path: "
                            + ze.getName());
                }
                if (ze.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    try {
                        Files.copy(zipIn, resolvedPath);
                    } catch (FileAlreadyExistsException e) {
                        logger.debug("File " + ze.getName() + " already exists.");
                    }
                }
            }
        }
    }

    @Bean("dbInitializer")
    ApplicationRunner init(ActivityRepository repo) {
        try {
            unzip(new FileInputStream(relativePath + "activities.zip"), new File(relativePath + location).toPath());
            logger.info("Successfully unzipped activities.zip");
            // makeshift solution, likely not the best
            ObjectMapper mapper = new ObjectMapper();

            List<Activity> activities = Arrays.asList(mapper.readValue(Paths
                    .get(relativePath + location + "/activities.json").toFile(), Activity[].class));
            activities = activities.stream().filter(x -> x.source.length() < 150).collect(Collectors.toList());
            repo.saveAll(activities);
            logger.info("Activities added to repo");
            logger.info("Activity count is at " + repo.count());

        } catch (IOException e) {
            logger.warn("Activities.zip is missing (or something else went wrong while loading activities.zip)");
            logger.info("It should be located at resources/activities.zip");
            logger.info(relativePath + "activities.zip");
            logger.info(relativePath + location);
            logger.info(System.getProperty("user.dir"));
        } catch (IllegalArgumentException ex) {
            logger.error("Something went wrong while loading activities.json");
        }

        return args -> {

        };
    }
}
