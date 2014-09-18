Universal board utility
=======================

Usage: run this program with java [-javaoptions] -jar UBU.jar [-options],
where [-options] can be:
 
  * -ver or -v: display version of UBU and exit; 
  * --help or -help or -h: display help info and exit; 
  * -merge: merge all folders in /Pack/ into one and exit; 
  * -thr [-extraargs] http://2ch.hk/$SECTION$/res/$THREADNUMBER1$.html http://2ch.hk/$SECTION$/res/$THREADNUMBER2$.html and so on: download all post pictures from threads into auto named folders; 
  * -sec [-extraargs] /b/ /e/ and so on: download all post pictures from threads in this section (from page 0 to 10 actually);
  
Now about [-extraargs] -- these can be: 

  * -manual-naming: you will decide the name pictures from a certain thread will be put; 
  * -section-pages X Y: pages from X to Y would only be parsed (works only after -sec);
  
Example: java -jar UBU.jar -sec -section-pages 0 5 /wp/ -sec -thr -manual-naming http://2ch.hk/b/res/63223407.html 
The application saves all images in /Pack directory where UBU.jar is located.