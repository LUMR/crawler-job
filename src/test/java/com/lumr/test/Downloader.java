package com.lumr.test;

/**
 * Created by work on 2018/3/9.
 *
 * @author lumr
 */
public class Downloader {
    /*private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

    private final Parser parser;
    private final PageFetcher pageFetcher;
    private final CrawlConfig config = new CrawlConfig();

    public Downloader() throws InstantiationException, IllegalAccessException {
        config.setFollowRedirects(true);
        parser = new Parser(config);
        pageFetcher = new PageFetcher(config);
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Downloader downloader = new Downloader();
//        downloader.processUrl("https://en.wikipedia.org/wiki/Main_Page/");
//        downloader.processUrl("https://api.ipify.org?format=html");
        downloader.processUrl("https://www.zhihu.com/question/31427895");
    }

    public void processUrl(String url) {
        logger.info("Processing: {}", url);
        Page page = download(url);
        if (page != null) {
            ParseData parseData = page.getParseData();
            if (parseData != null) {
                if (parseData instanceof HtmlParseData) {
                    HtmlParseData htmlParseData = (HtmlParseData) parseData;
                    logger.info("Title: {}", htmlParseData.getTitle());
                    logger.info("Text length: {}", htmlParseData.getText().length());
                    logger.info("Html length: {}", htmlParseData.getHtml().length());
                }
            } else {
                logger.warn("Couldn't parse the content of the page.");
            }
        } else {
            logger.warn("Couldn't fetch the content of the page.");
        }
        logger.debug("==============");
    }

    private Page download(String url) {
        WebURL curURL = new WebURL();
        curURL.setURL(url);
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(curURL);
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
                Page page = new Page(curURL);
                fetchResult.fetchContent(page, config.getMaxDownloadSize());
                parser.parse(page, curURL.getURL());
                pageFetcher.shutDown();
                return page;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching url: " + curURL.getURL(), e);
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }
        return null;
    }*/
}
