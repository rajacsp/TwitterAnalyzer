# TwitterAnalyzer
A tweet analyzer capable of performing a wide range of tasks such as identification, crawling, partitioning, sentiment analysis, co-occurrence analysis, web scraping & prediction.

## Identification
This task analyzes a dataset of gzip files and identifies tweets that contain one or more mentions in different formats (mention, replies, simple occurrences). 

<img src="screen/identification.png?raw=true" width="300"/>

## Crawling
This task crawls Twitter users' friendship relations and build a graph. It also calculates the pagerank of the nodes and determines the largest connected component.

<img src="screen/crawling.png?raw=true" width="150"/>

## Partitioning
In this task users are partitioned according to the mentions and some measures are calculated. Common examples of measures are frequency, influence, pagerank, closeness centrality.

<img src="screen/partitioning.png?raw=true" width="600"/>

## Sentiment Analysis
Users are classified into supporters, opponents and neutral according to the sentiment expressed in their tweets. The analysis consists of three steps:
1) Filtering
2) Normalization
3) Part-Of-Speech tagging & lemmatization
4) Sentiment polarity (SentiWordNet)
5) Linguistic rules

<img src="screen/sentiment.png?raw=true" width="150"/>

## Co-Occurrence Analysis
This task determines the most frequently co-occurring words using the Jaccard similarity coefficient.

<img src="screen/coccurrence2.png?raw=true" width="150"/>

 The analysis is performed using Lucene:
```java
coOccurrencesAnalysis(){
  for(term1: Index.highDocFreqTerms("tweet",5000)){
    freq=term1.docFreq
    if(freq>1){
      for(term2:terms){
        count=Index.search(term1,term2)
        if(count>1){
          jaccard=count/(term2.freq+freq-count)
          coOccs.add(term1,term2,jaccard)
        }
      }
      terms.add(term1)
    }
  }
  return coOccs
}
```

## Web Scraping

## Prediction

# Configuration
