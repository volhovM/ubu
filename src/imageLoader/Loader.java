package imageLoader;

import com.wizzardo.tools.io.FileTools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by volhovm on 3/1/14.
 */
public class Loader implements ILoader {
    Scanner scin = new Scanner(new InputStreamReader(System.in));
    URL downloadUrl;
    String dirPath;
    boolean autoNaming;

    public Loader(URL downloadUrl, String dirPath, boolean autoNaming) throws MalformedURLException {
        this.downloadUrl = downloadUrl;
        this.dirPath = dirPath;
        this.autoNaming = autoNaming;
    }

    public Loader(URL downloadUrl, String dirPath) throws MalformedURLException {
        this.downloadUrl = downloadUrl;
        this.dirPath = dirPath;
        autoNaming = true;
    }

    //Can be used only to download with downloadImage
    public Loader() {
        downloadUrl = null;
        dirPath = null;
        autoNaming = true;
    }

    public void execute() {
        try {
            System.out.print("Connecting to " + downloadUrl + " ...");
            Document document = Jsoup.connect(downloadUrl.toString()).timeout(10000).get();
            System.out.println(" Connected!");
            Elements linkPics = document.select("a[href~=^.+\\.(jpg|png|jpeg|gif)$]");

            //naming
            String fullDomain = downloadUrl.toString().substring(
                    downloadUrl.toString().indexOf("://") + 3,
                    downloadUrl.toString().indexOf("/", downloadUrl.toString().indexOf("://") + 3));
            if (autoNaming) {
                dirPath += fullDomain + "_" + document.title() + "_" + System.currentTimeMillis() + "/";
            } else {
                System.out.println("Provide the custom directory name for '" + downloadUrl.toString() + "':");
                fullDomain = scin.nextLine();
                fullDomain = fullDomain.replace(" ", "_");
                if (!fullDomain.endsWith("/")) fullDomain += "/";
                if (fullDomain.startsWith("/")) fullDomain = fullDomain.substring(1);
                dirPath += fullDomain;
                System.out.println("The files will be saved in " + dirPath);
            }

            System.out.println("------------------Starting iterating through the page-------------------\n");
            HashSet<String> linkSet = new HashSet<String>();
            for (Element s : linkPics) {
                linkSet.add(s.attr("href"));
            }
            int downloadCounter = 1;
            for (String s : linkSet) {
                System.out.print(downloadCounter++ + ": ");
                downloadImageP(s);
            }
            System.out.println("\n--------------------------------Done-----------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void downloadImageP(String imgSrc) throws IOException {
        try {
            String url;
            if (!(imgSrc.startsWith("http"))) {
                url = downloadUrl.toString().substring(0, downloadUrl.toString().indexOf("/",
                        downloadUrl.toString().indexOf("://") + 3)) + imgSrc;
            } else {
                url = imgSrc;
            }
            imgSrc = imgSrc.substring(imgSrc.lastIndexOf("/") + 1);
            String imgPath;
            imgPath = dirPath + imgSrc + " ";
            File currentPic = new File(imgPath);
            if (!currentPic.isFile()) {
                new File(dirPath).mkdir();
                System.out.print("Downloading " + imgSrc + " --- ");

                FileTools.bytes(currentPic, com.wizzardo.tools.HttpClient.connect(url).get().asBytes());
                System.out.print("Got it \r\n");
                System.out.println("--- Saved as " + currentPic.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void downloadImage(String imgSrc, URL downloadUrl, File dirFile) throws IOException {
        downloadImage(imgSrc, downloadUrl, dirFile.toString());
    }

    public void downloadImage(String imgSrc, URL downloadUrl, String dirPath) throws IOException {
        try {
            String url;
            if (!(imgSrc.startsWith("http"))) {
                url = downloadUrl.toString().substring(0, downloadUrl.toString().indexOf("/",
                        downloadUrl.toString().indexOf("://") + 3)) + imgSrc;
            } else {
                url = imgSrc;
            }
            imgSrc = imgSrc.substring(imgSrc.lastIndexOf("/") + 1);
            String imgPath;
            imgPath = dirPath + imgSrc + " ";
            File currentPic = new File(imgPath);
            if (!currentPic.isFile()) {
                new File(dirPath).mkdir();
                System.out.print("Downloading " + imgSrc + " --- ");
                FileTools.bytes(currentPic, com.wizzardo.tools.HttpClient.connect(url).get().asBytes());

                System.out.print("Got it \r\n");
                System.out.println("--- Saved as " + currentPic.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
