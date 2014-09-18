package imageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by volhovm on 2/22/14.
 */
public class SosachLoader extends Loader {
    public SosachLoader(URL url, String path, boolean autoNaming) throws MalformedURLException {
        super(url, path, autoNaming);
    }

    public SosachLoader(URL url, String path) throws MalformedURLException {
        super(url, path);
    }

    public SosachLoader() {
        super();
    }

    public void executeOnSection(String currentSection, int pageFrom, int pageTo, boolean autoNaming) throws IOException {
        if (!currentSection.endsWith("/")) currentSection += "/";
        if (!currentSection.startsWith("/")) currentSection = "/" + currentSection;
        String[] sectionPages;
        sectionPages = new String[pageTo - pageFrom + 1];
        sectionPages[0] = "http://2ch.hk" + currentSection;
        for (int z = 1; z <= pageTo; z++) {
            sectionPages[z] = "http://2ch.hk" + currentSection + z + ".html?";
        }
        HashSet<String> threadLinksSet = new HashSet<String>();
        for (int k = pageFrom; k <= pageTo; k++) {
            org.jsoup.nodes.Document threads = Jsoup.connect(sectionPages[k]).timeout(10000).get();
            Elements threadLinks = threads.select("a[href~=^.+\\/res\\/[\\d]+.html$");
            for (Element threadLink : threadLinks) {
                threadLinksSet.add("http://2ch.hk" + threadLink.attr("href"));
            }
        }
        for (String currentLink : threadLinksSet) {
            File subFile = new File(
                    System.getProperty("user.dir") + "/Pack/");
            subFile.mkdir();
            this.downloadUrl = new URL(currentLink);
            this.dirPath = subFile.toString() + "/";
            this.autoNaming = autoNaming;
            this.execute();
        }
    }

    @Override
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
                if (downloadUrl.toString().startsWith("http://2ch.hk")) {
                    dirPath += fullDomain + "_" + document.title().split("\\s+")[document.title().split("\\s+").length - 1] + "_"
                            + System.currentTimeMillis() + "/";
                } else {
                    dirPath += fullDomain + "_" + document.title() + "_" + System.currentTimeMillis() + "/";
                }
            } else {
                String threadTitle = "";
                try {
                    threadTitle = document.select("span.subject").get(0).text();
                } catch (Exception c) {
                    System.out.println("Can't find thread name (" + downloadUrl + ")");
                }
                System.out.println("Provide the custom directory name for 'b" +
                        (!threadTitle.equals("") ? threadTitle : downloadUrl.toString()) +
                        "':");
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


}
