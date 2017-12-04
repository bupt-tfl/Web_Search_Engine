# Web Search Engine - Readme
* this is a repo for web search engine course. Will contains some really interesting project about:
**web_crawler**, **index_builder** and **query_handler**
***
## Features
* Read pages from commoncrawl's wet files and create inverted index structures, plus structures for the lexicon and for the page (or docID-to-URL) table
* Compress inverted list structures to binary format
* Read user queries via simple command line prompt, and return the URL of each result, its BM25 score, and some snippet text with the context of the term occurrences in the pages.

## Run Program
* Download the wet files from commoncrawl and use InvertedIndexBuilder to build the temporary inverted index file and the pagetable.
* Use Unix sort command to merge and sort those index files and pagetables.
* Use the LexiconBuilder to build lexicon and compress the inverted index file to binary format
* Use the PagaTableLoader and LexcionLoader to load pagetable and lexicon from disk to main memory
* Use QueryProcessor to read queries via simple command line prompt and start searching

## Internal Process
* When given a commoncrawl's wet file which contains about 40000 web pages(ArchiveRecord), we use an ArchiveReader to read them. For each page, we create a unique id then we poll its url and page length from its header, and put them into a hashmap named pageTable. Then, we split the page's conent into terms and abandon those who are not english words or number. For each term, we put it into a hashmap, inverted index. To do this, we have to check out whether we already have this term in the inverted index. If not, we create a new index for this term and put it into the map. We take out the term's index and check if we have the docId in this index. If not, we create a new doc-freq index. Otherwise, we get the doc-freq index and increase the freq by one. We go through all wet files priovided and sometimes when the inverted index goes too big, we write it to file, and so does pageTable.This can take a lot of time.
* For those temporary inverted index files, we use unix merge and sort to put it into a large file. And so does pageTable files.
* Then, we are going to compress the large inverted index file using variable byte code. For each term and its corresponding doc-freq index, we calculate gaps between docs and compress them to a byte array and write it to file. We record this array's offset in the file as well as the legth in the lexicon.
* To process queries, we have to load the lexicon and pageTable into main memory. This can be very tricky in Java, since the cost of memory for a hashmap in Java is (16 \* size) + (4 \* capacity) + memory for data. So, this can go very large and overwhelm your memory. My solution is to control the hashmap size and abandon terms that are so long that we might never use.
* When given queries, we can user either conjunctive or disjunctive approach to process them. The conjunctive approach is faster than the disjunctive one since it is going to find docs in intersection among queries. We will find the quries in lexicon and return their offset and length records. Then, we use a RandonAccessFile to get those encoded index. After that, for each query, I create an Index instance to store the encoded index and decode it for further use. And, put all Index instances into an inverted index instance. There is an better approach that do not decode the index until the time to use it and will save much memory.
* After we get those index, we are going to rank those pages using BM25. And put the results into a priority queue. When we are done, we poll the top 20 results and crawl these pages to find the snippet. Finally, we print their url, BM25 score as well as the snippets.

## Program Design
* The Program is designed into two parts: Build Inverted Index & Query Process. For each of them, I will talk about my design decision including programming language, data structures, major functions and modules.
#### Programming Language: Java
Java is one of the most popular programming language nowadays. One of the main reason t is widely used is that Java has such as Garbage Collection. This is really useful in this homework. Besides, Java’s IO is also powerful and easy to use. It’s new feature, it’s RandomAccessFile allow us to randomly read/write file. Java (the platform) has a very large and standard class library, some parts of which are very well written. Primitive types don't inherit from Object. This is a decision the language designers made on purpose, and never causes problems that can't be worked around. Still, it robs the less intelligent of us of that cost feeling of consistency.
### Inverted Index
* This part is mainly composed of InvertedIndexBuilder, LexiconBuilder and VariableByteCode.
#### Data Structures
##### Inverted Index
In computer science, an inverted index (also referred to as postings file or inverted file) is an index data structure storing a mapping from content, such as words or numbers, to its locations in a database file, or in a document or a set of documents (named in contrast to a Forward Index, which maps from documents to content). The final inverted Index file will be totally binary format. But in the mid process, I decide to add term into this structure for sort and merge.
##### Temporary Inverted Index
I used a hashmap to store these informations. The key of the map is the term (the words) and the value is also a map, which stores the document Id and the terms frequencies. Hashmap can cost a lot of memory in Java. It cost about 200m with a size of 200000 terms. So, we need to write it into a file regularly and clear the map. For one wet file, I generally output 4-5 times. And the total size of these files is 150 M – 200 M.
##### Final Inverted Index
The final inverted index file will be totally binary format and will be compressed using Variable Byte Code. So, the total size will be 4 to 5 times smaller than the temporary files. To read the final inverted index, we need the help of lexicon. The lexicon records the term and its start position in the final inverted index as well as the length. We read the bytes and decode it into original positions.
##### Lexicon
The lexicon records the term and its start position in the final inverted index as well as the length. It’s stored as ascii format. So, this will be a little large.
##### Page Table
PageTable stores the document Id as well as its url and length. It’s also stored as ascii format and also a little large.
#### Merge & Sort
This part I decides to use Unix shell. Unix shell is a powerful shell with Very efficient virtual memory, so many programs can run with a modest amount of physical memory. It has a rich set of small commands and utilities that do specific tasks well. Besides, the speed in shell operation is really fast. It just costs about 7-8 minutes to sort and merge a total amount of 10 g files.
#### Class and Function
##### InvertedIndexBuilder
1. This class can read wet files and output pageTable and temporary inverted index. It has two functions: buildIndex and writePageTable.
2. **buildIndex( String dataPath, String indexPath, String …files)**
        return type	void
        throw IOExceptions.
This function will read wet files from the dataPath and output the inverted index files to the indexPath, as well as the pagetable.

3. **writePateTable(String Indexpath)**
        return type 	void
This function is to write the pageTable from HashMap to files using a StringBuilder.
##### InvertedIndexWriter
1. This is a wrapper for writing temporary inverted index files. And only has one function: writeInvertedIndex.
2. **writeInvertedIndex(Map<String, Map<Integer, Integer>> index, String filePath)**
        return type 	void.
This function will write an inverted index into a file and print the time used to write. I’ve tried several ways to do this. The one that using ObjecOutputStream and ByteArrayOutputStream has the fastest speed (about 5 times) but will write some object information to the file (will have about more 10 bytes). Finally I just used FileOutputStream. It’s more straight forward.
##### VariableByteCode
1. This class if for index compression and has three functions: encodeNumber, encode and decode.
2. **encodeNumber(int n)**
        return type 	byte[]
This function compresses an integer into bytes.
3. **encode(List<Integer> numbers)**
        return type	byte[]
This function compresses a list of integers into bytes.
4. **decode(byte[] byteStream)**
        return type	List<Integer>
This function decodes a byte array into list of integers.
##### LexiconBuilder
1. This class break the temporary inverted index file into lexicon and the final compressed inverted index.
2. **buildLexicon(String filePath, String indexFile)**
        return type	 void
        throw IOException
### Query Process
* This part is mainly composed of QueryProcessor, BM25, LexiconLoader, PageTableLoader, SnippetFinder.
#### Data Structures
* I create three data structures in this part, Index, IndexList and Doc.
##### Index
1. Index is compsed of a term, a doc-freq map, a sorted doc list and a position. It also implement nextGEQ and several other functions.
2. **Index(String term, Byte[] compressedIndex)**   
  Create an index instance and will decode the compressedIndex putting into the doc-freq map.
3. **nextGEQ(int k)**
        return type   int
Return the first posting in the index with docID at least k, when searching in a forward direction from the current position of the list pointer. Also, this function sets the position of the index to docId.
3. **getFreq()**
        return type   int
Return the frequency of the term at the postion.
4. **getFreq(int docId)**
        return type   int
Return the frequency of the term for given docId.
5. **contains(int docId)**
        return type   boolean
Check if we have this docId in this index.
6. **size()**
        return type   int
Return the number of docs in this index.
##### Index List
1. IndexList is basically a term-Index map, and implements openList and nextGEQ functions.
2. **openList(String term)**
        retrun type   Index
Return the term's corresponding Index.
3. **nextG(String term, int k)**
        return type   int
Return the first posting in this index with docID at least k, when searching in a forward direction from the current position of the list pointer.
##### Doc
1. Doc has a BM25 score and a docId, implementing getScore, getId function.
#### Class and Function
##### QueryProcessor
1. QueryProcessor is initialized with lexcion, pagaTable, indexReader and snippetFinder. It implements both conjunctiveProcess and disjunctiveProcess.
2. **QueryProcessor(List<HashMap<String, String>> lexicons, ArrayList<ArrayList<String>> pageTables, IndexReader indexReader)**
Create an QueryProcessor to execute disjunctiveProcess or conjunctiveProcess.
3. **parseQuery(String query)**
        return type   String[]
Parse the long query into several query terms.
4. **disjunctiveProcess(String ... queries)**
        return type   void
Taking in those parsed query terms, check if them exist in our lexicon. If so, open the indexes from inverted index file and put them into indexList. For all docs in the indexList, we calculate their BM25 score and rank them by a Priority Queue. Poll the top 20 pages with highest score, print out their url and score. Also, we print some snippet text with the context of the term occurrences in the pages by SnippetFinder.
5. **conjunctiveProcess(String ... queries)**
        return type   void
Taking in those parsed query terms, check if them exist in our lexicon. If anyone does not appear, we shall return. Open the indexes from inverted index file and put them into indexList. For all docs in the indexList, we are going to find the intersection among quries. If we can find the intersection, we will calculate these docs' BM25 score and rank them by a Priority Queue. Poll the top 20 pages with highest score, print out their url and score. Also, we print some snippet text with the context of the term occurrences in the pages by SnippetFinder.
6. **open(String ... queries)**
        return type   IndexList
        throws IOException
Search terms in lexicon, and read encoded index from file. Then create an Index instance and put it into the IndexList instance, and return the IndexList instance.
7. **searchTerm(String term)**
        return type   String
Search term in lexicon, if exist, return its offset and length.
8. **avgDocLen()**
        return type   double
Return avarage doc length of the whole collection.
9. **docNum()**
        return type   int
Return the number of docs of the whole collection.
10. **getUrl(int docId)**
        return type   String
Return the url of the page stored in pageTable.
11. **nextGEQ(IndexList indexList, String term, int k)**
        return type   int
Return the first posting in term's corresponding index with docID at least k, when searching in a forward direction from the current position of the list pointer.
##### PageTableLoader
* Given the file path, read the whole pagetable file into main memory and store it in an ArrayList. Since our pagetable is already sorted, given a docId, we can directly find the positon of data in the ArrayList. Also, the memory usage of ArrayList is smaller than HashMap in Java.
##### LexiconLoader
* Given the file path, read the whole lexicon file into main memory and store it in a term-to-offset,length HashMap. We have to control the size of a signal HashMap due to the limited memory. We might return a list of lexicon map.
##### IndexReader
1. **loadIndex(String indexPath)**
        return type   void
        throws FileNotFoundException
Given the file path, open a pipeline of the large inverted index file.
2. **readIndex(String coordinate)**
        return type byte[]
        throws IOException
Given the offset and length from lexicon, return the encoded index data.
##### BM25
1. Given quries and docId, calculate the BM25 score for the doc.
2. **setDocNum(int docNum)**
        return type   void
Set the N to the number of docs in the whole collection.
3. **setAvgDocLen(double avgDocLen)**
        return type   void
Set the avarage doc length in the whole collection for use of calculate K.
4. **calcScore(int docId)**
        return type   double
Return the BM25 score
##### SnippetFinder
1. **getSnippet(String strURL, String term)**
        return type   String
        throws IOException
Crawl the html file of the url and find snippets cotains the term.

## Performance
### Inverted Index
* 81 wet files(10.3 G) were processed. It contains 3332888 pages and 14132162 terms(only English words and numeric).
* The temporary inverted index file is about 9.84 GB. The pageTable file is about 275 M, the lexicon file is about 318 M and the final inverted index file is about 2.12 GB.
* The process to build temporary inverted index file takes about 140 mins. The sort and merge process takes about 40 mins. Finally, the process of building lexicon file and compressed inverted index file takes about 20 mins. The total time cost is 200 mins.

### Qurey Process
* Lexicon load time is about 30 seconds, and pageTable load time is about 15 seconds.
* Both conjunctive process and disjunctive process take less than 2 seconds to give results.

## Limitation
### Inverted Index
* When using unix merge and sort, it requires a unix shell. However, in windows, I have to use a terminal like cygwin to perform unix commands. And I didn’t figure out a way to use it in java. I think this can be down but need some more time.
* The second is the speed. Processing 1 wet file, say 40000 pages takes about 120 seconds. So, the speed is about 350 pages/s. And it definitely can do faster in java.

### Query Processor
* When open inverted index from file, the program decode it immediately and store it in a map, and also create a sorted list of docs. This will waste a lot of memory and slow down the whole program. Also, when the term's corresponding index contains too many docs, this kind of approach will kill the performance.
* The code does not seems very clean and should be refactored.
