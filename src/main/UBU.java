package main;

import imageLoader.SosachLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;

/**
 * Created by volhovm on 2/22/14.
 */
public class UBU {
    static final String VERSION_INFO =
            "UBU (Universal Board Utility) 0.1 dated 02.2014";
    static final String HELP_INFO =
            "\r\n\r\nUsage: run this program with java [-javaoptions] -jar UBU.jar [-options],\r\n" +
                    "where [-options] can be:\r\n" +
                    "   -ver or -v: display version of UBU and exit;\r\n" +
                    "   --help or -help or -h: display help info and exit;\r\n" +
                    "   -merge: merge all folders in /Pack/ into one and exit;\r\n" +
                    "   -thr [-extraargs] http://2ch.hk/$SECTION$/res/$THREADNUMBER1$.html " +
                    "http://2ch.hk/$SECTION$/res/$THREADNUMBER2$.html and so on:" +
                    " download all post pictures from threads into auto named folders;\r\n" +
                    "   -sec [-extraargs] /b/ /e/ and so on:" +
                    " download all post pictures from threads in this section (from page 0 to 10 actually);\r\n" +
                    "Now about [-extraargs] -- these can be:\r\n" +
                    "   -manual-naming: you will decide the name pictures from a certain thread will be put;\r\n" +
                    "   -section-pages X Y: pages from X to Y would only be parsed (works only after -sec);\r\n" +
                    "Example: java -jar UBU.jar -sec -section-pages 0 5 /wp/ -sec -thr -manual-naming http://2ch.hk/b/res/63223407.html\r\n" +
                    "The application saves all images in /Pack directory where UBU.jar is located.\r\n\r\n";
    static boolean section = false,
            thread = false,
            manualNaming = false,
            customPages = false;
    static short pageFrom = 0,
            pageTo = 10;


    public static void main(String[] args) throws Exception {

        //testing args
//        args = new String[]{"-thr", "http://2ch.hk/b/res/63619455.html", "-merge"};

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-ver")) {
                    System.out.println(VERSION_INFO);
                    return;
                } else if (args[i].equals("-v")) {
                    System.out.println(VERSION_INFO);
                    return;
                } else if (args[i].equals("--help")) {
                    System.out.println(HELP_INFO);
                    return;
                } else if (args[i].equals("-help")) {
                    System.out.println(HELP_INFO);
                    return;
                } else if (args[i].equals("-h")) {
                    System.out.println(HELP_INFO);
                    return;
                } else if (args[i].equals("-manual-naming")) {
                    if (thread || section) manualNaming = true;
                } else if (args[i].equals("-section-pages")) {
                    if (thread || section) {
                        customPages = true;
                        pageFrom = Short.parseShort(args[++i]);
                        pageTo = Short.parseShort(args[++i]);
                    }
                } else if (args[i].equals("-thr")) {
                    thread = true;
                    section = false;
                } else if (args[i].equals("-sec")) {
                    thread = false;
                    section = true;
                } else if (args[i].equals("-merge")) {
                    mergeMyPack();
                    return;
                } else {
                    if (thread) {
                        for (int j = i; j < args.length && !args[j].startsWith("-"); j++) {
                            File subFile = new File(
                                    System.getProperty("user.dir") + "/Pack/");
                            subFile.mkdir();
                            SosachLoader loadTask = new SosachLoader(new URL(args[j]), subFile.toString() + "/", !manualNaming);
                            loadTask.execute();
                            i = j;
                        }
                    } else if (section) {
                        for (int j = i; j < args.length && !args[j].startsWith("-"); j++) {
                            try {
                                SosachLoader currentLoader = new SosachLoader();
                                currentLoader.executeOnSection(args[i], pageFrom, pageTo, !manualNaming);
                            } catch (Exception exc) {
                                System.out.println(exc.toString() + "\r\n" + "~~~~~~You've done in wrong in -section-pages.");
                            }
                        }
                    } else throw new Exception("No options of -thr or -sec kind were found");
                    setDefaultVariables();
                }
            }
        } else
            throw new IOException("Wrong arguments, try --help");
    }

    private static void mergeMyPack() {
        File packDir = new File(
                System.getProperty("user.dir") + "/Pack/");
        packDir.mkdir();
        HashSet<File> filesToCopy = new HashSet<File>();
        filesToCopy = iterateThroughPack(packDir, filesToCopy);
        if (filesToCopy.size() >= 1) {
            File mergeDir = new File(
                    System.getProperty("user.dir") + "/Pack/Merged_" + System.currentTimeMillis() + "/");
            mergeDir.mkdir();
            for (File currentFile : filesToCopy) {
                try {
                    Files.move(currentFile.toPath(), new File(mergeDir.toString() + "/" + currentFile.getName()).toPath());
                    System.out.println("Successfully moved " + currentFile);
                } catch (IOException exc) {
                    System.out.println("Failed to move " + currentFile + " (already exists in merge dir)");
                }
            }
        } else {
            System.out.println("Your pack is empty, no data to merge.");
        }
    }

    private static HashSet<File> iterateThroughPack(File currentDir, HashSet<File> hashSet) {
        for (final File fileEntry : currentDir.listFiles()) {
            if (fileEntry.isFile()) {
                hashSet.add(fileEntry);
            } else {
                if (!fileEntry.toString().matches("^merged_[\\d]+$")) iterateThroughPack(fileEntry, hashSet);
            }
        }
        return hashSet;
    }

    private static void setDefaultVariables() {
        section = false;
        thread = false;
        manualNaming = false;
        customPages = false;
        pageFrom = 0;
        pageTo = 10;
    }
}
